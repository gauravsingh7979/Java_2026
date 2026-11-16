package com.findit.repository;

import com.findit.entity.Recovery;
import com.findit.enums.RecoveryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecoveryRepository extends JpaRepository<Recovery, Long> {

    Optional<Recovery> findByRecoveryCode(String recoveryCode);

    Optional<Recovery> findByClaimId(Long claimId);

    @Query("SELECT r FROM Recovery r WHERE r.claimant.id = :userId OR r.finder.id = :userId ORDER BY r.createdAt DESC")
    Page<Recovery> findUserRecoveries(@Param("userId") Long userId, Pageable pageable);

    long countByStatus(RecoveryStatus status);
}
