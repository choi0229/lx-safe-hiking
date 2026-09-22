package com.season.semiproject.spatial.trail.ingest.manifest;

/**
 * One target Trail declared in trail-import-manifest.json.
 * pmntnNm is the GeoJSON properties.PMNTN_NM value used to group Features into this Trail.
 * courseId/mappingStatus/mappingReason record whether (and why) this source trail was verified
 * to correspond to an existing `course` row -- never inferred from name equality alone.
 */
public class TrailManifestEntry {

    private String pmntnNm;
    private String sourceMountainName;
    private String sourceCourseName;
    private Integer courseId;
    private String mappingStatus;
    private String mappingReason;
    private String note;

    public String getPmntnNm() {
        return pmntnNm;
    }

    public void setPmntnNm(String pmntnNm) {
        this.pmntnNm = pmntnNm;
    }

    public String getSourceMountainName() {
        return sourceMountainName;
    }

    public void setSourceMountainName(String sourceMountainName) {
        this.sourceMountainName = sourceMountainName;
    }

    public String getSourceCourseName() {
        return sourceCourseName;
    }

    public void setSourceCourseName(String sourceCourseName) {
        this.sourceCourseName = sourceCourseName;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getMappingStatus() {
        return mappingStatus;
    }

    public void setMappingStatus(String mappingStatus) {
        this.mappingStatus = mappingStatus;
    }

    public String getMappingReason() {
        return mappingReason;
    }

    public void setMappingReason(String mappingReason) {
        this.mappingReason = mappingReason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
