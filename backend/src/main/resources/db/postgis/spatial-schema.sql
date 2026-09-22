-- Spatial (PostGIS) schema. This is NEW schema that did not exist in the original MySQL
-- project -- it is not a restoration of legacy structure, unlike
-- backend/src/main/resources/db/postgresql/schema.sql. Design notes: Trail/TrailFeature are
-- separate from `course`, course_id is nullable, and TrailFeature = one original GeoJSON Feature
-- and is a Raw Spatial Layer object -- NOT a routing-network Segment. There are no
-- slope/elevation columns.
--
-- Raw Spatial Layer only (this file):
--   GeoJSON --> Trail --> TrailFeature
-- The Derived Network Layer (TrailNode / TrailSegment) lives in its own file,
-- db/postgis/network-schema.sql, specifically so THAT file's DROP statements never touch
-- trail/trail_feature. TrailFeature.geom remains the Raw Spatial Source of Truth even after
-- the network layer exists; the network layer is derived and regenerable from it, never a
-- replacement for it.
--
-- The GeoJSON import and the network build are NOT part of this file -- this file
-- only creates trail/trail_feature as empty tables.
--
-- Dependency and re-run order (important):
--   trail.course_id is a nullable FK to course(course_id), so `course` must already exist when
--   this file runs. The required full-reproduction order is:
--     1) docker compose up (PostgreSQL/PostGIS)
--     2) db/postgresql/schema.sql   (relational tables, includes `course`)
--     3) db/postgresql/seed.sql
--     4) db/postgis/spatial-schema.sql   (this file)
--     5) trail import (`spatial-import` profile) -- populates trail/trail_feature
--     6) db/postgis/network-schema.sql   -- trail_node/trail_segment (empty)
--     7) network build (`spatial-network-build` profile)
--
--   Known re-run hazard #1: db/postgresql/schema.sql drops `course` with CASCADE. If trail/
--   trail_feature already exist at that point, PostgreSQL's CASCADE will silently DROP the
--   `fk_trail_course` constraint on `trail` (it does NOT drop the `trail` table itself, since
--   `trail` doesn't depend on `course` for its own existence -- only the FK constraint does).
--   The practical rule: if you re-run db/postgresql/schema.sql on a database that already has
--   the spatial tables, immediately re-run this file afterward to recreate the FK.
--
--   Known re-run hazard #2 (applies once the network layer exists): THIS file's own `DROP TABLE trail_feature
--   CASCADE` now also cascades to `fk_trail_segment_trail_feature` on `trail_segment` if the
--   network layer has already been built -- it silently drops that constraint (trail_segment
--   rows survive, orphaned) without dropping trail_segment itself. More importantly, re-running
--   THIS file after the import has run wipes the real imported trail_feature rows (this file was
--   always "drop and recreate empty" by design -- that was harmless before any import existed,
--   but is now a real data-loss trap for anyone re-running it casually). The practical rule: if
--   you must re-run this file after the import/network build have run, you need to re-run the
--   trail importer AND db/postgis/network-schema.sql AND the network build again afterward, in
--   that order, to get back to a consistent state.
--
--   THIS FILE IS A DESTRUCTIVE DEV-ENVIRONMENT RESET SCRIPT, NOT A NON-DESTRUCTIVE MIGRATION.
--   Every run unconditionally drops `trail_feature` and `trail` and recreates them empty.
--   Re-running it is repeatable/idempotent in the sense that it always ends in the same empty
--   schema, but it is NOT safe for data preservation -- any previously imported Trail/
--   TrailFeature rows are deleted every time this script runs. Once the network layer exists
--   (TrailNode/TrailSegment reference trail_feature), re-running this file leaves it
--   inconsistent -- see hazard #2 above.

CREATE EXTENSION IF NOT EXISTS postgis;

DROP TABLE IF EXISTS trail_feature CASCADE;
DROP TABLE IF EXISTS trail CASCADE;

-- ============================================================================
-- Trail
--
-- Represents one independently-identified source trail chain from a GeoJSON file
-- (one (source_file, source_course_name) pair). It is NOT a child object of `course` --
-- the two are related only when that relationship has been explicitly verified (e.g. the
-- "마루" name-collision case). course_id stays NULL
-- until the import confirms a specific source trail actually corresponds to a specific
-- `course` row.
-- ============================================================================
CREATE TABLE trail (
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    course_id            INTEGER,
    source_file          VARCHAR(255) NOT NULL,
    source_mountain_name VARCHAR(50) NOT NULL,
    source_course_name   VARCHAR(50) NOT NULL,
    CONSTRAINT fk_trail_course
        FOREIGN KEY (course_id) REFERENCES course (course_id) ON DELETE SET NULL,
    CONSTRAINT uq_trail_source
        UNIQUE (source_file, source_course_name)
);

-- No geom column here on purpose -- TrailFeature.geom is the single Raw Spatial Source of Truth.
-- The full trail shape, when needed, is composed at query time. Verified pattern (the naive
-- ST_Collect(geom ORDER BY sequence) does NOT work here: each row's geom is already a MultiLineString, so collecting rows of
-- MultiLineString directly yields a GEOMETRYCOLLECTION, which ST_LineMerge silently returns
-- EMPTY for. ST_Dump must flatten to individual LineStrings first):
--   WITH parts AS (
--     SELECT sequence, (ST_Dump(geom)).geom AS line_geom
--     FROM trail_feature WHERE trail_id = ...
--   )
--   SELECT ST_LineMerge(ST_Collect(line_geom ORDER BY sequence)) FROM parts;

-- ============================================================================
-- TrailFeature (Raw Spatial Layer)
--
-- One row = one Feature from the original GeoJSON features[] array, stored 1:1, geometry and
-- all. This is a RAW SOURCE OBJECT, not a routing-network Segment: a GeoJSON Feature boundary
-- is an artifact of how the source data was produced/exported, and there is no guarantee it
-- lines up with a real intersection, branch point, or endpoint of the actual trail network
-- (e.g. another Feature can physically meet this one in the middle, not just at its endpoints).
-- Modeling real network connectivity (nodes, intersections, edges) is a separate, later step
-- -- TrailNode / TrailSegment, defined in network-schema.sql.
--
-- This is also NOT a distance-based (20m/30m) or rendering-based grouping.
-- `sequence` is the intended trail-order position (validated/assigned during import); source_feature_index
-- is the raw index into the original file's features[] array, kept for traceability even where
-- it may not match `sequence` one-to-one (e.g. if noise features are skipped during import).
-- dn_value is the original GeoJSON properties.DN value with its type converted only -- it is not
-- given an elevation/altitude/slope meaning.
-- ============================================================================
CREATE TABLE trail_feature (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    trail_id              BIGINT NOT NULL,
    sequence              INTEGER NOT NULL,
    source_feature_index  INTEGER NOT NULL,
    geom                  geometry(MultiLineString, 4326) NOT NULL,
    dn_value              NUMERIC NOT NULL,
    CONSTRAINT fk_trail_feature_trail
        FOREIGN KEY (trail_id) REFERENCES trail (id) ON DELETE CASCADE,
    CONSTRAINT chk_trail_feature_sequence
        CHECK (sequence >= 0),
    CONSTRAINT chk_trail_feature_source_index
        CHECK (source_feature_index >= 0),
    CONSTRAINT uq_trail_feature_sequence
        UNIQUE (trail_id, sequence),
    CONSTRAINT uq_trail_feature_source_index
        UNIQUE (trail_id, source_feature_index)
);

CREATE INDEX idx_trail_feature_geom ON trail_feature USING GIST (geom);
