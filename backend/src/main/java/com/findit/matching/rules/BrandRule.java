package com.findit.matching.rules;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.matching.MatchingRule;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class BrandRule implements MatchingRule {

    private static final double MAX_SCORE = 15.0;

    @Override
    public double evaluate(LostItem lostItem, FoundItem foundItem, List<String> matchReasons) {
        String b1 = lostItem.getBrand();
        String b2 = foundItem.getBrand();

        // If either brand is missing, do NOT treat as confirmed match
        if (!StringUtils.hasText(b1) || !StringUtils.hasText(b2)) {
            return 0.0;
        }

        String nb1 = b1.trim().toLowerCase();
        String nb2 = b2.trim().toLowerCase();

        if (nb1.equals(nb2)) {
            matchReasons.add("Identical brand: " + b1.trim());
            return MAX_SCORE;
        }

        if (nb1.contains(nb2) || nb2.contains(nb1)) {
            matchReasons.add("Brand match: " + b1.trim() + " ~ " + b2.trim());
            return 12.0;
        }

        return 0.0;
    }

    @Override
    public double getMaxScore() {
        return MAX_SCORE;
    }

    @Override
    public String getRuleName() {
        return "Brand Similarity";
    }
}
