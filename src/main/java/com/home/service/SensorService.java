package com.home.service;

import com.home.db.SqlErrors;
import com.home.model.Sensor;
import org.noear.solon.annotation.Component;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

@Component
public class SensorService {

    private static final Logger log = LoggerFactory.getLogger(SensorService.class);

    @Db("db")
    DbContext db;

    public Sensor register(long id, String name) throws SQLException {
        try {
            db.table("sensors")
                    .set("id", id)
                    .set("name", name)
                    .insert();
        } catch (Exception e) {
            if (SqlErrors.hasState(e, SqlErrors.UNIQUE_VIOLATION)) {
                throw new SensorAlreadyExistsException(id);
            }
            throw e;
        }

        log.info("Registered sensor id={} name={}", id, name);
        return new Sensor(id, name);
    }
}
