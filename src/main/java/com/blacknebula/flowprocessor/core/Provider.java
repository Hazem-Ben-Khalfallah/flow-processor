package com.blacknebula.flowprocessor.core;

import com.blacknebula.flowprocessor.processor.Processor;
import com.blacknebula.flowprocessor.processor.ProviderRunPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

/**
 * Base (abstract) class for providers.
 *
 * @param <T> Element type
 */
public abstract class Provider<T> {

    private Processor<T> processor;

    private List<ElementWrapper<T>> objects = new ArrayList<>();

    @Value("${flow.processor.bulkSize}")
    private Integer bulkSize = 500;

    public void setProcessor(Processor<T> processor) {
        this.processor = processor;
    }

    /**
     * {@link Provider} implementors have to call this method sequentially with each element they have to emit.
     *
     * @param element    The element to emit
     * @param rawElement The raw representation on the element CSV, JSON, etc...
     */
    public void emitElement(final T element, final Object rawElement, final Long index, final boolean invalidRow) {
        ElementWrapper<T> elementWrapper = new ElementWrapper<>(element, rawElement);
        elementWrapper.setIndex(index);
        elementWrapper.setUnparseableRow(invalidRow);
        objects.add(elementWrapper);
        if (objects.size() >= bulkSize) {
            processor.onElements(objects);
            objects.clear();
        }
    }

    public final void onEnd() {
        processor.onElements(objects);
        objects.clear();
    }

    public abstract void start(Context<T> ctx, ProviderRunPolicy<T> providerRunPolicy);//implementors should invoke emitElement(element, rawElement) on each element they emit

    Integer getBulkSize() {
        return bulkSize;
    }
}