package com.home.service;

import com.home.db.SqlErrors;
import com.home.flow.ReadingFlow;
import com.home.model.Reading;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.wood.DbContext;
import org.noear.wood.annotation.Db;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

@Component
public class ReadingService {

    @Db("db")
    DbContext db;

    @Inject
    ReadingFlow flow;

    // Saves first, then publishes, so collectors only ever see persisted readings
    public Reading record(long sensorId, Double temperature, Double humidity) throws SQLException {
        Map<String, Object> row;
        try {
            row = db.sql("INSERT INTO readings (sensor_id, temperature, humidity) VALUES (?, ?, ?) RETURNING id, created_at",
                            sensorId, temperature, humidity)
                    .getMap();
        } catch (Exception e) {
            if (SqlErrors.hasState(e, SqlErrors.FOREIGN_KEY_VIOLATION)) {
                throw new UnknownSensorException(sensorId);
            }
            throw e;
        }

        Reading reading = new Reading(
                ((Number) row.get("id")).longValue(),
                sensorId,
                temperature,
                humidity,
                toInstant(row.get("created_at")));

        flow.emit(reading);
        return reading;
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
