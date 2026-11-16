package com.findit.matching;

import com.findit.dto.match.MatchScoreBreakdownDto;
import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.enums.MatchClassification;
import com.findit.matching.rules.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchingEngine {

    private static final Logger log = LoggerFactory.getLogger(MatchingEngine.class);

    private final CategoryRule categoryRule;
    private final TitleSimilarityRule titleRule;
    private final BrandRule brandRule;
    private final ColorRule colorRule;
    private final LocationProximityRule locationRule;
    private final DateProximityRule dateRule;
    private final DescriptionRule descriptionRule;

    public MatchingEngine(CategoryRule categoryRule,
                          TitleSimilarityRule titleRule,
                          BrandRule brandRule,
                          ColorRule colorRule,
                          LocationProximityRule locationRule,
                          DateProximityRule dateRule,
                          DescriptionRule descriptionRule) {
        this.categoryRule = categoryRule;
        this.titleRule = titleRule;
        this.brandRule = brandRule;
        this.colorRule = colorRule;
        this.locationRule = locationRule;
        this.dateRule = dateRule;
        this.descriptionRule = descriptionRule;
    }

    public MatchScoringResult computeMatch(LostItem lostItem, FoundItem foundItem) {
        List<String> reasons = new ArrayList<>();

        double catScore = categoryRule.evaluate(lostItem, foundItem, reasons);
        if (catScore == 0.0) {
            MatchScoreBreakdownDto breakdown = MatchScoreBreakdownDto.builder()
                    .totalScore(0.0)
                    .categoryScore(0.0)
                    .titleSimilarityScore(0.0)
                    .brandScore(0.0)
                    .colorScore(0.0)
                    .locationScore(0.0)
                    .dateProximityScore(0.0)
                    .descriptionScore(0.0)
                    .matchReasons(List.of("Incompatible categories (" + lostItem.getCategory() + " vs " + foundItem.getCategory() + ")"))
                    .build();

            return MatchScoringResult.builder()
                    .totalScore(0.0)
                    .classification(MatchClassification.WEAK)
                    .scoreBreakdown(breakdown)
                    .reasons(breakdown.getMatchReasons())
                    .summaryReason("No match: items belong to incompatible categories.")
                    .build();
        }

        double titleScore = titleRule.evaluate(lostItem, foundItem, reasons);
        double brandScore = brandRule.evaluate(lostItem, foundItem, reasons);
        double colorScore = colorRule.evaluate(lostItem, foundItem, reasons);
        double locScore = locationRule.evaluate(lostItem, foundItem, reasons);
        double dateScore = dateRule.evaluate(lostItem, foundItem, reasons);
        double descScore = descriptionRule.evaluate(lostItem, foundItem, reasons);

        double total = catScore + titleScore + brandScore + colorScore + locScore + dateScore + descScore;
        total = Math.min(100.0, Math.round(total * 10.0) / 10.0);

        MatchClassification classification = MatchScoringResult.classify(total);

        MatchScoreBreakdownDto breakdown = MatchScoreBreakdownDto.builder()
                .totalScore(total)
                .categoryScore(catScore)
                .titleSimilarityScore(titleScore)
                .brandScore(brandScore)
                .colorScore(colorScore)
                .locationScore(locScore)
                .dateProximityScore(dateScore)
                .descriptionScore(descScore)
                .matchReasons(reasons)
                .build();

        String summary = buildSummaryReason(lostItem, foundItem, total, classification);

        return MatchScoringResult.builder()
                .totalScore(total)
                .classification(classification)
                .scoreBreakdown(breakdown)
                .reasons(reasons)
                .summaryReason(summary)
                .build();
    }

    private String buildSummaryReason(LostItem lost, FoundItem found, double score, MatchClassification classification) {
        StringBuilder sb = new StringBuilder();
        String classText = classification == MatchClassification.STRONG ? "Strong match found" :
                (classification == MatchClassification.POSSIBLE ? "Possible match found" : "Weak match found");

        sb.append(classText).append(" (").append(score).append("/100): ");
        sb.append("Your lost '").append(lost.getTitle()).append("' may match a found item '")
                .append(found.getTitle()).append("'");

        if (found.getLocationFound() != null && !found.getLocationFound().isBlank()) {
            sb.append(" reported near ").append(found.getLocationFound());
        }

        if (lost.getDateLost() != null && found.getDateFound() != null) {
            long days = ChronoUnit.DAYS.between(lost.getDateLost(), found.getDateFound());
            if (days == 0) {
                sb.append(" on the same day.");
            } else if (days > 0) {
                sb.append(" ").append(days).append(" day").append(days > 1 ? "s" : "").append(" later.");
            } else {
                sb.append(".");
            }
        } else {
            sb.append(".");
        }

        return sb.toString();
    }
}
