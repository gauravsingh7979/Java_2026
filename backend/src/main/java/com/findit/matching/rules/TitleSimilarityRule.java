package com.findit.matching.rules;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.matching.MatchingRule;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class TitleSimilarityRule implements MatchingRule {

    private static final double MAX_SCORE = 20.0;

    @Override
    public double evaluate(LostItem lostItem, FoundItem foundItem, List<String> matchReasons) {
        String t1 = cleanString(lostItem.getTitle());
        String t2 = cleanString(foundItem.getTitle());

        if (!StringUtils.hasText(t1) || !StringUtils.hasText(t2)) {
            return 0.0;
        }

        if (t1.equalsIgnoreCase(t2)) {
            matchReasons.add("Exact item title match: '" + lostItem.getTitle() + "'");
            return MAX_SCORE;
        }

        // Token Jaccard similarity
        Set<String> words1 = new HashSet<>(Arrays.asList(t1.split("\\s+")));
        Set<String> words2 = new HashSet<>(Arrays.asList(t2.split("\\s+")));

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        double jaccard = union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();

        // Levenshtein similarity
        int distance = computeLevenshteinDistance(t1, t2);
        int maxLen = Math.max(t1.length(), t2.length());
        double editSim = maxLen == 0 ? 0.0 : 1.0 - ((double) distance / maxLen);

        // Blended similarity
        double similarity = Math.max(jaccard, editSim);
        double score = Math.round(similarity * MAX_SCORE * 10.0) / 10.0;

        if (score >= 12.0) {
            matchReasons.add("High item title similarity (" + Math.round(similarity * 100) + "%)");
        } else if (score >= 6.0) {
            matchReasons.add("Moderate item title similarity (" + Math.round(similarity * 100) + "%)");
        }

        return Math.min(score, MAX_SCORE);
    }

    private String cleanString(String input) {
        if (input == null) return "";
        return input.trim().toLowerCase().replaceAll("[^a-z0-9\\s]", "");
    }

    private int computeLevenshteinDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int j = 0; j <= s2.length(); j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= s1.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= s2.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        s1.charAt(i - 1) == s2.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[s2.length()];
    }

    @Override
    public double getMaxScore() {
        return MAX_SCORE;
    }

    @Override
    public String getRuleName() {
        return "Item Name Similarity";
    }
}
