package com.blacknebula.flowprocessor.processor;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.ElementWrapper;
import com.blacknebula.flowprocessor.core.Performer;
import com.blacknebula.flowprocessor.core.Provider;
import com.blacknebula.flowprocessor.processor.Processor;
import com.blacknebula.flowprocessor.processor.ProcessorBuilder;
import com.blacknebula.flowprocessor.processor.ProcessorBuilder4;
import com.blacknebula.flowprocessor.processor.ProviderRunPolicy;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.blacknebula.flowprocessor.processor.ProcessingTimeLimitPolicy.fixedLimit;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

@RunWith(BlockJUnit4ClassRunner.class)
public class ElementProcessingTimeoutTest {

    private static final int NB_ELEMENTS = 'Z' - 'A' + 1;
    /**
     * test method timeout, fixture included
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    @Test
    public void testFixedLimit() {
        // given
        final Provider<Character> provider = spy(new DummyProvider());
        final Performer<Character> performer = spy(new DummyPerformer());
        final Processor<Character> processor = ProcessorBuilder.processorForType(Character.class)//
                .withProvider(provider)//
                .startPoliciesConfig()//
                .elementProcessingTimeLimit(fixedLimit(100, TimeUnit.MILLISECONDS))//
                .endPoliciesConfig()//
                .startFlowDefinition()//
                .registerPerformer(performer)//
                .endFlowDefinition()//
                .build();
        // when
        final Context<Character> ctx = processor.start();
        // then
        verify(provider, times(NB_ELEMENTS)).emitElement(any(), any(), any(), anyBoolean());
        verify(performer, atLeastOnce()).apply(any(), any());
        reset(provider, performer);
        Assertions.assertThat(ctx.getErrors()).isNotEmpty();
        // only ascii codes 70, 80 and 90 should timeout
        //All the elements will timeout because it is no longer applied on single element but on batch
        Assertions.assertThat(ctx.getErrors()).hasSize(26);
    }


    @Test
    public void testFixedLimitWithNonReasonableConfig() {
        // given
        final Provider<Character> provider = spy(new DummyProvider());
        final Performer<Character> performer = spy(new DummyPerformer());
        final Processor<Character> processor = ProcessorBuilder.processorForType(Character.class)//
                .withProvider(provider)//
                .startPoliciesConfig()//
                .elementProcessingTimeLimit(fixedLimit(50, TimeUnit.MILLISECONDS))//
                .endPoliciesConfig()//
                .startFlowDefinition()//
                .registerPerformer(performer)//
                .endFlowDefinition()//
                .build();

        // when
        final Context<Character> ctx = processor.start();

        // then
        verify(provider, times(NB_ELEMENTS)).emitElement(any(), any(), any(), anyBoolean());
        verify(performer, atLeastOnce()).apply(any(), any());
        Assertions.assertThat(ctx.getErrors()).isNotEmpty();
        Assertions.assertThat(ctx.getErrors()).satisfies(new Condition<List<?>>() {
            @Override
            public boolean matches(List<?> errors) {
                return errors.size() > 3;
            }
        });
        reset(provider, performer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessingTimeLimitWithBadConfig2() {
        // given
        final Provider<Character> provider = Mockito.mock(DummyProvider.class, withSettings()
                .useConstructor()
                .defaultAnswer(CALLS_REAL_METHODS).stubOnly());
        final Performer<Character> performer = Mockito.mock(DummyPerformer.class, withSettings()
                .useConstructor()
                .defaultAnswer(CALLS_REAL_METHODS).stubOnly());

        final ProcessorBuilder4<Character> processorBuilder = ProcessorBuilder.processorForType(Character.class)//
                .withProvider(provider)//
                .startPoliciesConfig()//
                .elementProcessingTimeLimit(fixedLimit(10, TimeUnit.MILLISECONDS))//
                .elementProcessingTimeLimit(fixedLimit(100, TimeUnit.MILLISECONDS))//
                .endPoliciesConfig()//
                .startFlowDefinition()//
                .registerPerformer(performer)//
                .endFlowDefinition();

        // when
        processorBuilder.build();
    }

    public static class DummyProvider extends Provider<Character> {
        @Override
        public void start(Context<Character> ctx, ProviderRunPolicy<Character> providerRunPolicy) {
            int i = 0;
            for (char c = 'A'; c <= 'Z'; c++) {
                this.emitElement(c, c, (long) i++, false);
            }
            onEnd();
        }
    }

    public static class DummyPerformer implements Performer<Character> {
        @Override
        public void apply(Context<Character> ctx, ElementWrapper<Character> elementWrapper) {
            try {
                if (elementWrapper.getElement() % 10 == 0) {
                    TimeUnit.DAYS.sleep(1);
                } else {
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            } catch (Exception ignored) {
                // do nothing
            }
        }
    }

}
