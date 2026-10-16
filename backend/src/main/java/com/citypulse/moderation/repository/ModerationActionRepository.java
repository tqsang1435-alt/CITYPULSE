package com.citypulse.moderation.repository;

import com.citypulse.moderation.entity.ModerationAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModerationActionRepository extends JpaRepository<ModerationAction, UUID> {

    Page<ModerationAction> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ModerationAction> findBySubjectTypeAndSubjectIdOrderByCreatedAtDesc(String subjectType, UUID subjectId);

    Page<ModerationAction> findByModeratorIdOrderByCreatedAtDesc(UUID moderatorId, Pageable pageable);
}
