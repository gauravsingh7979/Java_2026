package com.findit.matching;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;

import java.util.List;

public interface MatchingRule {

    double evaluate(LostItem lostItem, FoundItem foundItem, List<String> matchReasons);

    double getMaxScore();

    String getRuleName();
}
