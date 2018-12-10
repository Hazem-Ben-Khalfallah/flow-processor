package com.blacknebula.flowprocessor.core;

import com.blacknebula.flowprocessor.processor.Processor;
import com.blacknebula.flowprocessor.processor.ProcessorBuilder;
import com.blacknebula.flowprocessor.processor.RunPolicy;
import com.blacknebula.flowprocessor.provider.CollectionProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.stubbing.Answer;

import java.util.stream.IntStream;

import static com.blacknebula.flowprocessor.processor.RunPolicy.alwaysRun;
import static com.blacknebula.flowprocessor.processor.RunPolicy.contextHasParam;
import static com.blacknebula.flowprocessor.processor.RunPolicy.elementHasErrors;
import static com.blacknebula.flowprocessor.processor.RunPolicy.elementHasNoErrors;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(BlockJUnit4ClassRunner.class)
public class PerformerRunPolicyTest {

    private static final String NEW_KEY = "some.key";
    private static final int TOTAL = 100;

    private Provider<Integer> provider;
    private DummyPerformer shouldNeverBeCalledWhenElementHasErrors;
    private DummyPerformer aPerformerThatAddsFlowParams;
    private DummyPerformer shouldAlwaysBeCalled;
    private DummyPerformer anyPerformer;
    private DummyPerformer contextHasPram;
    private DummyPerformer theErrorProducingPerformer;
    private DummyPerformer contextHasNewParamAndElementHasError;
    private DummyPerformer contextHasNotPram;
    private DummyPerformer contextHasNotPramAndElementHasError;

    @Before
    public void setup() {
        provider = new CollectionProvider<>(IntStream.range(0, TOTAL).boxed());
        theErrorProducingPerformer = getSpy();
        doAnswer((Answer<Void>) invocation -> {
            ElementWrapper<Number> elementWrapperArgument = invocation.getArgument(1);
            //even throws error
            if (elementWrapperArgument.getElement().intValue() % 2 == 0) {
                elementWrapperArgument.addError("key1", "ErrorMessage");
            }
            return null;
        }).when(theErrorProducingPerformer).apply(any(), any());
        shouldAlwaysBeCalled = getSpy();
        shouldNeverBeCalledWhenElementHasErrors = getSpy();
        aPerformerThatAddsFlowParams = getSpy();
        doAnswer((Answer<Void>) invocation -> {
            Context<Number> ctx = invocation.getArgument(0);
            if (!ctx.hasFlowParam(NEW_KEY)) {
                ctx.addFlowParam(NEW_KEY, 0);
            }
            return null;
        }).when(aPerformerThatAddsFlowParams).apply(any(), any());
        anyPerformer = getSpy();
        contextHasPram = getSpy();
        contextHasNotPram = getSpy();
        contextHasNotPramAndElementHasError = getSpy();
        contextHasNewParamAndElementHasError = getSpy();

    }

    @Test
    public void testRunPolicyEffectOnPerformerExecution() {
        // given
        final String VALID_KEY = "key";
        final Processor<Integer> onBoardingFlow = ProcessorBuilder
                .processorForType(Integer.class)
                .withProvider(provider)
                .startFlowDefinition()
                .registerPerformer(anyPerformer)
                .registerPerformer(contextHasPram, contextHasParam(NEW_KEY))
                .registerPerformer(aPerformerThatAddsFlowParams)
                .registerPerformer(theErrorProducingPerformer)
                .registerPerformer(contextHasNewParamAndElementHasError, RunPolicy.<Integer>elementHasErrors().and(contextHasParam(NEW_KEY)))
                .registerPerformer(shouldNeverBeCalledWhenElementHasErrors, elementHasNoErrors())
                .registerPerformer(shouldAlwaysBeCalled, alwaysRun())
                //predicate composition examples
                .registerPerformer(contextHasNotPram, RunPolicy.<Integer>contextHasParam(VALID_KEY).negate())
                .registerPerformer(contextHasNotPramAndElementHasError, RunPolicy.<Integer>contextHasParam("invalid_key").negate().and(elementHasErrors()))
                .endFlowDefinition()
                .addFlowParam(VALID_KEY, 0)
                .build();

        // when
        onBoardingFlow.start();

        // then
        verify(anyPerformer, times(TOTAL)).apply(any(), any());
        verify(theErrorProducingPerformer, times(TOTAL)).apply(any(), any());
        verify(shouldNeverBeCalledWhenElementHasErrors, times(TOTAL / 2)).apply(any(), any());
        verify(shouldAlwaysBeCalled, times(TOTAL)).apply(any(), any());
        verify(contextHasPram, never()).apply(any(), any());
        verify(contextHasNotPram, never()).apply(any(), any());
        verify(contextHasNotPramAndElementHasError, times(TOTAL / 2)).apply(any(), any());
        verify(contextHasNewParamAndElementHasError, times(TOTAL / 2)).apply(any(), any());
    }

    private DummyPerformer getSpy() {
        return spy(new DummyPerformer());
    }

    private class DummyPerformer implements Performer<Integer> {


        @Override
        public void apply(Context ctx, ElementWrapper elementWrapper) {
            // do nothing
        }
    }

}
