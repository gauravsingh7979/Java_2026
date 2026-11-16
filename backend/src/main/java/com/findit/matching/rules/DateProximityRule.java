package com.findit.matching.rules;

import com.findit.entity.FoundItem;
import com.findit.entity.LostItem;
import com.findit.matching.MatchingRule;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class DateProximityRule implements MatchingRule {

    private static final double MAX_SCORE = 10.0;

    @Override
    public double evaluate(LostItem lostItem, FoundItem foundItem, List<String> matchReasons) {
        LocalDate dateLost = lostItem.getDateLost();
        LocalDate dateFound = foundItem.getDateFound();

        if (dateLost == null || dateFound == null) {
            return 0.0;
        }

        long daysDiff = ChronoUnit.DAYS.between(dateLost, dateFound);

        // Found before reported lost: chronologically incongruent
        if (daysDiff < 0) {
            return 0.0;
        }

        if (daysDiff <= 2) {
            matchReasons.add("Date proximity: Found within " + (daysDiff == 0 ? "the same day" : daysDiff + " days"));
            return MAX_SCORE;
        } else if (daysDiff <= 5) {
            matchReasons.add("Date proximity: Found " + daysDiff + " days after lost date");
            return 8.0;
        } else if (daysDiff <= 10) {
            matchReasons.add("Date proximity: Found " + daysDiff + " days later");
            return 6.0;
        } else if (daysDiff <= 20) {
            matchReasons.add("Date proximity: Found " + daysDiff + " days later");
            return 4.0;
        } else if (daysDiff <= 30) {
            matchReasons.add("Date proximity: Found " + daysDiff + " days later");
            return 2.0;
        }

        return 0.0;
    }

    @Override
    public double getMaxScore() {
        return MAX_SCORE;
    }

    @Override
    public String getRuleName() {
        return "Date Proximity";
    }
}
