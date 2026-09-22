package com.season.semiproject.spatial.trail.ingest;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrailImportDAO {
    @Autowired
    private SqlSession session;

    public void deleteTrailsBySourceFile(String sourceFile) {
        session.delete("deleteTrailsBySourceFile", sourceFile);
    }

    public Long insertTrail(TrailInsertParam param) {
        session.insert("insertTrail", param);
        return param.getId();
    }

    public void insertTrailFeature(TrailFeatureInsertParam param) {
        session.insert("insertTrailFeature", param);
    }
}
