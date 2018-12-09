package com.blacknebula.flowprocessor.core;

import com.blacknebula.flowprocessor.processor.Processor;

import java.util.List;

/**
 * {@link Performer} implementations should operate in a decoupled fashion from any other performer defined within the flow.
 *
 * @param <T> Element type
 */
public interface Performer<T> {

    /**
     * Hook that's called before the whole data onBoarding flow starts.
     * <p>
     * <br><br>Performers that need to prepare whatever things (like resources) before the flow starts they do it here like connections to tiers, file creation, etc ...
     * <p>
     * <br><br>Note that {@link #onFlowStart(Context)} are called by {@link Processor} with no respect to order in which the {@link Performer} was registered in the flow.
     *
     * @param ctx The {@link Context}
     */
    default void onFlowStart(Context<T> ctx) {
    }

    /**
     * Hook that's called right after the whole data onBoarding flow terminates.
     * <p>
     * <br><br>Performers that need to clean up things or whatever after the flow finishes they do it here like closing connections to tiers, closing files, etc ...
     * <p>
     * <br><br>Note that {@link #onFlowEnd(Context)} are called by {@link Processor} with no respect to order in which the {@link Performer} was registered in the flow.
     *
     * @param ctx The {@link Context}
     */
    default void onFlowEnd(Context<T> ctx) {
    }

    /**
     * Main method of the {@link Performer}.
     *
     * @param ctx      {@link Context} where to add errors if the performer has to detect any errors like [pre-]validation performers
     * @param elements {@link ElementWrapper} holds a list of elementWrapper.
     */
    default void bulkApply(Context<T> ctx, List<ElementWrapper<T>> elements) {
        elements.forEach(elementWrapper -> apply(ctx, elementWrapper));
    }


    /**
     * this method will be applied for each element of the list.
     *
     * @param ctx            {@link Context} where to add errors if the performer has to detect any errors like [pre-]validation performers
     * @param elementWrapper {@link ElementWrapper} holds essential data that the performer needs to be notified with.
     */
    void apply(Context<T> ctx, ElementWrapper<T> elementWrapper);
}