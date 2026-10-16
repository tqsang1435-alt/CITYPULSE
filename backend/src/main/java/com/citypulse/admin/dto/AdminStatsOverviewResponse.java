package com.citypulse.admin.dto;

public class AdminStatsOverviewResponse {

    private long openReportsCount;
    private long inReviewReportsCount;
    private long resolvedReportsCount;
    private long lockedUsersCount;
    private long totalModerationActionsCount;
    private long totalAuditLogsCount;
    private String systemStatus;

    public AdminStatsOverviewResponse() {
    }

    public AdminStatsOverviewResponse(long openReportsCount, long inReviewReportsCount, long resolvedReportsCount,
                                    long lockedUsersCount, long totalModerationActionsCount,
                                    long totalAuditLogsCount, String systemStatus) {
        this.openReportsCount = openReportsCount;
        this.inReviewReportsCount = inReviewReportsCount;
        this.resolvedReportsCount = resolvedReportsCount;
        this.lockedUsersCount = lockedUsersCount;
        this.totalModerationActionsCount = totalModerationActionsCount;
        this.totalAuditLogsCount = totalAuditLogsCount;
        this.systemStatus = systemStatus;
    }

    public long getOpenReportsCount() {
        return openReportsCount;
    }

    public void setOpenReportsCount(long openReportsCount) {
        this.openReportsCount = openReportsCount;
    }

    public long getInReviewReportsCount() {
        return inReviewReportsCount;
    }

    public void setInReviewReportsCount(long inReviewReportsCount) {
        this.inReviewReportsCount = inReviewReportsCount;
    }

    public long getResolvedReportsCount() {
        return resolvedReportsCount;
    }

    public void setResolvedReportsCount(long resolvedReportsCount) {
        this.resolvedReportsCount = resolvedReportsCount;
    }

    public long getLockedUsersCount() {
        return lockedUsersCount;
    }

    public void setLockedUsersCount(long lockedUsersCount) {
        this.lockedUsersCount = lockedUsersCount;
    }

    public long getTotalModerationActionsCount() {
        return totalModerationActionsCount;
    }

    public void setTotalModerationActionsCount(long totalModerationActionsCount) {
        this.totalModerationActionsCount = totalModerationActionsCount;
    }

    public long getTotalAuditLogsCount() {
        return totalAuditLogsCount;
    }

    public void setTotalAuditLogsCount(long totalAuditLogsCount) {
        this.totalAuditLogsCount = totalAuditLogsCount;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }
}
