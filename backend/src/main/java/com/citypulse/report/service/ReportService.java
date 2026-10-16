package com.citypulse.report.service;

import com.citypulse.report.dto.CreateReportRequest;
import com.citypulse.report.dto.ReportSummaryResponse;
import com.citypulse.report.dto.ResolveReportRequest;
import com.citypulse.report.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReportService {

    ReportSummaryResponse submitReport(UUID reporterId, CreateReportRequest request);

    Page<ReportSummaryResponse> getReports(ReportStatus status, Pageable pageable);

    ReportSummaryResponse getReportById(UUID id);

    ReportSummaryResponse claimReport(UUID id, UUID moderatorId);

    ReportSummaryResponse resolveReport(UUID id, UUID moderatorId, ResolveReportRequest request);

    ReportSummaryResponse rejectReport(UUID id, UUID moderatorId, String reason);
}
