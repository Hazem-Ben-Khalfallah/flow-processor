package com.blacknebula.flowprocessor.processor;


import com.blacknebula.flowprocessor.core.Provider;

/**
 * Intermediate interface used through {@link Processor} definition with
 * {@link ProcessorBuilder}.
 *
 * @param <T> Element type
 */
public interface ProcessorBuilder1<T> {
    ProcessorBuilder2<T> withProvider(Provider<T> provider);

    ProcessorBuilder2<T> withProvider(Provider<T> provider, ProviderRunPolicy providerRunPolicy);

}