package com.blacknebula.flowprocessor.performer;

import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.ElementWrapper;
import com.blacknebula.flowprocessor.core.Performer;

public class CounterPerformer<T> implements Performer<T> {
    public static final String TOTAL_ELEMENTS_COUNT = "totalCount";
    private int count = 0;

    @Override
    public void apply(Context<T> ctx, ElementWrapper<T> elementWrapper) {
        count++;
    }

    @Override
    public void onFlowEnd(Context<T> ctx) {
        ctx.addFlowParam(TOTAL_ELEMENTS_COUNT, count);
    }

}
