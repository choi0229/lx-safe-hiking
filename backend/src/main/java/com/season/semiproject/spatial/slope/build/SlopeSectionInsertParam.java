package com.season.semiproject.spatial.slope.build;

public final class SlopeSectionInsertParam {
    private final long trailId;
    private final int chainSequence;
    private final int sectionSequence;
    private final int windowM;
    private final double distanceM;
    private final double estimatedElevationStart;
    private final double estimatedElevationEnd;
    private final double estimatedElevationDelta;
    private final double estimatedSlopePercent;
    private final String dataQualityFlag;
    private final String wkt;

    public SlopeSectionInsertParam(long trailId, int chainSequence, int sectionSequence, int windowM,
            double distanceM, double estimatedElevationStart, double estimatedElevationEnd,
            double estimatedElevationDelta, double estimatedSlopePercent, String dataQualityFlag, String wkt) {
        this.trailId = trailId;
        this.chainSequence = chainSequence;
        this.sectionSequence = sectionSequence;
        this.windowM = windowM;
        this.distanceM = distanceM;
        this.estimatedElevationStart = estimatedElevationStart;
        this.estimatedElevationEnd = estimatedElevationEnd;
        this.estimatedElevationDelta = estimatedElevationDelta;
        this.estimatedSlopePercent = estimatedSlopePercent;
        this.dataQualityFlag = dataQualityFlag;
        this.wkt = wkt;
    }

    public long getTrailId() {
        return trailId;
    }

    public int getChainSequence() {
        return chainSequence;
    }

    public int getSectionSequence() {
        return sectionSequence;
    }

    public int getWindowM() {
        return windowM;
    }

    public double getDistanceM() {
        return distanceM;
    }

    public double getEstimatedElevationStart() {
        return estimatedElevationStart;
    }

    public double getEstimatedElevationEnd() {
        return estimatedElevationEnd;
    }

    public double getEstimatedElevationDelta() {
        return estimatedElevationDelta;
    }

    public double getEstimatedSlopePercent() {
        return estimatedSlopePercent;
    }

    public String getDataQualityFlag() {
        return dataQualityFlag;
    }

    public String getWkt() {
        return wkt;
    }
}
