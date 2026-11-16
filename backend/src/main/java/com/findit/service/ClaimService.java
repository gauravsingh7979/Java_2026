package com.findit.service;

import com.findit.dto.claim.ClaimRequest;
import com.findit.dto.claim.ClaimResponse;
import com.findit.dto.claim.ClaimReviewRequest;
import com.findit.dto.common.PageResponse;
import com.findit.enums.ClaimStatus;
import org.springframework.data.domain.Pageable;

public interface ClaimService {

    ClaimResponse submitClaim(ClaimRequest request, Long claimantId);

    PageResponse<ClaimResponse> getMyClaims(Long claimantId, Pageable pageable);

    ClaimResponse getClaimById(Long claimId, Long currentUserId);

    PageResponse<ClaimResponse> getClaimsByStatus(ClaimStatus status, Pageable pageable);

    ClaimResponse markUnderReview(Long claimId, Long adminId);

    ClaimResponse approveClaim(Long claimId, ClaimReviewRequest request, Long adminId);

    ClaimResponse rejectClaim(Long claimId, ClaimReviewRequest request, Long adminId);
}
