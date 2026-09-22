package com.season.semiproject.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.season.semiproject.vo.Mountain;

@Repository
public class MountainDAO {
	
    @Autowired
    SqlSession session;

    public List<Mountain> getAllMountains() {
        return session.selectList("mapper-mountain.getAllMountains");
    }

    public Mountain getMountainById(int mountainId) {
        return session.selectOne("mapper-mountain.getMountainById", mountainId);
    }
}
