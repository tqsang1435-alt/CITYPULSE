package com.citypulse.moderation.service;

import com.citypulse.moderation.dto.ModerationActionResponse;
import com.citypulse.moderation.dto.ModerationDecisionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ModerationService {

    ModerationActionResponse executeDecision(UUID moderatorId, String subjectType, UUID subjectId, ModerationDecisionRequest request);

    Page<ModerationActionResponse> getModerationHistory(Pageable pageable);
}
