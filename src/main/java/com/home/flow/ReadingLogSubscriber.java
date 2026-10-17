package com.home.flow;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Destroy;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Example collector; a websocket broadcaster would subscribe the same way
@Component
public class ReadingLogSubscriber {

    private static final Logger log = LoggerFactory.getLogger(ReadingLogSubscriber.class);

    @Inject
    ReadingFlow flow;

    private Subscription subscription;

    @Init
    public void start() {
        subscription = flow.subscribe("log", r ->
                log.info("Reading id={} sensor={} t={} h={} at={}",
                        r.id(), r.sensorId(), r.temperature(), r.humidity(), r.createdAt()));
    }

    @Destroy
    public void stop() {
        if (subscription != null) {
            subscription.cancel();
        }
    }
}
