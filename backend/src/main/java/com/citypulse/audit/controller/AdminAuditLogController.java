package com.citypulse.audit.controller;

import com.citypulse.audit.dto.AuditLogResponse;
import com.citypulse.audit.service.AuditLogService;
import com.citypulse.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ADMIN')")
public class AdminAuditLogController {

    private final AuditLogService auditLogService;

    public AdminAuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<AuditLogResponse>> getAuditLogs(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.fromPage(auditLogService.getAuditLogs(pageable)));
    }

    @GetMapping("/actor/{actorId}")
    public ResponseEntity<PageResponse<AuditLogResponse>> getAuditLogsByActor(
            @PathVariable UUID actorId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(PageResponse.fromPage(auditLogService.getAuditLogsByActor(actorId, pageable)));
    }
}
