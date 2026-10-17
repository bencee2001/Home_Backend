package com.home.controller;

import com.home.model.ErrorResponse;
import com.home.model.SensorRequest;
import com.home.model.SensorResponse;
import com.home.service.SensorAlreadyExistsException;
import com.home.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;

import java.sql.SQLException;

@Tag(name = "Sensors")
@Controller
@Mapping("/sensors")
public class SensorController {

    @Inject
    SensorService sensorService;

    @Operation(summary = "Register a sensor", description = "Body: {\"id\":1,\"name\":\"living room\"}; name may be null")
    @Post
    @Mapping
    public Object create(@Body SensorRequest req, Context ctx) throws SQLException {
        if (req == null || req.id() == null) {
            ctx.status(400);
            return new ErrorResponse("id is required");
        }

        try {
            var sensor = sensorService.register(req.id(), req.name());
            ctx.status(201);
            return new SensorResponse(sensor.id(), sensor.name());
        } catch (SensorAlreadyExistsException e) {
            ctx.status(409);
            return new ErrorResponse(e.getMessage());
        }
    }
}
