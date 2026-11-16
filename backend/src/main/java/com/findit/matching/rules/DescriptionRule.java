package com.findit.matching.rules;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.matching.MatchingRule;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class DescriptionRule implements MatchingRule {

    private static final double MAX_SCORE = 5.0;

    private static final Set<String> STOPWORDS = Set.of(
            "the", "and", "a", "an", "in", "on", "at", "to", "for", "of", "with", "is", "it",
            "this", "that", "was", "my", "item", "lost", "found", "has", "have", "had", "near"
    );

    @Override
    public double evaluate(LostItem lostItem, FoundItem foundItem, List<String> matchReasons) {
        String desc1 = (lostItem.getDescription() != null ? lostItem.getDescription() : "") + " "
                + (lostItem.getDistinctiveCharacteristics() != null ? lostItem.getDistinctiveCharacteristics() : "");
        String desc2 = foundItem.getPublicDescription() != null ? foundItem.getPublicDescription() : "";

        if (!StringUtils.hasText(desc1) || !StringUtils.hasText(desc2)) {
            return 0.0;
        }

        Set<String> k1 = extractKeywords(desc1);
        Set<String> k2 = extractKeywords(desc2);

        if (k1.isEmpty() || k2.isEmpty()) {
            return 0.0;
        }

        Set<String> common = new HashSet<>(k1);
        common.retainAll(k2);

        if (!common.isEmpty()) {
            double ratio = (double) common.size() / Math.min(k1.size(), k2.size());
            double score = Math.round(ratio * MAX_SCORE * 10.0) / 10.0;
            score = Math.min(score, MAX_SCORE);

            matchReasons.add("Description keywords shared: " + common);
            return score;
        }

        return 0.0;
    }

    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        String[] words = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+");
        for (String w : words) {
            if (w.length() > 3 && !STOPWORDS.contains(w)) {
                keywords.add(w);
            }
        }
        return keywords;
    }

    @Override
    public double getMaxScore() {
        return MAX_SCORE;
    }

    @Override
    public String getRuleName() {
        return "Description Keyword Overlap";
    }
}
