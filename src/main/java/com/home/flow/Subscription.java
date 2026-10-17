package com.home.flow;

public interface Subscription {

    // Stops delivery to this collector; safe to call more than once
    void cancel();
}
