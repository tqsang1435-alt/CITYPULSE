package com.citypulse.moderation.dto;

import com.citypulse.moderation.entity.ModerationAction;
import com.citypulse.moderation.entity.ModerationActionType;

import java.time.Instant;
import java.util.UUID;

public class ModerationActionResponse {

    private UUID id;
    private UUID reportId;
    private UUID moderatorId;
    private String subjectType;
    private UUID subjectId;
    private ModerationActionType action;
    private String reason;
    private String internalNotes;
    private Instant createdAt;

    public ModerationActionResponse() {
    }

    public static ModerationActionResponse fromEntity(ModerationAction entity) {
        ModerationActionResponse dto = new ModerationActionResponse();
        dto.setId(entity.getId());
        dto.setReportId(entity.getReportId());
        dto.setModeratorId(entity.getModeratorId());
        dto.setSubjectType(entity.getSubjectType());
        dto.setSubjectId(entity.getSubjectId());
        dto.setAction(entity.getAction());
        dto.setReason(entity.getReason());
        dto.setInternalNotes(entity.getInternalNotes());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
    }

    public UUID getModeratorId() {
        return moderatorId;
    }

    public void setModeratorId(UUID moderatorId) {
        this.moderatorId = moderatorId;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public UUID getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(UUID subjectId) {
        this.subjectId = subjectId;
    }

    public ModerationActionType getAction() {
        return action;
    }

    public void setAction(ModerationActionType action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
