package com.home.controller;

import com.home.model.ErrorResponse;
import com.home.model.ReadingRequest;
import com.home.model.ReadingResponse;
import com.home.service.ReadingService;
import com.home.service.UnknownSensorException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;

@Tag(name = "Readings")
@Controller
@Mapping("/readings")
public class ReadingController {

    @Inject
    ReadingService readingService;

    @Operation(summary = "Store a sensor reading", description = "Body: {\"s_id\":1,\"t\":21.2,\"h\":42.8}; t and h may be null")
    @Post
    @Mapping
    public Object create(@Body ReadingRequest req, Context ctx) {
        if (req == null || req.sensorId() == null) {
            ctx.status(400);
            return new ErrorResponse("s_id is required");
        }

        try {
            var reading = readingService.record(req.sensorId(), req.temperature(), req.humidity());
            ctx.status(201);
            return ReadingResponse.of(reading);
        } catch (UnknownSensorException e) {
            ctx.status(422);
            return new ErrorResponse(e.getMessage());
        }
    }
}
