package com.findit.repository;

import com.findit.entity.FoundItem;
import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long>, JpaSpecificationExecutor<FoundItem> {

    Optional<FoundItem> findByTrackingNumber(String trackingNumber);

    Page<FoundItem> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<FoundItem> findByUserId(Long userId);

    long countByStatus(ItemStatus status);

    @Query("SELECT f FROM FoundItem f WHERE f.status = :status AND f.category = :category")
    List<FoundItem> findActiveCandidatesByCategory(@Param("status") ItemStatus status, @Param("category") ItemCategory category);

    @Query("SELECT f FROM FoundItem f WHERE f.status = 'ACTIVE'")
    List<FoundItem> findAllActiveItems();
}
