package com.blacknebula.flowprocessor.processor;

public interface ProcessorBuilder22<T> {

    ProcessorBuilder22<T> defaultProviderApplicationPolicy(RunPolicy<T> policy);

    ProcessorBuilder22<T> ignoreElementPolicy(RunPolicy<T> policy);

    ProcessorBuilder22<T> elementProcessingTimeLimit(ProcessingTimeLimitPolicy limit);

    ProcessorBuilder21<T> endPoliciesConfig();
}
