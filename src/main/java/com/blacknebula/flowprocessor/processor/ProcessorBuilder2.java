package com.blacknebula.flowprocessor.processor;

/**
 * Intermediate interface used through {@link Processor} definition with {@link ProcessorBuilder}.
 *
 * @param <T> Element type
 */
public interface ProcessorBuilder2<T> {
    ProcessorBuilder22<T> startPoliciesConfig();

    ProcessorBuilder3<T> startFlowDefinition();
}