package com.citypulse.trip.controller;

import com.citypulse.trip.service.CreateProposalRequest;
import com.citypulse.trip.service.ProposalResponse;
import com.citypulse.trip.service.ReviewProposalRequest;
import com.citypulse.trip.service.TripProposalService;
import com.citypulse.user.service.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller cho luồng Đề xuất địa điểm chuyến đi (UC-05, UC-06).
 */
@RestController
@RequestMapping("/api/v1/trips/{tripId}/proposals")
public class TripProposalController {

    private final TripProposalService proposalService;

    public TripProposalController(TripProposalService proposalService) {
        this.proposalService = proposalService;
    }

    /**
     * UC-05: Đề xuất địa điểm
     * POST /api/v1/trips/{tripId}/proposals
     * Quyền: Owner hoặc Contributor
     */
    @PostMapping
    public ResponseEntity<ProposalResponse> createProposal(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody CreateProposalRequest request) {
        ProposalResponse response = proposalService.createProposal(tripId, currentUser.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lấy danh sách đề xuất
     * GET /api/v1/trips/{tripId}/proposals
     * Quyền: Thành viên của trip
     */
    @GetMapping
    public ResponseEntity<List<ProposalResponse>> listProposals(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        List<ProposalResponse> responses = proposalService.listProposals(tripId, currentUser.id());
        return ResponseEntity.ok(responses);
    }

    /**
     * UC-06: Owner duyệt hoặc từ chối đề xuất
     * PATCH /api/v1/trips/{tripId}/proposals/{proposalId}/review
     * Quyền: Chỉ Owner
     */
    @PatchMapping("/{proposalId}/review")
    public ResponseEntity<ProposalResponse> reviewProposal(
            @PathVariable Long tripId,
            @PathVariable Long proposalId,
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody ReviewProposalRequest request) {
        ProposalResponse response = proposalService.reviewProposal(tripId, proposalId, currentUser.id(), request);
        return ResponseEntity.ok(response);
    }
}

