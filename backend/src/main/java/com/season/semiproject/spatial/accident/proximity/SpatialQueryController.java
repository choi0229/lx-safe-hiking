package com.season.semiproject.spatial.accident.proximity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Spatial proximity API between AccidentPoint (Raw) and TrailSegment (Derived Network).
 * `distanceMeters` is always a caller-chosen search radius -- this API never hardcodes or implies
 * a "risk radius".
 *
 * API A/B (Accident<->Segment, single id in, single id out) are not wired into any Frontend
 * view. API C ({@code /trails/{trailId}/nearby-accidents}) IS consumed by RecordView.vue's
 * real-time proximity alert -- PostGIS only selects the Trail-level candidate set here; the live
 * user-location comparison against those candidates stays a Frontend/Kakao Maps distance
 * concern.
 */
@RestController
@RequestMapping("/api/spatial")
public class SpatialQueryController {

    private final SpatialQueryService service;

    @Autowired
    public SpatialQueryController(SpatialQueryService service) {
        this.service = service;
    }

    @GetMapping("/accidents/{accidentId}/nearby-segments")
    public ResponseEntity<?> nearbySegments(
            @PathVariable Long accidentId,
            @RequestParam double distanceMeters) {
        if (distanceMeters <= 0) {
            return ResponseEntity.badRequest().body("distanceMeters must be greater than 0");
        }
        if (!service.accidentExists(accidentId)) {
            return ResponseEntity.notFound().build();
        }
        List<NearbySegmentRow> result = service.findSegmentsNearAccident(accidentId, distanceMeters);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trail-segments/{segmentId}/nearby-accidents")
    public ResponseEntity<?> nearbyAccidents(
            @PathVariable Long segmentId,
            @RequestParam double distanceMeters) {
        if (distanceMeters <= 0) {
            return ResponseEntity.badRequest().body("distanceMeters must be greater than 0");
        }
        if (!service.segmentExists(segmentId)) {
            return ResponseEntity.notFound().build();
        }
        List<NearbyAccidentRow> result = service.findAccidentsNearSegment(segmentId, distanceMeters);
        return ResponseEntity.ok(result);
    }

    /** API C: Trail -> distinct nearby AccidentPoint candidates (one Feature per accidentId,
     * closest TrailSegment distance kept), as a GeoJSON FeatureCollection<Point>. Consumed by
     * RecordView's real-time proximity alert (PostGIS only selects candidates near the Trail;
     * the live user-location comparison stays a Frontend/Kakao-distance concern). */
    @GetMapping("/trails/{trailId}/nearby-accidents")
    public ResponseEntity<?> nearbyAccidentsForTrail(
            @PathVariable long trailId,
            @RequestParam double distanceMeters) {
        if (distanceMeters <= 0) {
            return ResponseEntity.badRequest().body("distanceMeters must be greater than 0");
        }
        if (!service.trailExists(trailId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.findAccidentCandidatesForTrail(trailId, distanceMeters));
    }
}
