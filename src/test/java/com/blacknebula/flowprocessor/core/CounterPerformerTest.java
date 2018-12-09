package com.blacknebula.flowprocessor.core;

import com.blacknebula.flowprocessor.performer.CounterPerformer;
import com.blacknebula.flowprocessor.processor.Processor;
import com.blacknebula.flowprocessor.processor.ProcessorBuilder;
import com.blacknebula.flowprocessor.provider.CollectionProvider;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.IntStream;

public class CounterPerformerTest {

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
                .withProvider(provider)
                .startFlowDefinition()
                .registerPerformer(new CounterPerformer<>())
                .endFlowDefinition()
                .build();

        //when
        final Context<Integer> context = onBoardingFlow.start();

        // then
        Assertions.assertThat(context.getFlowParam(CounterPerformer.TOTAL_ELEMENTS_COUNT, Integer.class)).isEqualTo(TOTAL);
    }
}
