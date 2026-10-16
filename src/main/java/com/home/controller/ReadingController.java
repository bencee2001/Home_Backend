package com.home.controller;

import com.home.db.SqlErrors;
import com.home.model.ErrorResponse;
import com.home.model.ReadingRequest;
import com.home.model.ReadingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.Context;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Tag(name = "Readings")
@Controller
@Mapping("/readings")
public class ReadingController {

    private static final Logger log = LoggerFactory.getLogger(ReadingController.class);

    @Db("db")
    DbContext db;

    @Operation(summary = "Store a sensor reading", description = "Body: {\"s_id\":1,\"t\":21.2,\"h\":42.8}; t and h may be null")
    @Post
    @Mapping
    public Object create(@Body ReadingRequest req, Context ctx) throws SQLException {
        if (req == null || req.sensorId() == null) {
            ctx.status(400);
            return new ErrorResponse("s_id is required");
        }

        long id;
        try {
            id = db.table("readings")
                    .set("sensor_id", req.sensorId())
                    .set("temperature", req.temperature())
                    .set("humidity", req.humidity())
                    .insert();
        } catch (Exception e) {
            if (SqlErrors.hasState(e, SqlErrors.FOREIGN_KEY_VIOLATION)) {
                ctx.status(422);
                return new ErrorResponse("unknown sensor: " + req.sensorId());
            }
            throw e;
        }

        log.info("Stored reading id={} sensor={} t={} h={}", id, req.sensorId(), req.temperature(), req.humidity());
        ctx.status(201);
        return new ReadingResponse(id, req.sensorId(), req.temperature(), req.humidity());
    }
}
