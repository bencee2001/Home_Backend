package com.home.flow;

import com.home.model.Reading;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Destroy;

// Saved readings, conflated per sensor
@Component
public class ReadingFlow extends LatestValueFlow<Long, Reading> {

    public ReadingFlow() {
        super("readings", Reading::sensorId);
    }

    @Destroy
    public void shutdown() {
        close();
    }
}
