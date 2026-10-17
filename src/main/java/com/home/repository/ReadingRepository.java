package com.home.repository;

import com.home.model.Reading;
import org.noear.solon.annotation.Component;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
public class ReadingRepository {

    @Db("db")
    DbContext db;

    // Returns the stored row with its generated id and server-side created_at.
    // Throws ForeignKeyViolationException if the sensor does not exist.
    public Reading insert(long sensorId, Double temperature, Double humidity) {
        Map<String, Object> row;
        try {
            row = db.sql("INSERT INTO readings (sensor_id, temperature, humidity) VALUES (?, ?, ?) RETURNING id, created_at",
                            sensorId, temperature, humidity)
                    .getMap();
        } catch (Exception e) {
            throw SqlErrors.translate("insert reading for sensor " + sensorId, e);
        }

        return new Reading(
                ((Number) row.get("id")).longValue(),
                sensorId,
                temperature,
                humidity,
                toInstant(row.get("created_at")));
    }

    private static Instant toInstant(Object value) {
        if (value instanceof Timestamp ts) {
            return ts.toInstant();
        }
        if (value instanceof OffsetDateTime odt) {
            return odt.toInstant();
        }
        throw new IllegalStateException("unexpected created_at type: " + value);
    }
}
