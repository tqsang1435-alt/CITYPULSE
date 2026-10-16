-- V7__create_moderation_report_audit_tables.sql
-- Subsystem: Moderation, Report & Admin (TV5 - Week 2)
-- Dependencies: users (V2), places (V3)

-- 1. Table: reports (Báo cáo vi phạm từ người dùng)
CREATE TABLE IF NOT EXISTS reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    reporter_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    subject_type VARCHAR(50) NOT NULL,
    subject_id UUID NOT NULL,
    reason VARCHAR(100) NOT NULL,
    description TEXT,
    evidence_urls VARCHAR(1000),
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    resolution_action VARCHAR(50),
    assigned_to UUID REFERENCES users(id) ON DELETE SET NULL,
    resolved_at TIMESTAMPTZ,
    duplicate_count INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_reports_subject_type CHECK (subject_type IN ('PLACE', 'REVIEW', 'POST', 'SUBMISSION', 'MEDIA')),
    CONSTRAINT chk_reports_status CHECK (status IN ('OPEN', 'IN_REVIEW', 'RESOLVED', 'REJECTED')),
    CONSTRAINT chk_reports_duplicate_count CHECK (duplicate_count >= 1)
);

CREATE INDEX IF NOT EXISTS idx_reports_status ON reports (status);
CREATE INDEX IF NOT EXISTS idx_reports_subject ON reports (subject_type, subject_id);
CREATE INDEX IF NOT EXISTS idx_reports_reporter_created ON reports (reporter_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_reports_assigned_to ON reports (assigned_to);

-- 2. Table: moderation_actions (Phiên quyết định kiểm duyệt)
CREATE TABLE IF NOT EXISTS moderation_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id UUID REFERENCES reports(id) ON DELETE SET NULL,
    moderator_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    subject_type VARCHAR(50) NOT NULL,
    subject_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    reason TEXT NOT NULL,
    internal_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_mod_actions_subject_type CHECK (subject_type IN ('PLACE', 'REVIEW', 'POST', 'SUBMISSION')),
    CONSTRAINT chk_mod_actions_action CHECK (action IN ('APPROVE', 'REJECT', 'HIDE', 'RESTORE', 'WARN', 'DISMISS'))
);

CREATE INDEX IF NOT EXISTS idx_mod_actions_subject ON moderation_actions (subject_type, subject_id);
CREATE INDEX IF NOT EXISTS idx_mod_actions_moderator ON moderation_actions (moderator_id, created_at DESC);

-- 3. Table: audit_logs (Nhật ký kiểm toán hệ thống bất biến)
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id UUID REFERENCES users(id) ON DELETE SET NULL,
    actor_email VARCHAR(255) NOT NULL,
    action VARCHAR(60) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    before_state JSONB,
    after_state JSONB NOT NULL,
    reason TEXT NOT NULL,
    request_id VARCHAR(64),
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audit_logs_actor ON audit_logs (actor_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit_logs (entity_type, entity_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs (created_at DESC);
