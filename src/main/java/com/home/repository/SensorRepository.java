package com.home.repository;

import com.home.model.Sensor;
import org.noear.solon.annotation.Component;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

@Component
public class SensorRepository {

    @Db("db")
    DbContext db;

    // Throws DuplicateKeyException if the id is taken
    public void insert(Sensor sensor) {
        try {
            db.table("sensors")
                    .set("id", sensor.id())
                    .set("name", sensor.name())
                    .insert();
        } catch (Exception e) {
            throw SqlErrors.translate("insert sensor " + sensor.id(), e);
        }
    }
}
