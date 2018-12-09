package com.blacknebula.flowprocessor.provider;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.Provider;
import com.blacknebula.flowprocessor.processor.ProviderRunPolicy;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Scope(value = "prototype")
public class CollectionProvider<T> extends Provider<T> {

    private Stream<T> elements;

    public CollectionProvider() {
    }

    public CollectionProvider(Collection<T> elements) {
        setElements(elements);
    }

    public CollectionProvider(T... elements) {
        setElements(elements);
    }

    public CollectionProvider(Stream<T> stream) {
        setElements(stream);
    }

    public CollectionProvider<T> setElements(Stream<T> stream) {
        this.elements = stream;
        return this;
    }

    public CollectionProvider<T> setElements(T... elements) {
        Assert.isTrue(ArrayUtils.isNotEmpty(elements), "should not be empty");
        this.elements = Stream.of(elements);
        return this;
    }

    public CollectionProvider<T> setElements(Collection<T> elements) {
        Assert.isTrue(!Objects.isNull(elements), "should not be null");
        this.elements = elements.stream();
        return this;
    }

    @Override
    public void start(Context<T> ctx, ProviderRunPolicy<T> providerRunPolicy) {
        List<T> list = elements.collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            emitElement(list.get(i), list.get(i), (long) i, false);
            if (!providerRunPolicy.test(ctx)) {
                break;
            }
        }
        onEnd();
    }

}
