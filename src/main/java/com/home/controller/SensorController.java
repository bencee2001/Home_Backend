package com.home.controller;

import com.home.db.SqlErrors;
import com.home.model.ErrorResponse;
import com.home.model.SensorRequest;
import com.home.model.SensorResponse;
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

@Tag(name = "Sensors")
@Controller
@Mapping("/sensors")
public class SensorController {

    private static final Logger log = LoggerFactory.getLogger(SensorController.class);

    @Db("db")
    DbContext db;

    @Operation(summary = "Register a sensor", description = "Body: {\"id\":1,\"name\":\"living room\"}; name may be null")
    @Post
    @Mapping
    public Object create(@Body SensorRequest req, Context ctx) throws SQLException {
        if (req == null || req.id() == null) {
            ctx.status(400);
            return new ErrorResponse("id is required");
        }

        try {
            db.table("sensors")
                    .set("id", req.id())
                    .set("name", req.name())
                    .insert();
        } catch (Exception e) {
            if (SqlErrors.hasState(e, SqlErrors.UNIQUE_VIOLATION)) {
                ctx.status(409);
                return new ErrorResponse("sensor already exists: " + req.id());
            }
            throw e;
        }

        log.info("Registered sensor id={} name={}", req.id(), req.name());
        ctx.status(201);
        return new SensorResponse(req.id(), req.name());
    }
}
