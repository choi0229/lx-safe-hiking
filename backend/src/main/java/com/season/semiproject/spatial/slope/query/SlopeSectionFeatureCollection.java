package com.season.semiproject.spatial.slope.query;

import java.util.List;

public final class SlopeSectionFeatureCollection {
    private final String type = "FeatureCollection";
    private final long trailId;
    private final int windowMeters;
    private final String estimatedElevationSource =
            "GeoJSON properties.DN (unverified DEM-derived representative elevation, not a measured elevation)";
    private final List<SlopeSectionFeature> features;

    public SlopeSectionFeatureCollection(long trailId, int windowMeters, List<SlopeSectionFeature> features) {
        this.trailId = trailId;
        this.windowMeters = windowMeters;
        this.features = features;
    }

    public String getType() {
        return type;
    }

    public long getTrailId() {
        return trailId;
    }

    public int getWindowMeters() {
        return windowMeters;
    }

    public String getEstimatedElevationSource() {
        return estimatedElevationSource;
    }

    public List<SlopeSectionFeature> getFeatures() {
        return features;
    }
}
