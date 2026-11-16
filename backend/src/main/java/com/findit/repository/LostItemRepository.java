package com.findit.repository;

import com.findit.entity.LostItem;
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
public interface LostItemRepository extends JpaRepository<LostItem, Long>, JpaSpecificationExecutor<LostItem> {

    Optional<LostItem> findByTrackingNumber(String trackingNumber);

    Page<LostItem> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<LostItem> findByUserId(Long userId);

    long countByStatus(ItemStatus status);

    @Query("SELECT l FROM LostItem l WHERE l.status = :status AND l.category = :category")
    List<LostItem> findActiveCandidatesByCategory(@Param("status") ItemStatus status, @Param("category") ItemCategory category);

    @Query("SELECT l FROM LostItem l WHERE l.status = 'ACTIVE'")
    List<LostItem> findAllActiveItems();
}
