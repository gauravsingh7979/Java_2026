package com.findit.controller;

import com.findit.dto.common.ApiResponse;
import com.findit.dto.match.MatchResponse;
import com.findit.security.SecurityUtils;
import com.findit.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Matching Engine", description = "Endpoints for viewing and managing smart heuristic item matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/api/lost-items/{id}/matches")
    @Operation(summary = "Get potential matches for a lost item")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatchesForLostItem(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<MatchResponse> matches = matchService.getMatchesForLostItem(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(matches));
    }

    @GetMapping("/api/found-items/{id}/matches")
    @Operation(summary = "Get potential matches for a found item")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatchesForFoundItem(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<MatchResponse> matches = matchService.getMatchesForFoundItem(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(matches));
    }

    @PostMapping("/api/matches/recalculate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Admin trigger full match recalculation")
    public ResponseEntity<ApiResponse<Void>> recalculateAllMatches() {
        matchService.recalculateAll();
        return ResponseEntity.ok(ApiResponse.ok(null, "Full matching recalculation triggered successfully."));
    }

    @PatchMapping("/api/matches/{id}/dismiss")
    @Operation(summary = "Dismiss an irrelevant match recommendation")
    public ResponseEntity<ApiResponse<Void>> dismissMatch(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        matchService.dismissMatch(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Match dismissed successfully."));
    }
}
