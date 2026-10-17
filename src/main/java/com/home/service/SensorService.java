package com.home.service;

import com.home.model.Sensor;
import com.home.repository.DuplicateKeyException;
import com.home.repository.SensorRepository;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SensorService {

    private static final Logger log = LoggerFactory.getLogger(SensorService.class);

    @Inject
    SensorRepository sensorRepository;

    public Sensor register(long id, String name) {
        Sensor sensor = new Sensor(id, name);
        try {
            sensorRepository.insert(sensor);
        } catch (DuplicateKeyException e) {
            throw new SensorAlreadyExistsException(id);
        }

        log.info("Registered sensor id={} name={}", id, name);
        return sensor;
    }
}
