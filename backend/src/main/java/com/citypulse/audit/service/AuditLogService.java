package com.citypulse.audit.service;

import com.citypulse.audit.dto.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuditLogService {

    AuditLogResponse recordLog(UUID actorId, String actorEmail, String action, String entityType,
                              UUID entityId, String beforeState, String afterState,
                              String reason, String requestId, String ipAddress, String userAgent);

    Page<AuditLogResponse> getAuditLogs(Pageable pageable);

    Page<AuditLogResponse> getAuditLogsByActor(UUID actorId, Pageable pageable);
}
