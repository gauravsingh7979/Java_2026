package com.findit.matching.rules;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.matching.MatchingRule;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class ColorRule implements MatchingRule {

    private static final double MAX_SCORE = 10.0;

    @Override
    public double evaluate(LostItem lostItem, FoundItem foundItem, List<String> matchReasons) {
        String c1 = lostItem.getPrimaryColor();
        String c2 = foundItem.getPrimaryColor();

        // Missing color information must NOT be treated as confirmed match
        if (!StringUtils.hasText(c1) || !StringUtils.hasText(c2)) {
            return 0.0;
        }

        String nc1 = c1.trim().toLowerCase();
        String nc2 = c2.trim().toLowerCase();

        if (nc1.equals(nc2)) {
            matchReasons.add("Identical color match: " + c1.trim());
            return MAX_SCORE;
        }

        if (nc1.contains(nc2) || nc2.contains(nc1)) {
            matchReasons.add("Matching color shade: " + c1.trim() + " / " + c2.trim());
            return 8.0;
        }

        // Color synonym normalization (grey/gray)
        if (nc1.replace("grey", "gray").equals(nc2.replace("grey", "gray"))) {
            matchReasons.add("Color match: " + c1.trim());
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
        return "Color Similarity";
    }
}
