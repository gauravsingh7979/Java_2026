package com.findit.matching.rules;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.matching.MatchingRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryRule implements MatchingRule {

    private static final double MAX_SCORE = 25.0;

    @Override
    public double evaluate(LostItem lostItem, FoundItem foundItem, List<String> matchReasons) {
        if (lostItem.getCategory() == null || foundItem.getCategory() == null) {
            return 0.0;
        }

        if (lostItem.getCategory().equals(foundItem.getCategory())) {
            matchReasons.add("Identical category: " + lostItem.getCategory());
            return MAX_SCORE;
        }

        return 0.0;
    }

    @Override
    public double getMaxScore() {
        return MAX_SCORE;
    }

    @Override
    public String getRuleName() {
        return "Category Compatibility";
    }
}
