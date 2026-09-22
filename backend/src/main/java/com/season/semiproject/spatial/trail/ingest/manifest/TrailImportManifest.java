package com.season.semiproject.spatial.trail.ingest.manifest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Root object of trail-import-manifest.json. Loaded once per import run; never generated or
 * inferred at runtime -- every value in it was hand-verified against the actual GeoJSON file
 * and the live `course` table.
 */
public class TrailImportManifest {

    private String sourceFile;
    private List<TrailManifestEntry> trails;
    private List<ExcludedFeatureEntry> excludedFeatures;

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<TrailManifestEntry> getTrails() {
        return trails;
    }

    public void setTrails(List<TrailManifestEntry> trails) {
        this.trails = trails;
    }

    public List<ExcludedFeatureEntry> getExcludedFeatures() {
        return excludedFeatures;
    }

    public void setExcludedFeatures(List<ExcludedFeatureEntry> excludedFeatures) {
        this.excludedFeatures = excludedFeatures;
    }

    public Map<String, TrailManifestEntry> trailsByPmntnNm() {
        return trails.stream().collect(Collectors.toMap(TrailManifestEntry::getPmntnNm, t -> t));
    }

    public Map<Integer, ExcludedFeatureEntry> excludedByIndex() {
        return excludedFeatures.stream()
                .collect(Collectors.toMap(ExcludedFeatureEntry::getSourceFeatureIndex, e -> e));
    }
}
