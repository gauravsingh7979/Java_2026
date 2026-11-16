package com.findit.matching.rules;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.matching.MatchingRule;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class LocationProximityRule implements MatchingRule {

    private static final double MAX_SCORE = 15.0;

    @Override
    public double evaluate(LostItem lostItem, FoundItem foundItem, List<String> matchReasons) {
        String l1 = lostItem.getLocationLost();
        String l2 = foundItem.getLocationFound();

        if (!StringUtils.hasText(l1) || !StringUtils.hasText(l2)) {
            return 0.0;
        }

        String nl1 = clean(l1);
        String nl2 = clean(l2);

        if (nl1.equals(nl2)) {
            matchReasons.add("Identical location reported: '" + lostItem.getLocationLost() + "'");
            return MAX_SCORE;
        }

        Set<String> words1 = new HashSet<>(Arrays.asList(nl1.split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(nl2.split("\\s+")));

        // Filter out very short noise words
        words1.removeIf(w -> w.length() <= 2);
        words2.removeIf(w -> w.length() <= 2);

        if (words1.isEmpty() || words2.isEmpty()) {
            return 0.0;
        }

        Set<String> common = new HashSet<>(words1);
        common.retainAll(words2);

        if (!common.isEmpty()) {
            double ratio = (double) common.size() / Math.min(words1.size(), words2.size());
            double score = Math.round(ratio * MAX_SCORE * 10.0) / 10.0;
            score = Math.min(score, MAX_SCORE);

            matchReasons.add("Location similarity: common landmark(s) " + common);
            return score;
        }

        return 0.0;
    }

    private String clean(String s) {
        return s.trim().toLowerCase().replaceAll("[^a-z0-9\\s]", "");
    }

    @Override
    public double getMaxScore() {
        return MAX_SCORE;
    }

    @Override
    public String getRuleName() {
        return "Location Proximity";
    }
}
