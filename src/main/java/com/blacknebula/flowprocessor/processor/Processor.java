package com.blacknebula.flowprocessor.processor;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.ElementWrapper;
import com.blacknebula.flowprocessor.core.Error;
import com.blacknebula.flowprocessor.core.Performer;
import com.blacknebula.flowprocessor.core.Provider;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Processor<T> {

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
    private final List<PerformerWrapper<T>> performers = new ArrayList<>();
    private final Context<T> context;
    private final RunPolicy<T> ignoreElementPolicy;
    private final ProcessingTimeLimitPolicy timeLimitPolicy;
    private Provider<T> provider = null;
    private ProviderRunPolicy<T> providerRunPolicy;

    Processor(Context<T> context, RunPolicy<T> ignoreElementPolicy, ProcessingTimeLimitPolicy timeLimitPolicy,
              ProviderRunPolicy<T> providerRunPolicy) {
        this.context = context;
        this.ignoreElementPolicy = ignoreElementPolicy;
        this.timeLimitPolicy = timeLimitPolicy;
        this.providerRunPolicy = providerRunPolicy;
    }

    public Context<T> start() {
        // fire all onFlowStart() hooks
        performers.forEach(performerWrapper -> callSilently(performerWrapper.performer::onFlowStart, context));
        // execute steps
        provider.start(context, providerRunPolicy);
        // fire all onFlowEnd() hooks
        performers.forEach(performerWrapper -> callSilently(performerWrapper.performer::onFlowEnd, context));
        return context;
    }

    public void addPerformer(Performer<T> performer, RunPolicy<T> predicate) {
        performers.add(new PerformerWrapper<>(performer, predicate));
    }

    public void onElements(final List<ElementWrapper<T>> elementWrappers) {
        Objects.requireNonNull(elementWrappers, "Null event");
        boolean timeElapsed = false;
        // run flow on element
        try {
            List<Future<Void>> invokeResults = executor.invokeAll(Collections.singletonList(() -> {
                doProcess(elementWrappers);
                return null;
            }), timeLimitPolicy.getLimit(), TimeUnit.MILLISECONDS);
            timeElapsed = invokeResults.stream().anyMatch(Future::isCancelled);
        } catch (Exception e1) {
            LOGGER.error("error while waiting for element processing to terminate", e1);
        }

        if (timeElapsed) {
            LOGGER.error("processing timed out on elements with an index between {} and {}", //
                    elementWrappers.get(0).getIndex(), elementWrappers.get(elementWrappers.size() - 1).getIndex());
            elementWrappers.forEach(w -> w.addError("processing_timeout", "flow exec timed out on element at index " + w.getIndex()));
        }

        if (hasErrors(elementWrappers)) {
            elementWrappers.stream().filter(ElementWrapper::hasErrors).forEach(w -> {
                final Error<T> error = new Error<>(w.getErrors(), w.getIndex(), w.getElement(), w.getRawElement());
                error.setHandled(w.allErrorHandled());
                context.addError(error);
            });
        }
    }

    private boolean hasErrors(List<ElementWrapper<T>> elementWrappers) {
        return elementWrappers.stream().anyMatch(ElementWrapper::hasErrors);
    }

    private void doProcess(final List<ElementWrapper<T>> elementWrappers) {
        for (PerformerWrapper<T> performerWrapper : performers) {
            final List<ElementWrapper<T>> eligibleWrappers = elementWrappers.stream()
                    .filter(e -> !ignoreElementPolicy.test(context, e) && performerWrapper.runPolicy.test(context, e)).collect(Collectors.toList());
            try {
                performerWrapper.performer.bulkApply(context, eligibleWrappers);
            } catch (Exception e) {
                LOGGER.error(String.format("error while applying %s on elements with an index between %s and %s", //
                        performerWrapper.performer.getClass().getCanonicalName(), //
                        elementWrappers.get(0).getIndex(), //
                        elementWrappers.get(elementWrappers.size() - 1).getIndex()), e);

                eligibleWrappers.forEach(w -> w.addError("apply_exception", ExceptionUtils.getStackTrace(e)));
            }
        }
    }

    private void callSilently(Consumer<Context<T>> action, Context<T> context) {
        try {
            action.accept(context);
        } catch (Exception e) {
            LOGGER.error("error while applying " + action + " to context \n[" + context + "] \n" + ExceptionUtils.getStackTrace(e), e);
        }
    }

    public void setProvider(Provider<T> provider) {
        Assert.isNull(this.provider, "provider already set");
        provider.setProcessor(this);
        this.provider = provider;
    }

    static class PerformerWrapper<T> {
        Performer<T> performer;
        RunPolicy<T> runPolicy;

        public PerformerWrapper(Performer<T> performer, RunPolicy<T> runPolicy) {
            super();
            this.performer = performer;
            this.runPolicy = runPolicy;
        }
    }
}