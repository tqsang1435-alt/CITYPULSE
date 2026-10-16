-- V8__seed_admin_moderation_data.sql
-- Subsystem: Seed Data for Admin, Moderation, Report & Audit Log (TV5 - Week 2)
-- Idempotent insertions with conflict handling

-- 1. Seed Administrative and Representative Accounts
-- Passwords BCrypt hashed for 'Password@123'
INSERT INTO users (id, email, password_hash, display_name, role, status, created_at, updated_at)
VALUES
(
    '00000000-0000-0000-0000-000000000001',
    'admin@danang.vn',
    '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgMTGsWD10gkpvzPWqcHsiMmkG.',
    'Quản trị viên Hệ thống',
    'ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '00000000-0000-0000-0000-000000000002',
    'mod@danang.vn',
    '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgMTGsWD10gkpvzPWqcHsiMmkG.',
    'Kiểm duyệt viên Nội dung',
    'CONTENT_ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '00000000-0000-0000-0000-000000000003',
    'sysadmin@danang.vn',
    '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgMTGsWD10gkpvzPWqcHsiMmkG.',
    'Kỹ thuật viên Vận hành',
    'SYSTEM_ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '00000000-0000-0000-0000-000000000004',
    'demo.user@danang.vn',
    '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgMTGsWD10gkpvzPWqcHsiMmkG.',
    'Nguyễn Văn Trải Nghiệm',
    'USER',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    '00000000-0000-0000-0000-000000000005',
    'locked.user@danang.vn',
    '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgMTGsWD10gkpvzPWqcHsiMmkG.',
    'Tài khoản Vi Phạm',
    'USER',
    'LOCKED',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO UPDATE 
SET role = EXCLUDED.role, status = EXCLUDED.status;

-- 2. Seed Representative Reports (OPEN, IN_REVIEW, RESOLVED)
-- Associating with seeded places from V4 if present
INSERT INTO reports (
    id, reporter_id, subject_type, subject_id, reason, description,
    status, resolution_action, assigned_to, resolved_at, duplicate_count, created_at, updated_at
)
SELECT 
    '10000000-0000-0000-0000-000000000001'::uuid,
    '00000000-0000-0000-0000-000000000004'::uuid,
    'PLACE',
    COALESCE(p.id, '99999999-0000-0000-0000-000000000001'::uuid),
    'MISINFORMATION',
    'Thông tin giờ mở cửa ngắm bình minh cần cập nhật lại cho mùa đông (05:30 thay vì 05:00).',
    'OPEN',
    NULL,
    NULL,
    NULL,
    1,
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    CURRENT_TIMESTAMP - INTERVAL '2 hours'
FROM (SELECT id FROM places WHERE slug = 'bai-bien-my-khe' LIMIT 1) p
UNION ALL
SELECT
    '10000000-0000-0000-0000-000000000002'::uuid,
    '00000000-0000-0000-0000-000000000004'::uuid,
    'PLACE',
    COALESCE(p.id, '99999999-0000-0000-0000-000000000002'::uuid),
    'SPAM',
    'Phản ánh có đối tượng để lại số điện thoại cò mồi xe đưa đón trong phần đánh giá.',
    'IN_REVIEW',
    NULL,
    '00000000-0000-0000-0000-000000000002'::uuid,
    NULL,
    2,
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '30 minutes'
FROM (SELECT id FROM places WHERE slug = 'ba-na-hills' LIMIT 1) p
UNION ALL
SELECT
    '10000000-0000-0000-0000-000000000003'::uuid,
    '00000000-0000-0000-0000-000000000004'::uuid,
    'PLACE',
    COALESCE(p.id, '99999999-0000-0000-0000-000000000003'::uuid),
    'INAPPROPRIATE',
    'Thông báo lịch phun lửa Cầu Rồng tuần này đổi giờ sang 21:15.',
    'RESOLVED',
    'VERIFIED_UPDATED',
    '00000000-0000-0000-0000-000000000002'::uuid,
    CURRENT_TIMESTAMP - INTERVAL '1 hour',
    1,
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    CURRENT_TIMESTAMP - INTERVAL '1 hour'
FROM (SELECT id FROM places WHERE slug = 'cau-rong' LIMIT 1) p
ON CONFLICT (id) DO NOTHING;

-- 3. Seed Moderation Actions (Audit of past decisions)
INSERT INTO moderation_actions (
    id, report_id, moderator_id, subject_type, subject_id, action, reason, internal_notes, created_at
)
SELECT 
    '20000000-0000-0000-0000-000000000001'::uuid,
    '10000000-0000-0000-0000-000000000003'::uuid,
    '00000000-0000-0000-0000-000000000002'::uuid,
    'PLACE',
    COALESCE(p.id, '99999999-0000-0000-0000-000000000003'::uuid),
    'APPROVE',
    'Đã đối chiếu thông cáo chính thức từ Sở Du lịch Đà Nẵng, cập nhật giờ phun lửa.',
    'Xử lý hoàn tất theo báo cáo từ người dùng.',
    CURRENT_TIMESTAMP - INTERVAL '1 hour'
FROM (SELECT id FROM places WHERE slug = 'cau-rong' LIMIT 1) p
ON CONFLICT (id) DO NOTHING;

-- 4. Seed Audit Logs (Illustrative operational logs)
INSERT INTO audit_logs (
    id, actor_id, actor_email, action, entity_type, entity_id, before_state, after_state,
    reason, request_id, ip_address, user_agent, created_at
)
VALUES
(
    '30000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'admin@danang.vn',
    'ADMIN_USER_LOCK',
    'USER',
    '00000000-0000-0000-0000-000000000005',
    '{"status": "ACTIVE", "role": "USER"}'::jsonb,
    '{"status": "LOCKED", "role": "USER"}'::jsonb,
    'Khóa tài khoản do phát tán liên kết lừa đảo nhiều lần (BR-163).',
    'req-init-audit-001',
    '127.0.0.1',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',
    CURRENT_TIMESTAMP - INTERVAL '12 hours'
),
(
    '30000000-0000-0000-0000-000000000002',
    '00000000-0000-0000-0000-000000000002',
    'mod@danang.vn',
    'REPORT_RESOLVE',
    'REPORT',
    '10000000-0000-0000-0000-000000000003',
    '{"status": "IN_REVIEW"}'::jsonb,
    '{"status": "RESOLVED", "resolution_action": "VERIFIED_UPDATED"}'::jsonb,
    'Xác nhận thông tin báo cáo chính xác, hoàn tất cập nhật dữ liệu.',
    'req-init-audit-002',
    '127.0.0.1',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64)',
    CURRENT_TIMESTAMP - INTERVAL '1 hour'
)
ON CONFLICT (id) DO NOTHING;
