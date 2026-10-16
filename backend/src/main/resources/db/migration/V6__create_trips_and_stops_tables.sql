-- V6__create_trips_and_stops_tables.sql
-- Migration: Create trips and trip_stops tables for CITYPULSE / DNVIBE Trip Planning domain
-- Numbered as V6 to follow TV1's V2 (users), V3 (places), V4 (seed places), V5 (favorites).

CREATE TABLE IF NOT EXISTS trips (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(2000),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    budget NUMERIC(12, 2),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_trips_dates CHECK (start_date <= end_date),
    CONSTRAINT chk_trips_budget CHECK (budget IS NULL OR budget >= 0),
    CONSTRAINT chk_trips_status CHECK (status IN ('DRAFT', 'ACTIVE', 'COMPLETED', 'ARCHIVED'))
);

CREATE INDEX IF NOT EXISTS idx_trips_owner ON trips (owner_id);
CREATE INDEX IF NOT EXISTS idx_trips_status ON trips (status);
CREATE INDEX IF NOT EXISTS idx_trips_start_date ON trips (start_date);

CREATE TABLE IF NOT EXISTS trip_stops (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    trip_id UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    place_id UUID,
    custom_name VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    day_number INT NOT NULL DEFAULT 1,
    order_index INT NOT NULL DEFAULT 0,
    arrival_time TIME,
    departure_time TIME,
    estimated_cost NUMERIC(12, 2),
    note VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_trip_stops_day_number CHECK (day_number >= 1),
    CONSTRAINT chk_trip_stops_order_index CHECK (order_index >= 0),
    CONSTRAINT chk_trip_stops_cost CHECK (estimated_cost IS NULL OR estimated_cost >= 0),
    CONSTRAINT chk_trip_stops_lat CHECK (latitude IS NULL OR (latitude >= -90.0 AND latitude <= 90.0)),
    CONSTRAINT chk_trip_stops_lng CHECK (longitude IS NULL OR (longitude >= -180.0 AND longitude <= 180.0)),
    CONSTRAINT chk_trip_stops_identity CHECK (place_id IS NOT NULL OR (custom_name IS NOT NULL AND length(trim(custom_name)) > 0))
);

CREATE INDEX IF NOT EXISTS idx_trip_stops_trip ON trip_stops (trip_id);
CREATE INDEX IF NOT EXISTS idx_trip_stops_day_order ON trip_stops (trip_id, day_number, order_index);

