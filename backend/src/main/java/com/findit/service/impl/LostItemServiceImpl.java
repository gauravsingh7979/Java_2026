package com.findit.service.impl;

import com.findit.dto.common.PageResponse;
import com.findit.dto.item.ItemSearchFilter;
import com.findit.dto.item.LostItemRequest;
import com.findit.dto.item.LostItemResponse;
import com.findit.entity.ItemImage;
import com.findit.entity.LostItem;
import com.findit.entity.User;
import com.findit.enums.ItemStatus;
import com.findit.exception.ForbiddenException;
import com.findit.exception.ResourceNotFoundException;
import com.findit.mapper.ItemMapper;
import com.findit.repository.LostItemRepository;
import com.findit.repository.UserRepository;
import com.findit.repository.specification.ItemSpecifications;
import com.findit.security.SecurityUtils;
import com.findit.service.AuditService;
import com.findit.service.LostItemService;
import com.findit.service.MatchService;
import com.findit.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class LostItemServiceImpl implements LostItemService {

    private static final Logger log = LoggerFactory.getLogger(LostItemServiceImpl.class);

    private final LostItemRepository lostItemRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final MatchService matchService;
    private final ItemMapper itemMapper;
    private final AuditService auditService;

    public LostItemServiceImpl(LostItemRepository lostItemRepository,
                               UserRepository userRepository,
                               StorageService storageService,
                               MatchService matchService,
                               ItemMapper itemMapper,
                               AuditService auditService) {
        this.lostItemRepository = lostItemRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.matchService = matchService;
        this.itemMapper = itemMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public LostItemResponse createLostItem(LostItemRequest request, List<MultipartFile> images, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String trackingNumber = generateTrackingNumber();

        LostItem lostItem = LostItem.builder()
                .trackingNumber(trackingNumber)
                .user(user)
                .title(request.getTitle().trim())
                .category(request.getCategory())
                .description(request.getDescription().trim())
                .brand(request.getBrand() != null ? request.getBrand().trim() : null)
                .primaryColor(request.getPrimaryColor() != null ? request.getPrimaryColor().trim() : null)
                .dateLost(request.getDateLost())
                .locationLost(request.getLocationLost().trim())
                .distinctiveCharacteristics(request.getDistinctiveCharacteristics())
                .contactPreference(request.getContactPreference())
                .status(ItemStatus.ACTIVE)
                .build();

        if (images != null && !images.isEmpty()) {
            List<ItemImage> storedImages = storageService.storeImages(images);
            for (ItemImage img : storedImages) {
                lostItem.addImage(img);
            }
        }

        LostItem saved = lostItemRepository.save(lostItem);
        log.info("Created LostItem report id: {}, tracking: {}", saved.getId(), saved.getTrackingNumber());

        auditService.logAction(user, "CREATE_LOST_REPORT", "LostItem", saved.getId(),
                "Reported lost item: " + saved.getTitle(), null);

        matchService.runMatchingForLostItem(saved);

        return itemMapper.toLostItemResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LostItemResponse getLostItemById(Long id) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item report not found with id: " + id));
        return itemMapper.toLostItemResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<LostItemResponse> searchLostItems(ItemSearchFilter filter) {
        Sort sort = Sort.by(
                "ASC".equalsIgnoreCase(filter.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                filter.getSortBy() != null ? filter.getSortBy() : "createdAt"
        );
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Page<LostItem> page = lostItemRepository.findAll(ItemSpecifications.filterLostItems(filter), pageable);
        return PageResponse.from(page.map(itemMapper::toLostItemResponse));
    }

    @Override
    @Transactional
    public LostItemResponse updateLostItem(Long id, LostItemRequest request, Long userId) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with id: " + id));

        if (!item.getUser().getId().equals(userId) && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to update this report");
        }

        item.setTitle(request.getTitle().trim());
        item.setCategory(request.getCategory());
        item.setDescription(request.getDescription().trim());
        item.setBrand(request.getBrand() != null ? request.getBrand().trim() : null);
        item.setPrimaryColor(request.getPrimaryColor() != null ? request.getPrimaryColor().trim() : null);
        item.setDateLost(request.getDateLost());
        item.setLocationLost(request.getLocationLost().trim());
        item.setDistinctiveCharacteristics(request.getDistinctiveCharacteristics());
        item.setContactPreference(request.getContactPreference());

        LostItem saved = lostItemRepository.save(item);
        auditService.logAction("UPDATE_LOST_REPORT", "LostItem", saved.getId(), "Updated report: " + saved.getTitle());

        matchService.runMatchingForLostItem(saved);

        return itemMapper.toLostItemResponse(saved);
    }

    @Override
    @Transactional
    public void deleteLostItem(Long id, Long userId) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with id: " + id));

        if (!item.getUser().getId().equals(userId) && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to delete this report");
        }

        item.setStatus(ItemStatus.CANCELLED);
        lostItemRepository.save(item);
        auditService.logAction("CANCEL_LOST_REPORT", "LostItem", id, "Marked lost report as CANCELLED");
    }

    @Override
    @Transactional
    public LostItemResponse addImages(Long id, List<MultipartFile> images, Long userId) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with id: " + id));

        if (!item.getUser().getId().equals(userId) && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to add images to this report");
        }

        if (images != null && !images.isEmpty()) {
            List<ItemImage> storedImages = storageService.storeImages(images);
            for (ItemImage img : storedImages) {
                item.addImage(img);
            }
            lostItemRepository.save(item);
        }

        return itemMapper.toLostItemResponse(item);
    }

    private String generateTrackingNumber() {
        int year = LocalDate.now().getYear();
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "LOST-" + year + "-" + suffix;
    }
}
