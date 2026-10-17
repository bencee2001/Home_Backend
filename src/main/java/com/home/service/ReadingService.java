package com.home.service;

import com.home.flow.ReadingFlow;
import com.home.model.Reading;
import com.home.repository.ForeignKeyViolationException;
import com.home.repository.ReadingRepository;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class ReadingService {

    @Inject
    ReadingRepository readingRepository;

    @Inject
    ReadingFlow flow;

    // Saves first, then publishes, so collectors only ever see persisted readings
    public Reading record(long sensorId, Double temperature, Double humidity) {
        Reading reading;
        try {
            reading = readingRepository.insert(sensorId, temperature, humidity);
        } catch (ForeignKeyViolationException e) {
            throw new UnknownSensorException(sensorId);
        }

        flow.emit(reading);
        return reading;
    }
}
