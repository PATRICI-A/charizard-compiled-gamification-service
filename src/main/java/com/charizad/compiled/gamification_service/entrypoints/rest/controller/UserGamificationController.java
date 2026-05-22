package com.charizad.compiled.gamification_service.entrypoints.rest.controller;

import com.charizad.compiled.gamification_service.application.dto.request.RedeemEventCodeRequest;
import com.charizad.compiled.gamification_service.application.dto.request.SetRankingOptInRequest;
import com.charizad.compiled.gamification_service.application.dto.response.BadgeProgressResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedBadgeResponse;
import com.charizad.compiled.gamification_service.application.dto.response.EarnedRewardResponse;
import com.charizad.compiled.gamification_service.application.dto.response.MonaResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingOptInResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserLevelResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingEntryResponse;
import com.charizad.compiled.gamification_service.application.dto.response.RankingPositionResponse;
import com.charizad.compiled.gamification_service.application.dto.response.UserStatsResponse;
import com.charizad.compiled.gamification_service.domain.model.enums.RankingType;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingPositionUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserLevelUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetRankingUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserBadgesUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserProgressUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserRewardsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetUserStatsUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetMonaByIdUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.GetMonasUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.RedeemEventCodeUseCase;
import com.charizad.compiled.gamification_service.domain.ports.in.ToggleRankingOptInUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gamificacion")
@RequiredArgsConstructor
@Tag(name = "Gamification", description = "Student-facing gamification endpoints. Allows users to view their badge (mona) collection, track unlock progress, check accumulated XP and level, manage ranking participation, view unlocked rewards, and redeem event attendance codes.")
@SecurityRequirement(name = "bearerAuth")
public class UserGamificationController {

    private final GetUserBadgesUseCase getUserBadgesUseCase;
    private final GetUserProgressUseCase getUserProgressUseCase;
    private final GetUserStatsUseCase getUserStatsUseCase;
    private final ToggleRankingOptInUseCase toggleRankingOptInUseCase;
    private final GetRankingUseCase getRankingUseCase;
    private final GetUserRewardsUseCase getUserRewardsUseCase;
    private final GetUserLevelUseCase getUserLevelUseCase;
    private final GetRankingPositionUseCase getRankingPositionUseCase;
    private final RedeemEventCodeUseCase redeemEventCodeUseCase;
    private final GetMonasUseCase getMonasUseCase;
    private final GetMonaByIdUseCase getMonaByIdUseCase;

    @PostMapping("/monas/evento")
    @Operation(
            summary = "Redeem an event attendance code",
            description = "Allows an authenticated student to redeem an alphanumeric code distributed at a university event " +
                          "in order to earn the 'Asistente' badge (event attendance mona). " +
                          "The code must be active (within its validity window) and must not have been previously redeemed by this user. " +
                          "Each code can be redeemed at most once per student. " +
                          "On success, the badge is added to the user's collection and the corresponding XP is credited to their gamification profile. " +
                          "If the student already redeemed this code, a 400 error is returned with an appropriate message."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Event code redeemed successfully. The 'Asistente' badge has been added to the user's collection. Returns the earned badge record including the unlock timestamp and XP awarded.",
                    content = @Content(schema = @Schema(implementation = EarnedBadgeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or already redeemed code. The code does not exist, is outside its validity window, or has already been used by this user.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Event code not found. No event code matching the provided value exists in the system.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while processing the redemption.", content = @Content)
    })
    public ResponseEntity<EarnedBadgeResponse> redeemEventCode(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @Valid @RequestBody RedeemEventCodeRequest request) {
        return ResponseEntity.ok(redeemEventCodeUseCase.execute(userId, request.getEventCode()));
    }

    @GetMapping("/monas")
    @Operation(
            summary = "Get full badge catalogue with personal status",
            description = "Returns the complete list of all badges (monas) defined in the system, enriched with the authenticated student's personal status for each one. " +
                          "For each badge the response includes: the badge definition (name, description, icon, XP value, unlock criteria), " +
                          "whether the student has already unlocked it, the unlock date if applicable, and the current progress toward unlocking it. " +
                          "This endpoint powers the badge collection screen where students can see what they have earned and what remains to be achieved. " +
                          "Use this endpoint to display the full gamification catalogue to the logged-in user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Full badge catalogue returned. Each entry includes badge metadata and the authenticated user's unlock status and progress. Returns an empty array if no badges are defined yet.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MonaResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while retrieving the badge catalogue.", content = @Content)
    })
    public ResponseEntity<List<MonaResponse>> getMonas(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getMonasUseCase.execute(userId));
    }

    @GetMapping("/monas/{monaId}")
    @Operation(
            summary = "Get detail of a specific badge",
            description = "Returns the full detail of a single badge (mona) identified by its ID, along with the authenticated student's personal unlock status and progress toward it. " +
                          "Use this endpoint to display a badge detail screen showing the badge description, unlock criteria, XP reward, " +
                          "whether the student has already earned it, and how close they are to unlocking it if not yet earned. " +
                          "Returns 404 if the badge ID does not correspond to any badge in the catalog."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Badge detail returned successfully. Includes badge metadata and the authenticated user's current unlock status and progress.",
                    content = @Content(schema = @Schema(implementation = MonaResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Badge not found. The provided monaId does not match any badge in the system catalog.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while retrieving the badge detail.", content = @Content)
    })
    public ResponseEntity<MonaResponse> getMonaById(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @PathVariable String monaId) {
        return ResponseEntity.ok(getMonaByIdUseCase.execute(userId, monaId));
    }

    @GetMapping("/me/badges")
    @Operation(
            summary = "Get my earned badges",
            description = "Returns the list of all badges (monas) that the authenticated user has already unlocked. " +
                          "Each entry includes the badge definition and the date on which the badge was earned. " +
                          "This endpoint is used to populate the 'My Collection' section of the gamification profile. " +
                          "Returns an empty array if the user has not yet earned any badges. " +
                          "Returns 404 if the user's gamification profile has not been initialized in the system."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of earned badges returned successfully. Each entry contains the badge details and the unlock date. Returns an empty array if no badges have been earned yet.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EarnedBadgeResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found. The user's gamification record has not been created yet.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while retrieving the user's badges.", content = @Content)
    })
    public ResponseEntity<List<EarnedBadgeResponse>> getMyBadges(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserBadgesUseCase.execute(userId));
    }

    @GetMapping("/me/progress")
    @Operation(
            summary = "Get my progress toward unearned badges",
            description = "Returns the authenticated user's current progress toward each badge they have not yet unlocked. " +
                          "For each pending badge, the response includes the badge definition, the unlock criteria description, " +
                          "the current progress value (e.g. number of zones visited), and the target value required to unlock it. " +
                          "Badges already earned are excluded from this response — use GET /me/badges to see earned ones. " +
                          "Use this endpoint to display progress bars and motivational indicators on the badge catalogue screen."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress list returned successfully. Each entry describes a not-yet-unlocked badge and the user's current progress toward it. Returns an empty array if all badges have already been earned.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = BadgeProgressResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found. The user's gamification record has not been created yet.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while calculating progress.", content = @Content)
    })
    public ResponseEntity<List<BadgeProgressResponse>> getMyProgress(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserProgressUseCase.execute(userId));
    }

    @GetMapping("/me/stats")
    @Operation(
            summary = "Get my gamification statistics",
            description = "Returns a summary of the authenticated user's overall gamification status. " +
                          "The response includes: total accumulated XP across all time, XP earned in the current weekly period, " +
                          "total number of badges unlocked, and whether the user has opted into the public ranking. " +
                          "This endpoint is used to populate the gamification dashboard and user profile summary cards. " +
                          "Weekly XP is reset at the start of each ranking period (Monday 00:00)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gamification statistics returned successfully. Includes total XP, weekly XP, badge count, and ranking opt-in status.",
                    content = @Content(schema = @Schema(implementation = UserStatsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found. The user's gamification record has not been created yet.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while retrieving statistics.", content = @Content)
    })
    public ResponseEntity<UserStatsResponse> getMyStats(
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserStatsUseCase.execute(userId));
    }

    @PatchMapping("/me/ranking/optin")
    @Operation(
            summary = "Update my ranking participation preference",
            description = "Allows the authenticated user to explicitly opt in or opt out of the public student ranking. " +
                          "When opted in, the user's name and XP appear on the leaderboard visible to other students. " +
                          "When opted out, the user is excluded from all leaderboard queries and their position is not disclosed. " +
                          "Send `{\"participe\": true}` in the request body to join the ranking, or `{\"participe\": false}` to leave it. " +
                          "The preference takes effect immediately. Users can change this setting at any time without restriction."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking participation preference updated successfully. Returns the new opt-in status.",
                    content = @Content(schema = @Schema(implementation = RankingOptInResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body. The 'participe' field is missing or is not a boolean value.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found. The user's gamification record has not been created yet.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while updating the preference.", content = @Content)
    })
    public ResponseEntity<RankingOptInResponse> setRankingOptIn(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody SetRankingOptInRequest request) {
        return ResponseEntity.ok(toggleRankingOptInUseCase.execute(userId, request.getParticipe()));
    }

    @GetMapping("/me/rewards")
    @Operation(
            summary = "Get my unlocked rewards",
            description = "Returns all XP-threshold rewards that the authenticated user has unlocked to date. " +
                          "Rewards are automatically granted when a user's total accumulated XP reaches a predefined threshold. " +
                          "Each reward entry includes the reward name, description, the XP threshold that triggered it, and the unlock date. " +
                          "Returns an empty array if the user has not yet accumulated enough XP to unlock any reward."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of unlocked rewards returned successfully. Each entry contains the reward details and the date it was unlocked. Returns an empty array if no rewards have been earned yet.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EarnedRewardResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found. The user's gamification record has not been created yet.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while retrieving rewards.", content = @Content)
    })
    public ResponseEntity<List<EarnedRewardResponse>> getMyRewards(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserRewardsUseCase.execute(userId));
    }

    @GetMapping("/ranking")
    @Operation(
            summary = "Get the student ranking leaderboard",
            description = "Returns the public leaderboard of all students who have opted into the ranking, sorted by XP earned in the specified period (descending). " +
                          "Only users with ranking opt-in enabled are included. " +
                          "The `tipo` query parameter selects the time window: " +
                          "`WEEKLY` (default) — current week starting Monday; " +
                          "`mensual` — current calendar month; " +
                          "`semestral` — current academic semester. " +
                          "Each entry includes the student's display name, their XP for the period, and their position in the leaderboard. " +
                          "This endpoint is used to render the competitive ranking screen visible to all authenticated users."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking leaderboard returned successfully. Sorted by period XP descending. Returns an empty array if no students have opted in for the selected period.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RankingEntryResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid ranking period value. The 'tipo' parameter must be one of: WEEKLY, mensual, semestral.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while computing the ranking.", content = @Content)
    })
    public ResponseEntity<List<RankingEntryResponse>> getRanking(
            @Parameter(description = "Ranking period. Accepted values: WEEKLY (default, current week), mensual (current month), semestral (current academic semester).", example = "WEEKLY")
            @RequestParam(defaultValue = "WEEKLY") String tipo) {
        return ResponseEntity.ok(getRankingUseCase.execute(parseType(tipo)));
    }

    @GetMapping("/ranking/mi-posicion")
    @Operation(
            summary = "Get my position in the ranking",
            description = "Returns the authenticated user's current position in the leaderboard for the specified ranking period. " +
                          "If the user has not opted into the ranking, the response will contain `position: null` and `rankingOptIn: false`. " +
                          "If the user is opted in but has earned zero XP in the period, they will appear at the bottom of the ranking. " +
                          "Use the same `tipo` values as the main ranking endpoint: WEEKLY (default), mensual, semestral. " +
                          "This endpoint is used to show the student their standing without having to scroll through the full leaderboard."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User's ranking position returned. If the user is not opted in, position is null and rankingOptIn is false. Otherwise returns the 1-based position and the XP earned in the period.",
                    content = @Content(schema = @Schema(implementation = RankingPositionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ranking period value. The 'tipo' parameter must be one of: WEEKLY, mensual, semestral.", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while computing the user's ranking position.", content = @Content)
    })
    public ResponseEntity<RankingPositionResponse> getMyPosition(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId,
            @Parameter(description = "Ranking period. Accepted values: WEEKLY (default, current week), mensual (current month), semestral (current academic semester).", example = "WEEKLY")
            @RequestParam(defaultValue = "WEEKLY") String tipo) {
        return ResponseEntity.ok(getRankingPositionUseCase.execute(userId, parseType(tipo)));
    }

    @GetMapping("/me/nivel")
    @Operation(
            summary = "Get my current gamification level",
            description = "Returns the authenticated user's current level based on the total number of monas (badges) they have collected. " +
                          "The level system is structured in tiers: each tier requires a progressively higher number of earned badges. " +
                          "The response includes the current level name, the number of monas collected, " +
                          "and the number of monas needed to reach the next level (if not yet at maximum). " +
                          "Use this endpoint to display the user's level badge and progression bar on their gamification profile."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User level information returned successfully. Includes current level name, monas collected, and monas required for the next level.",
                    content = @Content(schema = @Schema(implementation = UserLevelResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required. The request is missing a JWT token or the provided token is expired or malformed.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Gamification profile not found. The user's gamification record has not been created yet.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. An unexpected error occurred while computing the user's level.", content = @Content)
    })
    public ResponseEntity<UserLevelResponse> getMiNivel(
            @Parameter(hidden = true) @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(getUserLevelUseCase.execute(userId));
    }

    private RankingType parseType(String tipo) {
        return switch (tipo.toLowerCase()) {
            case "mensual"    -> RankingType.MONTHLY;
            case "semestral"  -> RankingType.SEMESTER;
            default           -> RankingType.WEEKLY;
        };
    }
}
