package com.citypulse.moderation.dto;

import com.citypulse.moderation.entity.ModerationActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class ModerationDecisionRequest {

    private UUID reportId;

    @NotNull(message = "Hành động kiểm duyệt (action) là bắt buộc")
    private ModerationActionType action;

    @NotBlank(message = "Lý do kiểm duyệt không được để trống")
    @Size(min = 5, max = 2000, message = "Lý do kiểm duyệt phải từ 5 đến 2000 ký tự")
    private String reason;

    @Size(max = 2000, message = "Ghi chú nội bộ không được vượt quá 2000 ký tự")
    private String internalNotes;

    public ModerationDecisionRequest() {
    }

    public ModerationDecisionRequest(UUID reportId, ModerationActionType action, String reason, String internalNotes) {
        this.reportId = reportId;
        this.action = action;
        this.reason = reason;
        this.internalNotes = internalNotes;
    }

    public UUID getReportId() {
        return reportId;
    }

    public void setReportId(UUID reportId) {
        this.reportId = reportId;
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
}
