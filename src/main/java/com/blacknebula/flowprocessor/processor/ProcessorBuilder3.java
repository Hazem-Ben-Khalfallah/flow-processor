package com.blacknebula.flowprocessor.processor;


import com.blacknebula.flowprocessor.core.Performer;

/**
 * Intermediate interface used through {@link Processor} definition with {@link ProcessorBuilder}.
 *
 * @param <T> Element type
 */
public interface ProcessorBuilder3<T> {
    /**
     * Registers a {@link Performer}.
     * <br><br>
     * The order of registration of performers is the order in which they are called through the flow.
     *
     * @param performer {@link Performer}
     * @return The {@link ProcessorBuilder} instance
     */
    ProcessorBuilder3<T> registerPerformer(Performer<T> performer);

    ProcessorBuilder3<T> registerPerformer(Performer<T> performer, RunPolicy<T> predicate);

    ProcessorBuilder4<T> endFlowDefinition();
}