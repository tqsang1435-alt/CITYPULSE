package com.citypulse.report.service.impl;

import com.citypulse.audit.service.AuditLogService;
import com.citypulse.report.dto.CreateReportRequest;
import com.citypulse.report.dto.ReportSummaryResponse;
import com.citypulse.report.dto.ResolveReportRequest;
import com.citypulse.report.entity.Report;
import com.citypulse.report.entity.ReportStatus;
import com.citypulse.report.repository.ReportRepository;
import com.citypulse.report.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final AuditLogService auditLogService;

    public ReportServiceImpl(ReportRepository reportRepository, AuditLogService auditLogService) {
        this.reportRepository = reportRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public ReportSummaryResponse submitReport(UUID reporterId, CreateReportRequest request) {
        // BR-60 Deduplication check: If user already submitted an OPEN report for the same subject
        Optional<Report> existingActive = reportRepository.findActiveReportByReporterAndSubject(
            reporterId,
            request.getSubjectType(),
            request.getSubjectId()
        );

        if (existingActive.isPresent()) {
            Report duplicate = existingActive.get();
            duplicate.setDuplicateCount(duplicate.getDuplicateCount() + 1);
            if (request.getDescription() != null && !request.getDescription().isBlank()) {
                duplicate.setDescription(duplicate.getDescription() + " | [Bổ sung]: " + request.getDescription());
            }
            Report saved = reportRepository.save(duplicate);
            return ReportSummaryResponse.fromEntity(saved);
        }

        Report report = new Report(
            reporterId,
            request.getSubjectType(),
            request.getSubjectId(),
            request.getReason(),
            request.getDescription()
        );
        report.setEvidenceUrls(request.getEvidenceUrls());
        Report saved = reportRepository.save(report);
        return ReportSummaryResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportSummaryResponse> getReports(ReportStatus status, Pageable pageable) {
        if (status != null) {
            return reportRepository.findByStatus(status, pageable).map(ReportSummaryResponse::fromEntity);
        }
        return reportRepository.findAll(pageable).map(ReportSummaryResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportSummaryResponse getReportById(UUID id) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Không tìm thấy báo cáo với mã: " + id));
        return ReportSummaryResponse.fromEntity(report);
    }

    @Override
    public ReportSummaryResponse claimReport(UUID id, UUID moderatorId) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Không tìm thấy báo cáo với mã: " + id));

        report.setStatus(ReportStatus.IN_REVIEW);
        report.setAssignedTo(moderatorId);
        Report updated = reportRepository.save(report);

        auditLogService.recordLog(
            moderatorId,
            "moderator",
            "REPORT_CLAIM",
            "REPORT",
            id,
            "{\"status\": \"OPEN\"}",
            "{\"status\": \"IN_REVIEW\", \"assignedTo\": \"" + moderatorId + "\"}",
            "Tiếp nhận xử lý báo cáo",
            null,
            null,
            null
        );

        return ReportSummaryResponse.fromEntity(updated);
    }

    @Override
    public ReportSummaryResponse resolveReport(UUID id, UUID moderatorId, ResolveReportRequest request) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Không tìm thấy báo cáo với mã: " + id));

        String beforeState = "{\"status\": \"" + report.getStatus() + "\"}";

        report.setStatus(ReportStatus.RESOLVED);
        report.setResolutionAction(request.getResolutionAction());
        report.setAssignedTo(moderatorId);
        report.setResolvedAt(Instant.now());
        Report updated = reportRepository.save(report);

        auditLogService.recordLog(
            moderatorId,
            "moderator",
            "REPORT_RESOLVE",
            "REPORT",
            id,
            beforeState,
            "{\"status\": \"RESOLVED\", \"resolutionAction\": \"" + request.getResolutionAction() + "\"}",
            request.getReason(),
            null,
            null,
            null
        );

        return ReportSummaryResponse.fromEntity(updated);
    }

    @Override
    public ReportSummaryResponse rejectReport(UUID id, UUID moderatorId, String reason) {
        Report report = reportRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Không tìm thấy báo cáo với mã: " + id));

        String beforeState = "{\"status\": \"" + report.getStatus() + "\"}";

        report.setStatus(ReportStatus.REJECTED);
        report.setResolutionAction("REJECTED_NO_VIOLATION");
        report.setAssignedTo(moderatorId);
        report.setResolvedAt(Instant.now());
        Report updated = reportRepository.save(report);

        auditLogService.recordLog(
            moderatorId,
            "moderator",
            "REPORT_REJECT",
            "REPORT",
            id,
            beforeState,
            "{\"status\": \"REJECTED\"}",
            reason != null ? reason : "Báo cáo không có căn cứ vi phạm",
            null,
            null,
            null
        );

        return ReportSummaryResponse.fromEntity(updated);
    }
}
