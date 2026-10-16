package com.citypulse.moderation.controller;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.moderation.dto.ModerationActionResponse;
import com.citypulse.moderation.dto.ModerationDecisionRequest;
import com.citypulse.moderation.service.ModerationService;
import com.citypulse.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/moderation")
@PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'ADMIN')")
public class AdminModerationController {

    private final ModerationService moderationService;

    public AdminModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping("/actions")
    public ResponseEntity<PageResponse<ModerationActionResponse>> getModerationHistory(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.fromPage(moderationService.getModerationHistory(pageable)));
    }

    @PostMapping("/decisions/{subjectType}/{subjectId}")
    public ResponseEntity<ModerationActionResponse> executeDecision(
            @PathVariable String subjectType,
            @PathVariable UUID subjectId,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ModerationDecisionRequest request) {
        ModerationActionResponse response = moderationService.executeDecision(
            currentUser.getId(),
            subjectType.toUpperCase(),
            subjectId,
            request
        );
        return ResponseEntity.ok(response);
    }
}
