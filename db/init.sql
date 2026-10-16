CREATE TABLE IF NOT EXISTS sensors (
    id         BIGINT      PRIMARY KEY,
    name       TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS readings (
    id          BIGSERIAL        PRIMARY KEY,
    sensor_id   BIGINT           NOT NULL REFERENCES sensors (id),
    temperature DOUBLE PRECISION,
    humidity    DOUBLE PRECISION,
    created_at  TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS readings_sensor_created_idx ON readings (sensor_id, created_at);
