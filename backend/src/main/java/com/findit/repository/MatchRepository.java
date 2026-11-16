package com.findit.repository;

import com.findit.entity.Match;
import com.findit.enums.MatchClassification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByLostItemIdOrderByMatchScoreDesc(Long lostItemId);

    List<Match> findByFoundItemIdOrderByMatchScoreDesc(Long foundItemId);

    Optional<Match> findByLostItemIdAndFoundItemId(Long lostItemId, Long foundItemId);

    boolean existsByLostItemIdAndFoundItemId(Long lostItemId, Long foundItemId);

    @Query("SELECT m FROM Match m WHERE m.lostItem.user.id = :userId OR m.foundItem.user.id = :userId ORDER BY m.createdAt DESC")
    Page<Match> findMatchesForUser(@Param("userId") Long userId, Pageable pageable);

    long countByClassification(MatchClassification classification);
}
