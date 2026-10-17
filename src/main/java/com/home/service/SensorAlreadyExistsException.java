package com.home.service;

public class SensorAlreadyExistsException extends RuntimeException {

    public SensorAlreadyExistsException(long sensorId) {
        super("sensor already exists: " + sensorId);
    }
}
