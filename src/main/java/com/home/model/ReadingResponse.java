package com.home.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReadingResponse(
        long id,
        @JsonProperty("s_id") long sensorId,
        @JsonProperty("t") Double temperature,
        @JsonProperty("h") Double humidity) {
}
