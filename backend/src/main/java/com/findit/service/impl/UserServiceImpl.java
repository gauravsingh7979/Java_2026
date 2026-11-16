package com.findit.service.impl;

import com.findit.dto.common.PageResponse;
import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.LostItemResponse;
import com.findit.dto.item.UserReportsSummaryDto;
import com.findit.dto.user.UserProfileResponse;
import com.findit.dto.user.UserSummaryDto;
import com.findit.dto.user.UserUpdateRequest;
import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.entity.User;
import com.findit.enums.ClaimStatus;
import com.findit.enums.MatchStatus;
import com.findit.exception.ResourceNotFoundException;
import com.findit.mapper.ItemMapper;
import com.findit.repository.ClaimRepository;
import com.findit.repository.FoundItemRepository;
import com.findit.repository.LostItemRepository;
import com.findit.repository.UserRepository;
import com.findit.service.AuditService;
import com.findit.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final ClaimRepository claimRepository;
    private final ItemMapper itemMapper;
    private final AuditService auditService;

    public UserServiceImpl(UserRepository userRepository,
                           LostItemRepository lostItemRepository,
                           FoundItemRepository foundItemRepository,
                           ClaimRepository claimRepository,
                           ItemMapper itemMapper,
                           AuditService auditService) {
        this.userRepository = userRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.claimRepository = claimRepository;
        this.itemMapper = itemMapper;
        this.auditService = auditService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        User user = getUser(userId);
        List<LostItem> lostItems = lostItemRepository.findByUserId(userId);
        List<FoundItem> foundItems = foundItemRepository.findByUserId(userId);

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.isActive())
                .verified(user.isVerified())
                .createdAt(user.getCreatedAt())
                .totalLostReports(lostItems.size())
                .totalFoundReports(foundItems.size())
                .totalClaims(claimRepository.findByClaimantIdOrderByCreatedAtDesc(userId, Pageable.unpaged()).getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserUpdateRequest request) {
        User user = getUser(userId);
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }
        User saved = userRepository.save(user);

        auditService.logAction(saved, "USER_PROFILE_UPDATED", "User", saved.getId(), "Updated profile details", null);

        return getCurrentUserProfile(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserReportsSummaryDto getUserReportsSummary(Long userId) {
        List<LostItem> lostList = lostItemRepository.findByUserId(userId);
        List<FoundItem> foundList = foundItemRepository.findByUserId(userId);

        List<LostItemResponse> lostResponses = lostList.stream()
                .map(itemMapper::toLostItemResponse)
                .collect(Collectors.toList());

        List<FoundItemResponse> foundResponses = foundList.stream()
                .map(f -> itemMapper.toFoundItemResponse(f, true))
                .collect(Collectors.toList());

        int activeMatchesCount = lostList.stream()
                .mapToInt(l -> (int) l.getMatches().stream().filter(m -> m.getStatus() != MatchStatus.DISMISSED).count())
                .sum();

        int pendingClaimsCount = foundList.stream()
                .mapToInt(f -> (int) f.getClaims().stream().filter(c -> c.getStatus() == ClaimStatus.PENDING).count())
                .sum();

        return UserReportsSummaryDto.builder()
                .lostReports(lostResponses)
                .foundReports(foundResponses)
                .totalLost(lostResponses.size())
                .totalFound(foundResponses.size())
                .activeMatches(activeMatchesCount)
                .pendingClaims(pendingClaimsCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserSummaryDto> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return PageResponse.from(page.map(this::toSummaryDto));
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, boolean active) {
        User user = getUser(userId);
        user.setActive(active);
        userRepository.save(user);
        auditService.logAction("USER_STATUS_CHANGE", "User", userId, "Changed active status to: " + active);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, com.findit.enums.Role role) {
        User user = getUser(userId);
        user.setRole(role);
        userRepository.save(user);
        auditService.logAction("USER_ROLE_CHANGE", "User", userId, "Changed user role to: " + role.name());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private UserSummaryDto toSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
