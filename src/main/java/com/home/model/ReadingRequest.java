package com.home.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// Incoming sensor payload: {"s_id":1,"t":21.2,"h":42.8}
public record ReadingRequest(
        @JsonProperty("s_id") Long sensorId,
        @JsonProperty("t") Double temperature,
        @JsonProperty("h") Double humidity) {
}
