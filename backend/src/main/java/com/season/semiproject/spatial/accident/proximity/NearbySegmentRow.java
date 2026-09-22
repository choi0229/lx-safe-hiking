package com.season.semiproject.spatial.accident.proximity;

public class NearbySegmentRow {
    private Long segmentId;
    private Long trailId;
    private String sourceCourseName;
    private Long sourceTrailFeatureId;
    private double distanceMeters;

    public Long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Long segmentId) {
        this.segmentId = segmentId;
    }

    public Long getTrailId() {
        return trailId;
    }

    public void setTrailId(Long trailId) {
        this.trailId = trailId;
    }

    public String getSourceCourseName() {
        return sourceCourseName;
    }

    public void setSourceCourseName(String sourceCourseName) {
        this.sourceCourseName = sourceCourseName;
    }

    public Long getSourceTrailFeatureId() {
        return sourceTrailFeatureId;
    }

    public void setSourceTrailFeatureId(Long sourceTrailFeatureId) {
        this.sourceTrailFeatureId = sourceTrailFeatureId;
    }

    public double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }
}
