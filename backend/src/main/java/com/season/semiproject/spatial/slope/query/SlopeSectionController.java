package com.season.semiproject.spatial.slope.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Network-based fixed-distance SlopeSection GeoJSON API, backed by the precomputed
 * `slope_section` Derived Analysis Layer -- this Controller and {@link SlopeSectionService} never
 * compute a SlopeSection at request time. Consumed by the 4 slope Frontend screens (Base Trail
 * Layer + Slope Overlay).
 */
@RestController
@RequestMapping("/api/spatial/trails")
public class SlopeSectionController {

    private final SlopeSectionService service;

    @Autowired
    public SlopeSectionController(SlopeSectionService service) {
        this.service = service;
    }

    @GetMapping("/{trailId}/slope-sections")
    public ResponseEntity<?> slopeSections(
            @PathVariable long trailId,
            @RequestParam int windowMeters) {
        if (!SlopeSectionService.ALLOWED_WINDOW_METERS.contains(windowMeters)) {
            return ResponseEntity.badRequest()
                    .body("windowMeters must be one of " + SlopeSectionService.ALLOWED_WINDOW_METERS);
        }
        if (!service.trailExists(trailId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.getSlopeSections(trailId, windowMeters));
    }
}
