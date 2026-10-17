package com.home.service;

public class UnknownSensorException extends RuntimeException {

    public UnknownSensorException(long sensorId) {
        super("unknown sensor: " + sensorId);
    }
}
