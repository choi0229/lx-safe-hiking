package com.season.semiproject.spatial.slope.query;

import com.fasterxml.jackson.databind.JsonNode;

public final class SlopeSectionFeature {
    private final String type = "Feature";
    private final SlopeSectionProperties properties;
    private final JsonNode geometry;

    public SlopeSectionFeature(SlopeSectionProperties properties, JsonNode geometry) {
        this.properties = properties;
        this.geometry = geometry;
    }

    public String getType() {
        return type;
    }

    public SlopeSectionProperties getProperties() {
        return properties;
    }

    public JsonNode getGeometry() {
        return geometry;
    }
}
