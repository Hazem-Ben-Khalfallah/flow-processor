package com.blacknebula.flowprocessor.processor;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.Performer;
import com.blacknebula.flowprocessor.core.Provider;
import org.apache.commons.collections.MapUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ProcessorBuilder<T> implements ProcessorBuilder4<T>, ProcessorBuilder3<T>, ProcessorBuilder21<T>, ProcessorBuilder22<T>, ProcessorBuilder2<T>, ProcessorBuilder1<T> {

    private static final int PROCESSING_TIMEOUT = 30;
    private final List<Processor.PerformerWrapper<T>> performers = new ArrayList<>();
    private RunPolicy<T> defaultPerformerRunPolicy = RunPolicy.alwaysRun();
    private RunPolicy<T> ignoreElementPolicy = RunPolicy.<T>alwaysRun().negate();
    private ProviderRunPolicy<T> providerRunPolicy = ProviderRunPolicy.alwaysRun();
    private ProcessingTimeLimitPolicy timeLimitPolicy;
    private Provider<T> provider;
    private Context<T> context = new Context<>();

    public static <S> ProcessorBuilder1<S> processorForType(Class<S> type) {
        return new ProcessorBuilder<>();
    }

    @Override
    public ProcessorBuilder<T> withProvider(Provider<T> provider) {
        return withProvider(provider, ProviderRunPolicy.alwaysRun());
    }

    @Override
    public ProcessorBuilder<T> withProvider(Provider<T> provider, ProviderRunPolicy providerRunPolicy) {
        Objects.requireNonNull(provider, "Provider is null");
        Assert.isNull(this.provider, "Provider is to be set only once");
        this.provider = provider;
        this.providerRunPolicy = providerRunPolicy;
        return this;
    }

    @Override
    public ProcessorBuilder22<T> startPoliciesConfig() {
        return this;
    }

    @Override
    public ProcessorBuilder22<T> defaultProviderApplicationPolicy(RunPolicy<T> policy) {
        defaultPerformerRunPolicy = policy;
        return this;
    }

    @Override
    public ProcessorBuilder22<T> ignoreElementPolicy(RunPolicy<T> policy) {
        ignoreElementPolicy = policy;
        return this;
    }

    @Override
    public ProcessorBuilder22<T> elementProcessingTimeLimit(ProcessingTimeLimitPolicy limit) {
        Assert.isNull(timeLimitPolicy, "timeLimitPolicy is already set");
        timeLimitPolicy = limit;
        return this;
    }

    @Override
    public ProcessorBuilder21<T> endPoliciesConfig() {
        return this;
    }

    @Override
    public ProcessorBuilder3<T> registerPerformer(Performer<T> performer) {
        Objects.requireNonNull(performer, "Performer is null");
        performers.add(new Processor.PerformerWrapper<>(performer, defaultPerformerRunPolicy));
        return this;
    }

    @Override
    public ProcessorBuilder3<T> registerPerformer(Performer<T> performer, RunPolicy<T> policy) {
        Objects.requireNonNull(performer, "Performer is null");
        Objects.requireNonNull(policy, "policy is null");
        performers.add(new Processor.PerformerWrapper<>(performer, policy));
        return this;
    }

    @Override
    public ProcessorBuilder3<T> startFlowDefinition() {
        return this;
    }

    @Override
    public ProcessorBuilder4<T> endFlowDefinition() {
        return this;
    }

    @Override
    public ProcessorBuilder4<T> addFlowParam(String key, Object value) {
        Objects.requireNonNull(key, "flow param key is null");
        Objects.requireNonNull(value, String.format("flow param [%s] value is null", key));
        context.addFlowParam(key, value);
        return this;
    }

    @Override
    public ProcessorBuilder4<T> addNullableFlowParam(String key, Object value) {
        Objects.requireNonNull(key, "flow param key is null");
        context.addFlowParam(key, value);
        return this;
    }

    @Override
    public ProcessorBuilder4<T> addFlowParams(Map<String, Object> params) {
        if (MapUtils.isNotEmpty(params)) {
            params.forEach((key, value) -> context.addFlowParam(key, value));
        }
        return this;
    }

    @Override
    public Processor<T> build() {

        if (Objects.isNull(timeLimitPolicy))

            timeLimitPolicy = ProcessingTimeLimitPolicy.fixedLimit(PROCESSING_TIMEOUT, TimeUnit.SECONDS);

        // context && ignoreElementPolicy
        final Processor<T> processor = new Processor<>(context, ignoreElementPolicy, timeLimitPolicy, providerRunPolicy);

        // provider
        processor.setProvider(provider);

        // performers
        performers.forEach(performerWrapper -> processor.addPerformer(performerWrapper.performer, performerWrapper.runPolicy));

        return processor;
    }
}
