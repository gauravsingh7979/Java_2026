package com.findit.repository;

import com.findit.entity.Claim;
import com.findit.enums.ClaimStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Page<Claim> findByClaimantIdOrderByCreatedAtDesc(Long claimantId, Pageable pageable);

    List<Claim> findByFoundItemId(Long foundItemId);

    Page<Claim> findByStatus(ClaimStatus status, Pageable pageable);

    boolean existsByFoundItemIdAndClaimantIdAndStatusIn(Long foundItemId, Long claimantId, Collection<ClaimStatus> statuses);

    long countByStatus(ClaimStatus status);

    @Query("SELECT c FROM Claim c WHERE c.foundItem.user.id = :finderId ORDER BY c.createdAt DESC")
    Page<Claim> findClaimsOnItemsFoundByUser(@Param("finderId") Long finderId, Pageable pageable);
}
