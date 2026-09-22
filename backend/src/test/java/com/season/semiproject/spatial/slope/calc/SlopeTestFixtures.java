package com.season.semiproject.spatial.slope.calc;

import com.season.semiproject.spatial.slope.build.TrailSegmentElevationRow;

final class SlopeTestFixtures {

    private SlopeTestFixtures() {
    }

    static String lineStringGeoJson(double x1, double y1, double x2, double y2) {
        return String.format(
                "{\"type\":\"LineString\",\"coordinates\":[[%s,%s],[%s,%s]]}", x1, y1, x2, y2);
    }

    static TrailSegmentElevationRow segment(long segmentId, long fromNodeId, long toNodeId,
            double lengthMeters, long sourceTrailFeatureId, double sourceDn,
            double x1, double y1, double x2, double y2) {
        TrailSegmentElevationRow row = new TrailSegmentElevationRow();
        row.setSegmentId(segmentId);
        row.setFromNodeId(fromNodeId);
        row.setToNodeId(toNodeId);
        row.setLengthMeters(lengthMeters);
        row.setSourceTrailFeatureId(sourceTrailFeatureId);
        row.setSourceDn(sourceDn);
        row.setGeometryGeoJson(lineStringGeoJson(x1, y1, x2, y2));
        return row;
    }
}
