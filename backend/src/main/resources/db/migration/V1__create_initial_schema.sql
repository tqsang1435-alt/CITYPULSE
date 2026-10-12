-- Initial database migration. Domain tables will be added after the ERD is confirmed.
CREATE TABLE IF NOT EXISTS app_schema_version_note (
    id BIGSERIAL PRIMARY KEY,
    note VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_schema_version_note (note)
VALUES ('CITYPULSE schema initialized');
