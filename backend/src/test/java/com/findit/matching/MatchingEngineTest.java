package com.findit.matching;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.enums.ItemCategory;
import com.findit.enums.MatchClassification;
import com.findit.matching.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MatchingEngineTest {

    private MatchingEngine matchingEngine;

    @BeforeEach
    void setUp() {
        CategoryRule categoryRule = new CategoryRule();
        TitleSimilarityRule titleRule = new TitleSimilarityRule();
        BrandRule brandRule = new BrandRule();
        ColorRule colorRule = new ColorRule();
        LocationProximityRule locationRule = new LocationProximityRule();
        DateProximityRule dateRule = new DateProximityRule();
        DescriptionRule descriptionRule = new DescriptionRule();

        matchingEngine = new MatchingEngine(
                categoryRule, titleRule, brandRule, colorRule,
                locationRule, dateRule, descriptionRule
        );
    }

    @Test
    @DisplayName("Should produce STRONG MATCH when category, title, brand, color, location and date align")
    void testStrongMatch() {
        LostItem lost = LostItem.builder()
                .title("Black Leather Bifold Wallet")
                .category(ItemCategory.WALLETS)
                .brand("Tommy Hilfiger")
                .primaryColor("Black")
                .locationLost("University Main Library 2nd Floor")
                .dateLost(LocalDate.now().minusDays(2))
                .description("Lost my black leather wallet with cash and cards")
                .build();

        FoundItem found = FoundItem.builder()
                .title("Black Leather Bifold Wallet")
                .category(ItemCategory.WALLETS)
                .brand("Tommy Hilfiger")
                .primaryColor("Black")
                .locationFound("University Main Library 2nd Floor study desks")
                .dateFound(LocalDate.now().minusDays(1))
                .publicDescription("Found black leather wallet in library study area")
                .build();

        MatchScoringResult result = matchingEngine.computeMatch(lost, found);

        assertThat(result.getTotalScore()).isGreaterThanOrEqualTo(80.0);
        assertThat(result.getClassification()).isEqualTo(MatchClassification.STRONG);
        assertThat(result.getReasons()).isNotEmpty();
        assertThat(result.getSummaryReason()).contains("Strong match found");
    }

    @Test
    @DisplayName("Should produce 0 score when categories are incompatible")
    void testIncompatibleCategories() {
        LostItem lost = LostItem.builder()
                .title("iPhone 15 Pro")
                .category(ItemCategory.ELECTRONICS)
                .brand("Apple")
                .build();

        FoundItem found = FoundItem.builder()
                .title("iPhone 15 Pro")
                .category(ItemCategory.BOOKS)
                .brand("Apple")
                .build();

        MatchScoringResult result = matchingEngine.computeMatch(lost, found);

        assertThat(result.getTotalScore()).isEqualTo(0.0);
        assertThat(result.getClassification()).isEqualTo(MatchClassification.WEAK);
        assertThat(result.getSummaryReason()).contains("incompatible categories");
    }

    @Test
    @DisplayName("Missing optional attributes should NOT produce confirmed match points")
    void testMissingAttributesNoPoints() {
        LostItem lost = LostItem.builder()
                .title("Generic Notebook")
                .category(ItemCategory.BOOKS)
                .brand(null)
                .primaryColor(null)
                .locationLost("Hallway")
                .dateLost(LocalDate.now())
                .description("Lined paper")
                .build();

        FoundItem found = FoundItem.builder()
                .title("Generic Notebook")
                .category(ItemCategory.BOOKS)
                .brand(null)
                .primaryColor(null)
                .locationFound("Hallway")
                .dateFound(LocalDate.now())
                .publicDescription("Lined notebook")
                .build();

        MatchScoringResult result = matchingEngine.computeMatch(lost, found);

        // Category = 25, Title = 20, Location = 15, Date = 10, Description ~ 5 -> Brand (0) & Color (0)
        assertThat(result.getScoreBreakdown().getBrandScore()).isEqualTo(0.0);
        assertThat(result.getScoreBreakdown().getColorScore()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Chronologically invalid dates (found before lost) should yield 0 date points")
    void testFoundBeforeLostDateYieldsZero() {
        LostItem lost = LostItem.builder()
                .title("Headphones")
                .category(ItemCategory.ELECTRONICS)
                .dateLost(LocalDate.now())
                .build();

        FoundItem found = FoundItem.builder()
                .title("Headphones")
                .category(ItemCategory.ELECTRONICS)
                .dateFound(LocalDate.now().minusDays(5)) // Found 5 days BEFORE it was lost!
                .build();

        MatchScoringResult result = matchingEngine.computeMatch(lost, found);

        assertThat(result.getScoreBreakdown().getDateProximityScore()).isEqualTo(0.0);
    }
}
