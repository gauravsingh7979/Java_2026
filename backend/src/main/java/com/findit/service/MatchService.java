package com.findit.service;

import com.findit.dto.match.MatchResponse;
import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;

import java.util.List;

public interface MatchService {

    void runMatchingForLostItem(LostItem lostItem);

    void runMatchingForFoundItem(FoundItem foundItem);

    List<MatchResponse> getMatchesForLostItem(Long lostItemId, Long currentUserId);

    List<MatchResponse> getMatchesForFoundItem(Long foundItemId, Long currentUserId);

    void recalculateAll();

    void dismissMatch(Long matchId, Long currentUserId);
}
