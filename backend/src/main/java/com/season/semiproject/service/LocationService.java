package com.season.semiproject.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.season.semiproject.vo.LocationRequest;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocationService {
    private final ConcurrentHashMap<String, double[]> userLocations = new ConcurrentHashMap<>();

    public void updateUserLocation(String userId, double latitude, double longitude) {
        userLocations.put(userId, new double[]{latitude, longitude});
    }

    public ConcurrentHashMap<String, double[]> getAllUserLocations() {
        return userLocations;
    }
}
