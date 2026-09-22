package com.season.semiproject.spatial.trail.geojson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Serves the Validated Raw Spatial Layer (`trail_feature`) as a GeoJSON FeatureCollection for
 * ordinary (non-slope) Frontend map rendering.
 *
 * This is intentionally NOT the source for the 4 Legacy Slope Compatibility views
 * (MountainDetailView.vue, CompareCourseView.vue, MountainDetailView2.vue,
 * MobileMountainDetailView.vue) -- those keep reading the original static GeoJSON file, because
 * the groupCoordinates() position shift causes large legacy slope color differences when fed
 * this DB-derived source.
 */
@Service
public class TrailGeoJsonService {

    private final TrailGeoJsonDAO dao;

    @Autowired
    public TrailGeoJsonService(TrailGeoJsonDAO dao) {
        this.dao = dao;
    }

    /** Already a complete, valid GeoJSON FeatureCollection JSON string (never null -- see mapper). */
    public String getTrailFeatureCollectionJson() {
        return dao.getTrailFeatureCollectionJson();
    }
}
