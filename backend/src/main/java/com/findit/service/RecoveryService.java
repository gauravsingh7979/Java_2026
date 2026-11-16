package com.findit.service;

import com.findit.dto.common.PageResponse;
import com.findit.dto.recovery.ConfirmHandoverRequest;
import com.findit.dto.recovery.RecoveryResponse;
import com.findit.dto.recovery.ScheduleHandoverRequest;
import com.findit.entity.Claim;
import com.findit.entity.Recovery;
import org.springframework.data.domain.Pageable;

public interface RecoveryService {

    Recovery initiateRecovery(Claim approvedClaim);

    RecoveryResponse scheduleHandover(Long recoveryId, ScheduleHandoverRequest request, Long userId);

    RecoveryResponse confirmHandover(Long recoveryId, ConfirmHandoverRequest request, Long userId);

    PageResponse<RecoveryResponse> getUserRecoveries(Long userId, Pageable pageable);

    RecoveryResponse getRecoveryById(Long recoveryId, Long userId);
}
