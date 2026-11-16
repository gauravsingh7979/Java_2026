package com.findit.service.impl;

import com.findit.dto.common.PageResponse;
import com.findit.dto.item.FoundItemRequest;
import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.ItemSearchFilter;
import com.findit.entity.FoundItem;
import com.findit.entity.ItemImage;
import com.findit.entity.User;
import com.findit.enums.ItemStatus;
import com.findit.exception.ForbiddenException;
import com.findit.exception.ResourceNotFoundException;
import com.findit.mapper.ItemMapper;
import com.findit.repository.FoundItemRepository;
import com.findit.repository.UserRepository;
import com.findit.repository.specification.ItemSpecifications;
import com.findit.security.SecurityUtils;
import com.findit.service.AuditService;
import com.findit.service.FoundItemService;
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
public class FoundItemServiceImpl implements FoundItemService {

    private static final Logger log = LoggerFactory.getLogger(FoundItemServiceImpl.class);

    private final FoundItemRepository foundItemRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final MatchService matchService;
    private final ItemMapper itemMapper;
    private final AuditService auditService;

    public FoundItemServiceImpl(FoundItemRepository foundItemRepository,
                                UserRepository userRepository,
                                StorageService storageService,
                                MatchService matchService,
                                ItemMapper itemMapper,
                                AuditService auditService) {
        this.foundItemRepository = foundItemRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.matchService = matchService;
        this.itemMapper = itemMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public FoundItemResponse createFoundItem(FoundItemRequest request, List<MultipartFile> images, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        String trackingNumber = generateTrackingNumber();

        FoundItem foundItem = FoundItem.builder()
                .trackingNumber(trackingNumber)
                .user(user)
                .title(request.getTitle().trim())
                .category(request.getCategory())
                .publicDescription(request.getPublicDescription().trim())
                .privateDetails(request.getPrivateDetails() != null ? request.getPrivateDetails().trim() : null)
                .brand(request.getBrand() != null ? request.getBrand().trim() : null)
                .primaryColor(request.getPrimaryColor() != null ? request.getPrimaryColor().trim() : null)
                .dateFound(request.getDateFound())
                .locationFound(request.getLocationFound().trim())
                .storageLocation(request.getStorageLocation() != null ? request.getStorageLocation().trim() : null)
                .status(ItemStatus.ACTIVE)
                .build();

        if (images != null && !images.isEmpty()) {
            List<ItemImage> storedImages = storageService.storeImages(images);
            for (ItemImage img : storedImages) {
                foundItem.addImage(img);
            }
        }

        FoundItem saved = foundItemRepository.save(foundItem);
        log.info("Created FoundItem report id: {}, tracking: {}", saved.getId(), saved.getTrackingNumber());

        auditService.logAction(user, "CREATE_FOUND_REPORT", "FoundItem", saved.getId(),
                "Reported found item: " + saved.getTitle(), null);

        matchService.runMatchingForFoundItem(saved);

        return itemMapper.toFoundItemResponse(saved, true);
    }

    @Override
    @Transactional(readOnly = true)
    public FoundItemResponse getFoundItemById(Long id, Long currentUserId) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item report not found with id: " + id));

        boolean canSeePrivateDetails = (currentUserId != null && item.getUser().getId().equals(currentUserId))
                || SecurityUtils.isAdminOrSuperAdmin();

        return itemMapper.toFoundItemResponse(item, canSeePrivateDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FoundItemResponse> searchFoundItems(ItemSearchFilter filter) {
        Sort sort = Sort.by(
                "ASC".equalsIgnoreCase(filter.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC,
                filter.getSortBy() != null ? filter.getSortBy() : "createdAt"
        );
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Page<FoundItem> page = foundItemRepository.findAll(ItemSpecifications.filterFoundItems(filter), pageable);

        return PageResponse.from(page.map(item -> itemMapper.toFoundItemResponse(item, false)));
    }

    @Override
    @Transactional
    public FoundItemResponse updateFoundItem(Long id, FoundItemRequest request, Long userId) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));

        if (!item.getUser().getId().equals(userId) && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to update this report");
        }

        item.setTitle(request.getTitle().trim());
        item.setCategory(request.getCategory());
        item.setPublicDescription(request.getPublicDescription().trim());
        if (request.getPrivateDetails() != null) {
            item.setPrivateDetails(request.getPrivateDetails().trim());
        }
        item.setBrand(request.getBrand() != null ? request.getBrand().trim() : null);
        item.setPrimaryColor(request.getPrimaryColor() != null ? request.getPrimaryColor().trim() : null);
        item.setDateFound(request.getDateFound());
        item.setLocationFound(request.getLocationFound().trim());
        item.setStorageLocation(request.getStorageLocation() != null ? request.getStorageLocation().trim() : null);

        FoundItem saved = foundItemRepository.save(item);
        auditService.logAction("UPDATE_FOUND_REPORT", "FoundItem", saved.getId(), "Updated report: " + saved.getTitle());

        matchService.runMatchingForFoundItem(saved);

        return itemMapper.toFoundItemResponse(saved, true);
    }

    @Override
    @Transactional
    public void deleteFoundItem(Long id, Long userId) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));

        if (!item.getUser().getId().equals(userId) && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to delete this report");
        }

        item.setStatus(ItemStatus.CANCELLED);
        foundItemRepository.save(item);
        auditService.logAction("CANCEL_FOUND_REPORT", "FoundItem", id, "Marked found report as CANCELLED");
    }

    @Override
    @Transactional
    public FoundItemResponse addImages(Long id, List<MultipartFile> images, Long userId) {
        FoundItem item = foundItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + id));

        if (!item.getUser().getId().equals(userId) && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to add images to this report");
        }

        if (images != null && !images.isEmpty()) {
            List<ItemImage> storedImages = storageService.storeImages(images);
            for (ItemImage img : storedImages) {
                item.addImage(img);
            }
            foundItemRepository.save(item);
        }

        return itemMapper.toFoundItemResponse(item, true);
    }

    private String generateTrackingNumber() {
        int year = LocalDate.now().getYear();
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "FND-" + year + "-" + suffix;
    }
}
