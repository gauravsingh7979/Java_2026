package com.findit.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findit.dto.match.MatchResponse;
import com.findit.dto.match.MatchScoreBreakdownDto;
import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.entity.Match;
import com.findit.enums.ItemStatus;
import com.findit.enums.MatchClassification;
import com.findit.enums.MatchStatus;
import com.findit.exception.ForbiddenException;
import com.findit.exception.ResourceNotFoundException;
import com.findit.mapper.ItemMapper;
import com.findit.matching.MatchScoringResult;
import com.findit.matching.MatchingEngine;
import com.findit.repository.FoundItemRepository;
import com.findit.repository.LostItemRepository;
import com.findit.repository.MatchRepository;
import com.findit.security.SecurityUtils;
import com.findit.service.MatchService;
import com.findit.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchServiceImpl.class);

    private final MatchRepository matchRepository;
    private final LostItemRepository lostItemRepository;
    private final FoundItemRepository foundItemRepository;
    private final MatchingEngine matchingEngine;
    private final NotificationService notificationService;
    private final ItemMapper itemMapper;
    private final ObjectMapper objectMapper;

    private static final double MIN_MATCH_SCORE_THRESHOLD = 50.0;

    public MatchServiceImpl(MatchRepository matchRepository,
                            LostItemRepository lostItemRepository,
                            FoundItemRepository foundItemRepository,
                            MatchingEngine matchingEngine,
                            NotificationService notificationService,
                            ItemMapper itemMapper,
                            ObjectMapper objectMapper) {
        this.matchRepository = matchRepository;
        this.lostItemRepository = lostItemRepository;
        this.foundItemRepository = foundItemRepository;
        this.matchingEngine = matchingEngine;
        this.notificationService = notificationService;
        this.itemMapper = itemMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Async
    @Transactional
    public void runMatchingForLostItem(LostItem lostItem) {
        if (lostItem.getStatus() != ItemStatus.ACTIVE) {
            return;
        }

        List<FoundItem> candidates = foundItemRepository.findActiveCandidatesByCategory(ItemStatus.ACTIVE, lostItem.getCategory());
        log.info("Running matching engine for LostItem id: {} against {} active found candidates", lostItem.getId(), candidates.size());

        for (FoundItem foundItem : candidates) {
            processMatchPair(lostItem, foundItem);
        }
    }

    @Override
    @Async
    @Transactional
    public void runMatchingForFoundItem(FoundItem foundItem) {
        if (foundItem.getStatus() != ItemStatus.ACTIVE) {
            return;
        }

        List<LostItem> candidates = lostItemRepository.findActiveCandidatesByCategory(ItemStatus.ACTIVE, foundItem.getCategory());
        log.info("Running matching engine for FoundItem id: {} against {} active lost candidates", foundItem.getId(), candidates.size());

        for (LostItem lostItem : candidates) {
            processMatchPair(lostItem, foundItem);
        }
    }

    private void processMatchPair(LostItem lostItem, FoundItem foundItem) {
        MatchScoringResult result = matchingEngine.computeMatch(lostItem, foundItem);

        if (result.getTotalScore() >= MIN_MATCH_SCORE_THRESHOLD) {
            Optional<Match> existingOpt = matchRepository.findByLostItemIdAndFoundItemId(lostItem.getId(), foundItem.getId());
            boolean isNewMatch = existingOpt.isEmpty();

            Match match = existingOpt.orElseGet(() -> Match.builder()
                    .lostItem(lostItem)
                    .foundItem(foundItem)
                    .status(MatchStatus.UNVIEWED)
                    .build());

            match.setMatchScore(result.getTotalScore());
            match.setClassification(result.getClassification());

            try {
                match.setMatchReasons(objectMapper.writeValueAsString(result.getScoreBreakdown()));
            } catch (Exception e) {
                match.setMatchReasons(String.join("; ", result.getReasons()));
            }

            matchRepository.save(match);
            log.info("Match pair recorded: Lost #{} & Found #{} -> Score: {} ({})",
                    lostItem.getId(), foundItem.getId(), result.getTotalScore(), result.getClassification());

            if (isNewMatch) {
                sendMatchNotifications(lostItem, foundItem, result);
            }
        }
    }

    private void sendMatchNotifications(LostItem lost, FoundItem found, MatchScoringResult result) {
        notificationService.createNotification(
                lost.getUser(),
                "Potential Match Found (" + result.getClassification() + ")",
                result.getSummaryReason(),
                "MATCH_FOUND",
                "/matches?lostId=" + lost.getId()
        );

        notificationService.createNotification(
                found.getUser(),
                "Matching Lost Report Discovered",
                "A lost report '" + lost.getTitle() + "' was paired with your found item '" + found.getTitle() + "' with a score of " + result.getTotalScore() + "/100.",
                "MATCH_FOUND",
                "/matches?foundId=" + found.getId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesForLostItem(Long lostItemId, Long currentUserId) {
        LostItem lost = lostItemRepository.findById(lostItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Lost item not found with id: " + lostItemId));

        if (!lost.getUser().getId().equals(currentUserId) && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to view matches for this item");
        }

        List<Match> matches = matchRepository.findByLostItemIdOrderByMatchScoreDesc(lostItemId);
        return matches.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesForFoundItem(Long foundItemId, Long currentUserId) {
        FoundItem found = foundItemRepository.findById(foundItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + foundItemId));

        if (!found.getUser().getId().equals(currentUserId) && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to view matches for this item");
        }

        List<Match> matches = matchRepository.findByFoundItemIdOrderByMatchScoreDesc(foundItemId);
        return matches.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void recalculateAll() {
        log.info("Admin initiated full match recalculation");
        List<LostItem> allLost = lostItemRepository.findAllActiveItems();
        List<FoundItem> allFound = foundItemRepository.findAllActiveItems();

        for (LostItem lost : allLost) {
            for (FoundItem found : allFound) {
                if (lost.getCategory().equals(found.getCategory())) {
                    processMatchPair(lost, found);
                }
            }
        }
    }

    @Override
    @Transactional
    public void dismissMatch(Long matchId, Long currentUserId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));

        boolean isOwner = match.getLostItem().getUser().getId().equals(currentUserId) ||
                          match.getFoundItem().getUser().getId().equals(currentUserId);

        if (!isOwner && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("Not authorized to dismiss this match");
        }

        match.setStatus(MatchStatus.DISMISSED);
        matchRepository.save(match);
    }

    private MatchResponse mapToResponse(Match m) {
        MatchScoreBreakdownDto breakdown = null;
        try {
            breakdown = objectMapper.readValue(m.getMatchReasons(), MatchScoreBreakdownDto.class);
        } catch (Exception ignored) {
            breakdown = MatchScoreBreakdownDto.builder()
                    .totalScore(m.getMatchScore())
                    .matchReasons(List.of(m.getMatchReasons()))
                    .build();
        }

        return MatchResponse.builder()
                .id(m.getId())
                .lostItemId(m.getLostItem().getId())
                .foundItemId(m.getFoundItem().getId())
                .lostItem(itemMapper.toLostItemResponse(m.getLostItem()))
                .foundItem(itemMapper.toFoundItemResponse(m.getFoundItem(), false))
                .matchScore(m.getMatchScore())
                .classification(m.getClassification())
                .matchReasons(m.getMatchReasons())
                .scoreBreakdown(breakdown)
                .status(m.getStatus())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
