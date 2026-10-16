-- V2: Domain tables for User, Trip, TripMembership, TripInvitation
-- Áp dụng cho UC-01 (TV3): Gửi lời mời tham gia chuyến đi

-- Bảng người dùng
CREATE TABLE IF NOT EXISTS app_user (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(254) NOT NULL UNIQUE,
    display_name VARCHAR(120) NOT NULL,
    -- password_hash sẽ được thêm khi implement Auth (TV khác)
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Bảng chuyến đi
CREATE TABLE IF NOT EXISTS trip (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    owner_id    BIGINT NOT NULL REFERENCES app_user(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Bảng lời mời tham gia chuyến đi
-- UC-01: một lời mời = (trip_id, invitee_email, status)
-- Ràng buộc UNIQUE (trip_id, invitee_email) WHERE status = 'pending'
-- dùng partial unique index để tránh duplicate pending invitation.
CREATE TABLE IF NOT EXISTS trip_invitation (
    id              BIGSERIAL PRIMARY KEY,
    trip_id         BIGINT NOT NULL REFERENCES trip(id) ON DELETE CASCADE,
    invited_by_id   BIGINT NOT NULL REFERENCES app_user(id),
    invitee_email   VARCHAR(254) NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'pending'
                        CHECK (status IN ('pending', 'accepted', 'declined', 'expired')),
    expires_at      TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Partial unique index: chỉ có tối đa 1 lời mời pending cho mỗi (trip, email)
-- Đây là cơ chế DB-level để tránh race condition ngay cả khi nhiều request đến cùng lúc.
CREATE UNIQUE INDEX IF NOT EXISTS uidx_trip_invitation_pending
    ON trip_invitation (trip_id, invitee_email)
    WHERE status = 'pending';

-- Bảng thành viên (chuẩn bị cho module Membership - TV3, không implement ở UC-01)
CREATE TABLE IF NOT EXISTS trip_membership (
    id          BIGSERIAL PRIMARY KEY,
    trip_id     BIGINT NOT NULL REFERENCES trip(id) ON DELETE CASCADE,
    user_id     BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role        VARCHAR(20) NOT NULL DEFAULT 'member'
                    CHECK (role IN ('owner', 'member')),
    joined_at   TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uidx_trip_membership_user UNIQUE (trip_id, user_id)
);
