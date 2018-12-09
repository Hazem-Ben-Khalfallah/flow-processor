package com.blacknebula.flowprocessor.performer;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.ElementWrapper;
import com.blacknebula.flowprocessor.core.Performer;

public class ProcessInterruptionPerformer<T> implements Performer<T> {
    public static final String LIMIT = "limit";
    private int count = 0;

    @Override
    public void apply(Context<T> ctx, ElementWrapper<T> elementWrapper) {
        final Integer limit = ctx.getFlowParam(LIMIT, Integer.class);
        if (shouldInterruptProcessing(limit)) {
            // Provider will not emit elements anymore
            ctx.setFlowInterrupted();
        } else {
            count++;
        }
    }


    private boolean shouldInterruptProcessing(Integer limit) {
        return count > limit;
    }

}
