package com.blacknebula.flowprocessor.core;

import com.blacknebula.flowprocessor.performer.CounterPerformer;
import com.blacknebula.flowprocessor.performer.ProcessInterruptionPerformer;
import com.blacknebula.flowprocessor.processor.Processor;
import com.blacknebula.flowprocessor.processor.ProcessorBuilder;
import com.blacknebula.flowprocessor.processor.ProviderRunPolicy;
import com.blacknebula.flowprocessor.provider.CollectionProvider;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class ProcessInterruptionTest {

    // TOTAL = 2 * bulk size
    private static final int TOTAL = 1000;

    private Provider<Integer> provider;

    @Before
    public void setup() {
        provider = new CollectionProvider<>(IntStream.range(0, TOTAL).boxed());
    }

    @Test
    public void testCountElements() {
        // given
        final Processor<Integer> onBoardingFlow = ProcessorBuilder
                .processorForType(Integer.class)
                .withProvider(provider, ProviderRunPolicy.flowInterrupted().negate())
                .startFlowDefinition()
                .registerPerformer(new CounterPerformer<>())
                .registerPerformer(new ProcessInterruptionPerformer<>())
                .endFlowDefinition()
                .addFlowParam(ProcessInterruptionPerformer.LIMIT, 2)
                .build();

        //when
        final Context<Integer> context = onBoardingFlow.start();

        // then
        Assertions.assertThat(context.getFlowParam(ProcessInterruptionPerformer.LIMIT, Integer.class)).isLessThan(provider.getBulkSize());
        Assertions.assertThat(context.getFlowParam(CounterPerformer.TOTAL_ELEMENTS_COUNT, Integer.class)).isEqualTo(provider.getBulkSize());
    }
}
