package com.home.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A hot, conflated flow similar to Kotlin's StateFlow, but keyed: it keeps only the latest value per key.
 *
 * <ul>
 *   <li>Hot: {@link #emit} never waits for collectors; values go out whether anyone listens or not.</li>
 *   <li>Conflated: each collector holds at most one pending value per key; a newer value overwrites it.</li>
 *   <li>Immediate: each collector has its own worker thread that runs as soon as a value arrives.</li>
 *   <li>Replay: a new collector first receives the current latest value of every key.</li>
 * </ul>
 *
 * A slow or failing collector never blocks the emitter or other collectors.
 */
public class LatestValueFlow<K, T> {

    private static final Logger log = LoggerFactory.getLogger(LatestValueFlow.class);

    private final String name;
    private final Function<? super T, ? extends K> keyOf;

    // Guarded by lock
    private final Map<K, T> latest = new LinkedHashMap<>();
    private final List<Collector> collectors = new ArrayList<>();
    private boolean closed;

    private final Object lock = new Object();

    public LatestValueFlow(String name, Function<? super T, ? extends K> keyOf) {
        this.name = name;
        this.keyOf = keyOf;
    }

    public void emit(T value) {
        Objects.requireNonNull(value, "value");
        K key = keyOf.apply(value);
        synchronized (lock) {
            if (closed) {
                return;
            }
            latest.put(key, value);
            for (Collector c : collectors) {
                c.offer(key, value);
            }
        }
    }

    public Optional<T> latest(K key) {
        synchronized (lock) {
            return Optional.ofNullable(latest.get(key));
        }
    }

    public List<T> latestAll() {
        synchronized (lock) {
            return List.copyOf(latest.values());
        }
    }

    // The collector name is used for its thread name and in log messages
    public Subscription subscribe(String collectorName, Consumer<? super T> handler) {
        synchronized (lock) {
            if (closed) {
                throw new IllegalStateException("flow '" + name + "' is closed");
            }
            Collector c = new Collector(collectorName, handler);
            latest.forEach(c::offer);
            collectors.add(c);
            return c;
        }
    }

    public void close() {
        synchronized (lock) {
            closed = true;
            collectors.forEach(Collector::stop);
            collectors.clear();
        }
    }

    private final class Collector implements Subscription {

        private final String collectorName;
        private final Consumer<? super T> handler;
        private final ExecutorService worker;

        // Guarded by this
        private final Map<K, T> pending = new LinkedHashMap<>();
        private boolean draining;
        private boolean active = true;

        Collector(String collectorName, Consumer<? super T> handler) {
            this.collectorName = collectorName;
            this.handler = handler;
            this.worker = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, name + "-" + collectorName);
                t.setDaemon(true);
                return t;
            });
        }

        // Called only while holding the flow lock, so it never races with stop()
        void offer(K key, T value) {
            synchronized (this) {
                if (!active) {
                    return;
                }
                pending.put(key, value);
                if (draining) {
                    return;
                }
                draining = true;
            }
            worker.execute(this::drain);
        }

        private void drain() {
            while (true) {
                T next;
                synchronized (this) {
                    Iterator<T> it = pending.values().iterator();
                    if (!active || !it.hasNext()) {
                        draining = false;
                        return;
                    }
                    next = it.next();
                    it.remove();
                }
                try {
                    handler.accept(next);
                } catch (Throwable e) {
                    log.warn("Collector '{}' of flow '{}' failed", collectorName, name, e);
                }
            }
        }

        @Override
        public void cancel() {
            synchronized (lock) {
                collectors.remove(this);
                stop();
            }
        }

        void stop() {
            synchronized (this) {
                active = false;
                pending.clear();
            }
            worker.shutdown();
        }
    }
}
