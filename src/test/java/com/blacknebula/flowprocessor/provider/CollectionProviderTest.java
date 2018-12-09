package com.blacknebula.flowprocessor.provider;

import com.blacknebula.flowprocessor.core.Performer;
import com.blacknebula.flowprocessor.core.Provider;
import com.blacknebula.flowprocessor.processor.Processor;
import com.blacknebula.flowprocessor.processor.ProcessorBuilder;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CollectionProviderTest {

    private static final String PROCESSOR_FIELD = "processor";
    private static final String PROVIDER_FIELD = "provider";

    @Mock
    private Provider<Integer> provider;

    private Processor<Integer> onBoardingFlow;

    @Before
    public void setup() {
        @SuppressWarnings("unchecked") final Performer<Integer> anyPerformer = mock(Performer.class);
        onBoardingFlow = Mockito.spy(
                ProcessorBuilder
                        .processorForType(Integer.class)
                        .withProvider(provider)
                        .startFlowDefinition()
                        .registerPerformer(anyPerformer)
                        .endFlowDefinition()
                        .build());
    }

    @After
    @SuppressWarnings("unchecked")
    public void cleanUp() {
        reset(provider);
    }

    @Test
    public void testArrayProvider() {
        // given
        this.provider = Mockito.spy(new CollectionProvider<>(1, 2, 3));
        ReflectionTestUtils.setField(this.provider, PROCESSOR_FIELD, onBoardingFlow);
        ReflectionTestUtils.setField(onBoardingFlow, PROVIDER_FIELD, this.provider);
        // when
        onBoardingFlow.start();
        // then
        verify(this.provider, times(3)).emitElement(any(), any(), any(), anyBoolean());
        verify(onBoardingFlow, times(1)).onElements(any());
    }

    @Test
    public void testCollectionProvider() {
        // given
        provider = spy(new CollectionProvider<>(ImmutableList.<Integer>builder().add(1, 2, 3).build()));
        ReflectionTestUtils.setField(provider, PROCESSOR_FIELD, onBoardingFlow);
        ReflectionTestUtils.setField(onBoardingFlow, PROVIDER_FIELD, provider);
        // when
        onBoardingFlow.start();
        // then
        verify(provider, times(3)).emitElement(any(), any(), any(), anyBoolean());
        verify(onBoardingFlow, times(1)).onElements(any());
    }

    @Test
    public void testStreamProvider() {
        // given
        provider = spy(new CollectionProvider<>(Stream.of(1, 2, 3)));
        ReflectionTestUtils.setField(provider, PROCESSOR_FIELD, onBoardingFlow);
        ReflectionTestUtils.setField(onBoardingFlow, PROVIDER_FIELD, provider);
        // when
        onBoardingFlow.start();
        // then
        verify(provider, times(3)).emitElement(any(), any(), any(), anyBoolean());
        verify(onBoardingFlow, times(1)).onElements(any());
    }
}
