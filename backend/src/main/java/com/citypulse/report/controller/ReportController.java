package com.citypulse.report.controller;

import com.citypulse.report.dto.CreateReportRequest;
import com.citypulse.report.dto.ReportSummaryResponse;
import com.citypulse.report.service.ReportService;
import com.citypulse.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public/User facing endpoint for submitting content violation reports.
 */
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportSummaryResponse> submitReport(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody CreateReportRequest request) {
        ReportSummaryResponse response = reportService.submitReport(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
