package com.findit.service;

import com.findit.dto.common.PageResponse;
import com.findit.dto.item.UserReportsSummaryDto;
import com.findit.dto.user.UserProfileResponse;
import com.findit.dto.user.UserSummaryDto;
import com.findit.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserProfileResponse getCurrentUserProfile(Long userId);

    UserProfileResponse updateUserProfile(Long userId, UserUpdateRequest request);

    UserReportsSummaryDto getUserReportsSummary(Long userId);

    PageResponse<UserSummaryDto> getAllUsers(Pageable pageable);

    void updateUserStatus(Long userId, boolean active);

    void updateUserRole(Long userId, com.findit.enums.Role role);
}
