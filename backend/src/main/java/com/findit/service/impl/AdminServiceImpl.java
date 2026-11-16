package com.findit.service.impl;

import com.findit.dto.admin.AdminDashboardStatsDto;
import com.findit.dto.admin.AuditLogResponse;
import com.findit.dto.common.PageResponse;
import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.LostItemResponse;
import com.findit.dto.user.UserSummaryDto;
import com.findit.enums.ClaimStatus;
import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;
import com.findit.enums.MatchClassification;
import com.findit.enums.RecoveryStatus;
import com.findit.mapper.ItemMapper;
import com.findit.repository.*;
import com.findit.service.AdminService;
import com.findit.service.AuditService;
import com.findit.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final MatchRepository matchRepository;
    private final ClaimRepository claimRepository;
    private final RecoveryRepository recoveryRepository;
    private final UserService userService;
    private final AuditService auditService;
    private final ItemMapper itemMapper;

    public AdminServiceImpl(UserRepository userRepository,
                            LostItemRepository lostItemRepository,
                            FoundItemRepository foundItemRepository,
                            MatchRepository matchRepository,
                            ClaimRepository claimRepository,
                            RecoveryRepository recoveryRepository,
                            UserService userService,
                            AuditService auditService,
                            ItemMapper itemMapper) {
        this.userRepository = userRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.matchRepository = matchRepository;
        this.claimRepository = claimRepository;
        this.recoveryRepository = recoveryRepository;
        this.userService = userService;
        this.auditService = auditService;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActiveTrue();

        long totalLost = lostItemRepository.count();
        long activeLost = lostItemRepository.countByStatus(ItemStatus.ACTIVE);

        long totalFound = foundItemRepository.count();
        long activeFound = foundItemRepository.countByStatus(ItemStatus.ACTIVE);

        long totalMatches = matchRepository.count();
        long strongMatches = matchRepository.countByClassification(MatchClassification.STRONG);

        long pendingClaims = claimRepository.countByStatus(ClaimStatus.PENDING);
        long approvedClaims = claimRepository.countByStatus(ClaimStatus.APPROVED);

        long totalRecoveries = recoveryRepository.count();
        long completedRecoveries = recoveryRepository.countByStatus(RecoveryStatus.COMPLETED);

        double recoveryRate = (totalLost + totalFound) > 0 ?
                Math.round(((double) (completedRecoveries * 2) / (totalLost + totalFound)) * 100.0 * 10.0) / 10.0 : 0.0;

        Map<String, Long> categoryCounts = new HashMap<>();
        for (ItemCategory cat : ItemCategory.values()) {
            long count = lostItemRepository.count((root, query, cb) -> cb.equal(root.get("category"), cat)) +
                         foundItemRepository.count((root, query, cb) -> cb.equal(root.get("category"), cat));
            categoryCounts.put(cat.name(), count);
        }

        return AdminDashboardStatsDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalLostReports(totalLost)
                .activeLostReports(activeLost)
                .totalFoundReports(totalFound)
                .activeFoundReports(activeFound)
                .totalMatches(totalMatches)
                .strongMatches(strongMatches)
                .pendingClaims(pendingClaims)
                .approvedClaims(approvedClaims)
                .totalRecoveries(totalRecoveries)
                .completedRecoveries(completedRecoveries)
                .recoveryRate(recoveryRate)
                .reportsByCategory(categoryCounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LostItemResponse> getAllLostReportsForModeration() {
        return lostItemRepository.findAll().stream()
                .map(itemMapper::toLostItemResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoundItemResponse> getAllFoundReportsForModeration() {
        return foundItemRepository.findAll().stream()
                .map(f -> itemMapper.toFoundItemResponse(f, true))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserSummaryDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @Override
    @Transactional
    public void setUserActiveStatus(Long userId, boolean active) {
        userService.updateUserStatus(userId, active);
    }

    @Override
    @Transactional
    public void updateUserRole(Long userId, com.findit.enums.Role role) {
        userService.updateUserRole(userId, role);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditService.getAuditLogs(pageable);
    }
}
