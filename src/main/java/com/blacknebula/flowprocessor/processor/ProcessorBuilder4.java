package com.blacknebula.flowprocessor.processor;

import java.util.Map;

/**
 * Intermediate interface used through {@link Processor} definition with
 * {@link ProcessorBuilder}.
 *
 * @param <T> Element type
 */
public interface ProcessorBuilder4<T> {
    ProcessorBuilder4<T> addFlowParam(String key, Object value);

    ProcessorBuilder4<T> addFlowParams(Map<String, Object> params);

    ProcessorBuilder4<T> addNullableFlowParam(String key, Object value);

    Processor<T> build();
}