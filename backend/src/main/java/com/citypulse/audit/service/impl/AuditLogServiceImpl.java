package com.citypulse.audit.service.impl;

import com.citypulse.audit.dto.AuditLogResponse;
import com.citypulse.audit.entity.AuditLog;
import com.citypulse.audit.repository.AuditLogRepository;
import com.citypulse.audit.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLogResponse recordLog(UUID actorId, String actorEmail, String action, String entityType,
                                     UUID entityId, String beforeState, String afterState,
                                     String reason, String requestId, String ipAddress, String userAgent) {
        AuditLog auditLog = new AuditLog(
            actorId,
            actorEmail != null ? actorEmail : "system",
            action,
            entityType,
            entityId,
            beforeState,
            afterState != null ? afterState : "{}",
            reason,
            requestId,
            ipAddress,
            userAgent
        );
        AuditLog saved = auditLogRepository.save(auditLog);
        return AuditLogResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(AuditLogResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogsByActor(UUID actorId, Pageable pageable) {
        return auditLogRepository.findByActorIdOrderByCreatedAtDesc(actorId, pageable)
            .map(AuditLogResponse::fromEntity);
    }
}
