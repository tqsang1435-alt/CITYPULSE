package com.citypulse.report;

import com.citypulse.audit.service.AuditLogService;
import com.citypulse.report.dto.CreateReportRequest;
import com.citypulse.report.dto.ReportSummaryResponse;
import com.citypulse.report.dto.ResolveReportRequest;
import com.citypulse.report.entity.Report;
import com.citypulse.report.entity.ReportStatus;
import com.citypulse.report.entity.ReportSubjectType;
import com.citypulse.report.repository.ReportRepository;
import com.citypulse.report.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private AuditLogService auditLogService;

    private ReportServiceImpl reportService;

    private final UUID reporterId = UUID.randomUUID();
    private final UUID subjectId = UUID.randomUUID();
    private final UUID moderatorId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(reportRepository, auditLogService);
    }

    @Test
    @DisplayName("REPORT-01: Gửi báo cáo hợp lệ lần đầu tiên tạo trạng thái OPEN")
    void shouldSubmitNewReportSuccessfully() {
        CreateReportRequest request = new CreateReportRequest(
            ReportSubjectType.PLACE,
            subjectId,
            "SPAM",
            "Nội dung chứa liên kết lừa đảo",
            null
        );

        when(reportRepository.findActiveReportByReporterAndSubject(reporterId, ReportSubjectType.PLACE, subjectId))
            .thenReturn(Optional.empty());

        when(reportRepository.save(any(Report.class)))
            .thenAnswer(invocation -> {
                Report r = invocation.getArgument(0);
                r.setId(UUID.randomUUID());
                return r;
            });

        ReportSummaryResponse response = reportService.submitReport(reporterId, request);

        assertNotNull(response);
        assertEquals(ReportStatus.OPEN, response.getStatus());
        assertEquals(1, response.getDuplicateCount());
        assertEquals("SPAM", response.getReason());
        verify(reportRepository, times(1)).save(any(Report.class));
    }

    @Test
    @DisplayName("REPORT-02 (BR-60): Gom nhóm báo cáo trùng lặp cho cùng đối tượng đang OPEN")
    void shouldDeduplicateActiveReport() {
        CreateReportRequest request = new CreateReportRequest(
            ReportSubjectType.PLACE,
            subjectId,
            "SPAM",
            "Bổ sung thêm hình ảnh chứng cứ",
            null
        );

        Report existing = new Report(reporterId, ReportSubjectType.PLACE, subjectId, "SPAM", "Nội dung ban đầu");
        existing.setId(UUID.randomUUID());
        existing.setDuplicateCount(1);

        when(reportRepository.findActiveReportByReporterAndSubject(reporterId, ReportSubjectType.PLACE, subjectId))
            .thenReturn(Optional.of(existing));

        when(reportRepository.save(existing)).thenReturn(existing);

        ReportSummaryResponse response = reportService.submitReport(reporterId, request);

        assertNotNull(response);
        assertEquals(2, response.getDuplicateCount());
        assertTrue(existing.getDescription().contains("[Bổ sung]"));
        verify(reportRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("REPORT-03: Moderator tiếp nhận báo cáo chuyển trạng thái sang IN_REVIEW")
    void shouldClaimReportSuccessfully() {
        UUID reportId = UUID.randomUUID();
        Report report = new Report(reporterId, ReportSubjectType.PLACE, subjectId, "MISINFORMATION", "Sai giờ");
        report.setId(reportId);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(report);

        ReportSummaryResponse response = reportService.claimReport(reportId, moderatorId);

        assertEquals(ReportStatus.IN_REVIEW, response.getStatus());
        assertEquals(moderatorId, response.getAssignedTo());
        verify(auditLogService, times(1)).recordLog(
            eq(moderatorId), any(), eq("REPORT_CLAIM"), eq("REPORT"), eq(reportId), any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    @DisplayName("REPORT-04: Giải quyết báo cáo thành công và ghi nhận Audit Log")
    void shouldResolveReportSuccessfully() {
        UUID reportId = UUID.randomUUID();
        Report report = new Report(reporterId, ReportSubjectType.PLACE, subjectId, "MISINFORMATION", "Sai giờ");
        report.setId(reportId);
        report.setStatus(ReportStatus.IN_REVIEW);

        ResolveReportRequest resolveRequest = new ResolveReportRequest("CONTENT_HIDDEN", "Đã ẩn nội dung vi phạm");

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(report);

        ReportSummaryResponse response = reportService.resolveReport(reportId, moderatorId, resolveRequest);

        assertEquals(ReportStatus.RESOLVED, response.getStatus());
        assertEquals("CONTENT_HIDDEN", response.getResolutionAction());
        assertNotNull(response.getResolvedAt());
        verify(auditLogService, times(1)).recordLog(
            eq(moderatorId), any(), eq("REPORT_RESOLVE"), eq("REPORT"), eq(reportId), any(), any(), eq("Đã ẩn nội dung vi phạm"), any(), any(), any()
        );
    }
}
