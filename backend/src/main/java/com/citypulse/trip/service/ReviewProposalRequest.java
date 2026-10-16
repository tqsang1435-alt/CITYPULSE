package com.citypulse.trip.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ReviewProposalRequest(
        @NotBlank(message = "Hành động duyệt không được để trống")
        @Pattern(regexp = "(?i)^(approved|rejected)$", message = "Hành động chỉ có thể là 'approved' hoặc 'rejected'")
        String action,

        @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
        String reviewNote
) {}

