package com.home.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReadingResponse(
        long id,
        @JsonProperty("s_id") long sensorId,
        @JsonProperty("t") Double temperature,
        @JsonProperty("h") Double humidity,
        @JsonProperty("created_at") String createdAt) {

    public static ReadingResponse of(Reading r) {
        return new ReadingResponse(r.id(), r.sensorId(), r.temperature(), r.humidity(), r.createdAt().toString());
    }
}
