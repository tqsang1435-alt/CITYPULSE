package com.citypulse.report.controller;

import com.citypulse.common.dto.PageResponse;
import com.citypulse.report.dto.ReportSummaryResponse;
import com.citypulse.report.dto.ResolveReportRequest;
import com.citypulse.report.entity.ReportStatus;
import com.citypulse.report.service.ReportService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reports")
@PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReportSummaryResponse>> getReports(
            @RequestParam(required = false) ReportStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.fromPage(reportService.getReports(status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportSummaryResponse> getReportById(@PathVariable UUID id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<ReportSummaryResponse> claimReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(reportService.claimReport(id, currentUser.getId()));
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ReportSummaryResponse> resolveReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ResolveReportRequest request) {
        return ResponseEntity.ok(reportService.resolveReport(id, currentUser.getId(), request));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ReportSummaryResponse> rejectReport(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(reportService.rejectReport(id, currentUser.getId(), reason));
    }
}
