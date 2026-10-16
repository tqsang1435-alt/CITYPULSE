-- V3__create_places_table.sql
-- Migration: Create places table for CITYPULSE / DANANG_VISTA Place domain

CREATE TABLE IF NOT EXISTS places (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(150) NOT NULL,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(50) NOT NULL,
    district VARCHAR(100) NOT NULL,
    address VARCHAR(300),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    status VARCHAR(30) NOT NULL DEFAULT 'PUBLISHED',
    short_description VARCHAR(500),
    long_description TEXT,
    thumbnail_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    price_min NUMERIC(12, 2),
    price_max NUMERIC(12, 2),
    visit_duration_min INTEGER,
    rating_average NUMERIC(3, 2) NOT NULL DEFAULT 0.00,
    review_count INTEGER NOT NULL DEFAULT 0,
    tags VARCHAR(300),
    best_time VARCHAR(100),
    data_quality VARCHAR(50) NOT NULL DEFAULT 'VERIFIED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_places_slug UNIQUE (slug),
    CONSTRAINT chk_places_status CHECK (status IN ('PUBLISHED', 'DRAFT', 'ARCHIVED'))
);

-- Indexes for search, filter, and sorting
CREATE INDEX IF NOT EXISTS idx_places_status ON places (status);
CREATE INDEX IF NOT EXISTS idx_places_category ON places (category);
CREATE INDEX IF NOT EXISTS idx_places_district ON places (district);
CREATE INDEX IF NOT EXISTS idx_places_rating ON places (rating_average DESC);
CREATE INDEX IF NOT EXISTS idx_places_created_at ON places (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_places_price_min ON places (price_min);
CREATE INDEX IF NOT EXISTS idx_places_slug ON places (slug);
