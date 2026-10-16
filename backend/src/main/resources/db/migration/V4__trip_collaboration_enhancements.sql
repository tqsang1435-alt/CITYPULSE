-- V4: Trip Collaboration enhancements for Invitation, Membership, and Proposal (TV3)

-- 1. Update trip_invitation: add 'cancelled' status and token column
ALTER TABLE trip_invitation
    DROP CONSTRAINT IF EXISTS trip_invitation_status_check;

ALTER TABLE trip_invitation
    ADD CONSTRAINT trip_invitation_status_check
    CHECK (status IN ('pending', 'accepted', 'declined', 'expired', 'cancelled'));

ALTER TABLE trip_invitation
    ADD COLUMN IF NOT EXISTS token VARCHAR(64);

CREATE UNIQUE INDEX IF NOT EXISTS uidx_trip_invitation_token
    ON trip_invitation(token)
    WHERE token IS NOT NULL;

-- 2. Update trip_membership: allow 'Owner', 'Contributor', 'Viewer' roles
ALTER TABLE trip_membership
    DROP CONSTRAINT IF EXISTS trip_membership_role_check;

ALTER TABLE trip_membership
    ADD CONSTRAINT trip_membership_role_check
    CHECK (role IN ('Owner', 'Contributor', 'Viewer', 'owner', 'member'));

ALTER TABLE trip_membership
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 3. Create trip_proposal table for place recommendations (UC-05, UC-06)
CREATE TABLE IF NOT EXISTS trip_proposal (
    id              BIGSERIAL PRIMARY KEY,
    trip_id         BIGINT NOT NULL REFERENCES trip(id) ON DELETE CASCADE,
    proposed_by_id  BIGINT NOT NULL REFERENCES app_user(id),
    place_id        BIGINT NULL,
    place_name      VARCHAR(200) NOT NULL,
    description     TEXT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'pending'
                        CHECK (status IN ('pending', 'approved', 'rejected')),
    reviewed_by_id  BIGINT NULL REFERENCES app_user(id),
    reviewed_at     TIMESTAMPTZ NULL,
    review_note     VARCHAR(500) NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_trip_proposal_trip_id ON trip_proposal(trip_id);
CREATE INDEX IF NOT EXISTS idx_trip_proposal_status ON trip_proposal(trip_id, status);
CREATE INDEX IF NOT EXISTS idx_trip_proposal_proposed_by ON trip_proposal(proposed_by_id);

