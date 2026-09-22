package com.season.semiproject.spatial.slope.calc;

/**
 * One representative "DEM-derived estimated elevation" sample placed on a {@link NetworkChain}'s
 * distance axis, at {@code distanceAlongChainMeters}. {@code sourceDn} is the parent TrailFeature's
 * raw GeoJSON {@code properties.DN}; it is never treated as a measured/exact elevation.
 *
 * One sample per (TrailFeature, chain) pair -- never one per TrailSegment. A TrailFeature that
 * was split into multiple TrailSegments (256 of 1706 Features in the current dataset) contributes
 * exactly one sample per chain it appears in, positioned at the length-weighted midpoint of just
 * the Segments of that Feature that fall in that chain, so identical DN values are never
 * duplicated near each other on the same chain (which would otherwise distort interpolation).
 */
public final class ElevationSample {

    private final long sourceTrailFeatureId;
    private final double distanceAlongChainMeters;
    private final double sourceDn;

    public ElevationSample(long sourceTrailFeatureId, double distanceAlongChainMeters, double sourceDn) {
        this.sourceTrailFeatureId = sourceTrailFeatureId;
        this.distanceAlongChainMeters = distanceAlongChainMeters;
        this.sourceDn = sourceDn;
    }

    public long getSourceTrailFeatureId() {
        return sourceTrailFeatureId;
    }

    public double getDistanceAlongChainMeters() {
        return distanceAlongChainMeters;
    }

    public double getSourceDn() {
        return sourceDn;
    }
}
