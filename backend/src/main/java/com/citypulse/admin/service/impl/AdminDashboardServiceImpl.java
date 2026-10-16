package com.citypulse.admin.service.impl;

import com.citypulse.admin.dto.AdminStatsOverviewResponse;
import com.citypulse.admin.service.AdminDashboardService;
import com.citypulse.audit.repository.AuditLogRepository;
import com.citypulse.moderation.repository.ModerationActionRepository;
import com.citypulse.report.entity.ReportStatus;
import com.citypulse.report.repository.ReportRepository;
import com.citypulse.user.entity.UserStatus;
import com.citypulse.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final ReportRepository reportRepository;
    private final ModerationActionRepository moderationActionRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AdminDashboardServiceImpl(ReportRepository reportRepository,
                                    ModerationActionRepository moderationActionRepository,
                                    AuditLogRepository auditLogRepository,
                                    UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.moderationActionRepository = moderationActionRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AdminStatsOverviewResponse getStatsOverview() {
        long openReports = reportRepository.countByStatus(ReportStatus.OPEN);
        long inReviewReports = reportRepository.countByStatus(ReportStatus.IN_REVIEW);
        long resolvedReports = reportRepository.countByStatus(ReportStatus.RESOLVED);
        long lockedUsers = userRepository.countByStatus(UserStatus.LOCKED);
        long totalModerationActions = moderationActionRepository.count();
        long totalAuditLogs = auditLogRepository.count();

        return new AdminStatsOverviewResponse(
            openReports,
            inReviewReports,
            resolvedReports,
            lockedUsers,
            totalModerationActions,
            totalAuditLogs,
            "OPERATIONAL"
        );
    }
}
