package com.home.model;

import java.time.Instant;

// A reading as stored in the database
public record Reading(long id, long sensorId, Double temperature, Double humidity, Instant createdAt) {
}
