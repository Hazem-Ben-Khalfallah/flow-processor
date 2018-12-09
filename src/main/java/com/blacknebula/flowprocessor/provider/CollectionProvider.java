package com.blacknebula.flowprocessor.provider;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.Provider;
import com.blacknebula.flowprocessor.processor.ProviderRunPolicy;
import com.codahale.metrics.Counter;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@Scope(value = "prototype")
public class CollectionProvider<T> extends Provider<T> {

    private Stream<T> stream;

    public CollectionProvider() {
    }

    public CollectionProvider(Collection<T> elements) {
        setElements(elements);
    }

    public CollectionProvider(T... elements) {
        setElements(elements);
    }

    public CollectionProvider(Stream<T> stream) {
        setStream(stream);
    }

    public CollectionProvider<T> setStream(Stream<T> stream) {
        this.stream = stream;
        return this;
    }

    public CollectionProvider<T> setElements(T... elements) {
        Assert.isTrue(ArrayUtils.isNotEmpty(elements), "should not be empty");
        this.stream = Stream.of(elements);
        return this;
    }

    public CollectionProvider<T> setElements(Collection<T> elements) {
        Assert.isTrue(!Objects.isNull(elements), "should not be null");
        this.stream = elements.stream();
        return this;
    }

    @Override
    public void start(Context<T> ctx, ProviderRunPolicy<T> providerRunPolicy) {
        applyStream(ctx, providerRunPolicy);
        onEnd();
    }

    private void applyStream(Context<T> ctx, ProviderRunPolicy<T> providerRunPolicy) {
        final Counter count = new Counter();
        try {
            stream.forEach(element -> {
                emitElement(element, element, count.getCount(), false);
                if (!providerRunPolicy.test(ctx)) {
                    throw new InterruptionException();
                }
            });
        } catch (InterruptionException e) {
            // do nothing because it is a functional interruption
        }
    }

}
