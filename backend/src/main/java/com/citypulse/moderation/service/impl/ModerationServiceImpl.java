package com.citypulse.moderation.service.impl;

import com.citypulse.audit.service.AuditLogService;
import com.citypulse.moderation.dto.ModerationActionResponse;
import com.citypulse.moderation.dto.ModerationDecisionRequest;
import com.citypulse.moderation.entity.ModerationAction;
import com.citypulse.moderation.repository.ModerationActionRepository;
import com.citypulse.moderation.service.ModerationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ModerationServiceImpl implements ModerationService {

    private final ModerationActionRepository moderationActionRepository;
    private final AuditLogService auditLogService;

    public ModerationServiceImpl(ModerationActionRepository moderationActionRepository,
                                 AuditLogService auditLogService) {
        this.moderationActionRepository = moderationActionRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    public ModerationActionResponse executeDecision(UUID moderatorId, String subjectType, UUID subjectId, ModerationDecisionRequest request) {
        ModerationAction action = new ModerationAction(
            request.getReportId(),
            moderatorId,
            subjectType,
            subjectId,
            request.getAction(),
            request.getReason(),
            request.getInternalNotes()
        );
        ModerationAction saved = moderationActionRepository.save(action);

        // Record Audit Log
        auditLogService.recordLog(
            moderatorId,
            "moderator",
            "MODERATE_" + subjectType + "_" + request.getAction(),
            subjectType,
            subjectId,
            null,
            "{\"action\": \"" + request.getAction() + "\", \"reason\": \"" + request.getReason() + "\"}",
            request.getReason(),
            null,
            null,
            null
        );

        return ModerationActionResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModerationActionResponse> getModerationHistory(Pageable pageable) {
        return moderationActionRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(ModerationActionResponse::fromEntity);
    }
}
