package com.citypulse.report.dto;

import com.citypulse.report.entity.ReportSubjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class CreateReportRequest {

    @NotNull(message = "Loại đối tượng báo cáo (subjectType) là bắt buộc")
    private ReportSubjectType subjectType;

    @NotNull(message = "Mã đối tượng (subjectId) là bắt buộc")
    private UUID subjectId;

    @NotBlank(message = "Lý do báo cáo không được để trống")
    @Size(min = 3, max = 100, message = "Lý do báo cáo phải từ 3 đến 100 ký tự")
    private String reason;

    @Size(max = 2000, message = "Mô tả chi tiết không được vượt quá 2000 ký tự")
    private String description;

    @Size(max = 1000, message = "Đường dẫn bằng chứng không được vượt quá 1000 ký tự")
    private String evidenceUrls;

    public CreateReportRequest() {
    }

    public CreateReportRequest(ReportSubjectType subjectType, UUID subjectId, String reason, String description, String evidenceUrls) {
        this.subjectType = subjectType;
        this.subjectId = subjectId;
        this.reason = reason;
        this.description = description;
        this.evidenceUrls = evidenceUrls;
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
}
