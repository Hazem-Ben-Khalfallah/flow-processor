package com.blacknebula.flowprocessor.processor;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.ElementWrapper;
import com.blacknebula.flowprocessor.core.Performer;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * Used in {@link ProcessorBuilder#registerPerformer(Performer, RunPolicy)} to
 * define custom run policy within flow execution.<br>
 * It's a {@link BiPredicate} interface that when applied to a given
 * {@link Context} and {@link ElementWrapper} tells the {@link Processor} if it
 * should execute/ignore a {@link Performer} for a given
 * {@link ElementWrapper}.<br>
 *
 * @param <T> type
 */
@FunctionalInterface
public interface RunPolicy<T> {
    /**
     * When registering a {@link Performer} with this {@link RunPolicy} it would
     * always be invoked by the {@link Processor} throughout the flow.
     *
     * @param <T> type
     * @return predicate {@link RunPolicy}
     */
    static <T> RunPolicy<T> alwaysRun() {
        return ((Context<T> ctx, ElementWrapper<T> elementWrapper) -> true);
    }

    /**
     * When registering a {@link Performer} with this {@link RunPolicy} it would
     * be invoked by the {@link Processor} only if there's errors in the
     * context.
     *
     * @param <T> type
     * @return predicate {@link RunPolicy}
     */
    static <T> RunPolicy<T> elementHasErrors() {
        return ((Context<T> ctx, ElementWrapper<T> elementWrapper) -> elementWrapper.hasErrors());
    }

    /**
     * When registering a {@link Performer} with this {@link RunPolicy} it would
     * be invoked by the {@link Processor} only if there's no errors in the
     * context.
     *
     * @param <T> type
     * @return predicate {@link RunPolicy}
     */
    static <T> RunPolicy<T> elementHasNoErrors() {
        return ((Context<T> ctx, ElementWrapper<T> elementWrapper) -> !elementWrapper.hasErrors());
    }

    /**
     * When registering a {@link Performer} with this {@link RunPolicy} it would
     * be invoked by the {@link Processor} only if the context contains a
     * parameter having a key equal to the provided one.
     *
     * @param key parameter key that should be present in context
     * @param <T> type
     * @return predicate {@link RunPolicy}
     */
    static <T> RunPolicy<T> contextHasParam(String key) {
        return ((Context<T> ctx, ElementWrapper<T> elementWrapper) -> ctx.hasFlowParam(key));
    }

    /**
     * When registering a {@link Performer} with this {@link RunPolicy} it would
     * be invoked by the {@link Processor} only if the current element's index
     * is greater than a given index(it may be used interruption recovery).
     *
     * @param index a given index that must be lower than all elements that will
     *              be processed
     * @param <T>   type
     * @return predicate {@link RunPolicy}
     */
    static <T> RunPolicy<T> indexLowerThan(Integer index) {
        return ((Context<T> ctx, ElementWrapper<T> elementWrapper) -> elementWrapper.getIndex() < index);
    }

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param ctx            a {@link Context}
     * @param elementWrapper {@link ElementWrapper}
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     */
    boolean test(Context<T> ctx, ElementWrapper<T> elementWrapper);

    default RunPolicy<T> and(RunPolicy<T> other) {
        Objects.requireNonNull(other);
        return (Context<T> ctx, ElementWrapper<T> elementWrapper) -> test(ctx, elementWrapper) && other.test(ctx, elementWrapper);
    }

    default RunPolicy<T> negate() {
        return (Context<T> ctx, ElementWrapper<T> elementWrapper) -> !test(ctx, elementWrapper);
    }

    default RunPolicy<T> or(RunPolicy<T> other) {
        Objects.requireNonNull(other);
        return (Context<T> ctx, ElementWrapper<T> elementWrapper) -> test(ctx, elementWrapper) || other.test(ctx, elementWrapper);
    }
}
