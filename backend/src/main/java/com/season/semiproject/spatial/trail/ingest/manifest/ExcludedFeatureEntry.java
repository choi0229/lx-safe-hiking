package com.season.semiproject.spatial.trail.ingest.manifest;

/**
 * A source Feature that the manifest author has explicitly decided to exclude from import,
 * identified by its absolute index in the original FeatureCollection.features[] array.
 *
 * category is one of:
 *   EXCLUDED_SOURCE_FEATURE       - structurally valid Feature whose PMNTN_NM does not match
 *                                    any declared Trail (e.g. blank label).
 *   INVALID_GEOMETRY_SOURCE_FEATURE - Feature that fails structural geometry validation
 *                                    (e.g. a sub-line with fewer than 2 coordinates).
 *
 * Any Feature encountered during import that fails validation or falls outside the declared
 * Trails AND is not listed here is treated as an unexpected anomaly and aborts the import --
 * this list is the only place "known, explained" exclusions may be declared.
 */
public class ExcludedFeatureEntry {

    private int sourceFeatureIndex;
    private String pmntnNm;
    private String category;
    private String reason;

    public int getSourceFeatureIndex() {
        return sourceFeatureIndex;
    }

    public void setSourceFeatureIndex(int sourceFeatureIndex) {
        this.sourceFeatureIndex = sourceFeatureIndex;
    }

    public String getPmntnNm() {
        return pmntnNm;
    }

    public void setPmntnNm(String pmntnNm) {
        this.pmntnNm = pmntnNm;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
