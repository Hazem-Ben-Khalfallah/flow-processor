package com.blacknebula.flowprocessor.processor;

import java.util.concurrent.TimeUnit;

@FunctionalInterface
public interface ProcessingTimeLimitPolicy {
    static ProcessingTimeLimitPolicy fixedLimit(int duration, TimeUnit unit) {
        return () -> unit.toMillis(duration);
    }

    /**
     * Returns the limit - in milliseconds - that execution time should not exceed.
     *
     * @return The limit - in milliseconds - that execution time should not exceed.
     */
    long getLimit();
}
