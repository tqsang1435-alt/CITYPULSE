package com.citypulse.admin.controller;

import com.citypulse.admin.dto.AdminStatsOverviewResponse;
import com.citypulse.admin.service.AdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SYSTEM_ADMIN', 'ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsOverviewResponse> getStatsOverview() {
        return ResponseEntity.ok(adminDashboardService.getStatsOverview());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getAdminHealth() {
        return ResponseEntity.ok(Map.of(
            "subsystem", "ADMIN_MODERATION_REPORT",
            "status", "HEALTHY",
            "version", "1.0-week2"
        ));
    }
}
