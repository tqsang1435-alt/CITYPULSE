package com.citypulse.report.dto;

import com.citypulse.report.entity.Report;
import com.citypulse.report.entity.ReportStatus;
import com.citypulse.report.entity.ReportSubjectType;

import java.time.Instant;
import java.util.UUID;

public class ReportSummaryResponse {

    private UUID id;
    private UUID reporterId;
    private ReportSubjectType subjectType;
    private UUID subjectId;
    private String reason;
    private String description;
    private String evidenceUrls;
    private ReportStatus status;
    private String resolutionAction;
    private UUID assignedTo;
    private Instant resolvedAt;
    private int duplicateCount;
    private Instant createdAt;
    private Instant updatedAt;

    public ReportSummaryResponse() {
    }

    public static ReportSummaryResponse fromEntity(Report report) {
        ReportSummaryResponse dto = new ReportSummaryResponse();
        dto.setId(report.getId());
        dto.setReporterId(report.getReporterId());
        dto.setSubjectType(report.getSubjectType());
        dto.setSubjectId(report.getSubjectId());
        dto.setReason(report.getReason());
        dto.setDescription(report.getDescription());
        dto.setEvidenceUrls(report.getEvidenceUrls());
        dto.setStatus(report.getStatus());
        dto.setResolutionAction(report.getResolutionAction());
        dto.setAssignedTo(report.getAssignedTo());
        dto.setResolvedAt(report.getResolvedAt());
        dto.setDuplicateCount(report.getDuplicateCount());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public void setReporterId(UUID reporterId) {
        this.reporterId = reporterId;
    }

    public ReportSubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(ReportSubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public UUID getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEvidenceUrls() {
        return evidenceUrls;
    }

    public void setEvidenceUrls(String evidenceUrls) {
        this.evidenceUrls = evidenceUrls;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getResolutionAction() {
        return resolutionAction;
    }

    public void setResolutionAction(String resolutionAction) {
        this.resolutionAction = resolutionAction;
    }

    public UUID getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UUID assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public int getDuplicateCount() {
        return duplicateCount;
    }

    public void setDuplicateCount(int duplicateCount) {
        this.duplicateCount = duplicateCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
