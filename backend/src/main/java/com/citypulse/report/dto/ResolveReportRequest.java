package com.citypulse.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResolveReportRequest {

    @NotBlank(message = "Hành động giải quyết (resolutionAction) là bắt buộc")
    @Size(max = 50)
    private String resolutionAction;

    @NotBlank(message = "Lý do giải quyết không được để trống")
    @Size(min = 5, max = 1000, message = "Lý do giải quyết phải từ 5 đến 1000 ký tự")
    private String reason;

    public ResolveReportRequest() {
    }

    public ResolveReportRequest(String resolutionAction, String reason) {
        this.resolutionAction = resolutionAction;
        this.reason = reason;
    }

    public String getResolutionAction() {
        return resolutionAction;
    }

    public void setResolutionAction(String resolutionAction) {
        this.resolutionAction = resolutionAction;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
