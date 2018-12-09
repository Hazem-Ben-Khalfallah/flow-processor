package com.blacknebula.flowprocessor.processor;


import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.Provider;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Used in {@link ProcessorBuilder#withProvider(Provider, ProviderRunPolicy)} to define custom run policy within flow execution.<br>
 * It's a {@link Predicate} interface that when applied to a given {@link Context} tells the {@link Provider} if it should continue execution or not.<br>
 *
 * @param <T> type
 */
@FunctionalInterface
public interface ProviderRunPolicy<T> {
    /**
     * When registering a {@link Provider} with this {@link ProviderRunPolicy} it would
     * always emit elements in the flow(default behavior).
     *
     * @param <T> type
     * @return predicate {@link ProviderRunPolicy}
     */
    static <T> ProviderRunPolicy<T> alwaysRun() {
        return ((Context<T> ctx) -> true);
    }

    /**
     * When registering a {@link Provider} with this {@link ProviderRunPolicy} it would
     * emit elements only if the flow is interrupted(the negation will be more convenient).
     *
     * @param <T> type
     * @return predicate {@link ProviderRunPolicy}
     */
    static <T> ProviderRunPolicy<T> flowInterrupted() {
        return ((Context<T> ctx) -> ctx.isFlowInterrupted());
    }

    /**
     * Evaluates this predicate on the given context.
     *
     * @param ctx a {@link Context}
     * @return {@code true} if the input arguments match the predicate, otherwise {@code false}
     */
    boolean test(Context<T> ctx);

    default ProviderRunPolicy<T> and(ProviderRunPolicy<T> other) {
        Objects.requireNonNull(other);
        return (Context<T> ctx) -> test(ctx) && other.test(ctx);
    }

    default ProviderRunPolicy<T> negate() {
        return (Context<T> ctx) -> !test(ctx);
    }

    default ProviderRunPolicy<T> or(ProviderRunPolicy<T> other) {
        Objects.requireNonNull(other);
        return (Context<T> ctx) -> test(ctx) || other.test(ctx);
    }
}
