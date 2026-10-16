package com.citypulse.trip.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProposalRequest(
        Long placeId,

        @NotBlank(message = "Tên địa điểm không được để trống")
        @Size(max = 200, message = "Tên địa điểm không được vượt quá 200 ký tự")
        String placeName,

        @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
        String description
) {}

