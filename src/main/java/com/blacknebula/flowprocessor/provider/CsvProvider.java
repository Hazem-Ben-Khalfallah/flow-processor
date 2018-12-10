package com.blacknebula.flowprocessor.provider;


import com.blacknebula.flowprocessor.core.Context;
import com.blacknebula.flowprocessor.core.Provider;
import com.blacknebula.flowprocessor.csv.CsvElement;
import com.blacknebula.flowprocessor.csv.CsvParameters;
import com.blacknebula.flowprocessor.csv.CsvUnivocityParser;
import com.blacknebula.flowprocessor.csv.ParseLimit;
import com.blacknebula.flowprocessor.processor.ProviderRunPolicy;

import java.io.InputStream;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class CsvProvider<T> extends Provider<T> {
    private static final String CSV_HEADERS = "csvHeaders";

    private InputStream inputStream;
    private CsvParameters csvParameters;

    public CsvProvider(InputStream inputStream, CsvParameters csvParameters) {
        this.inputStream = inputStream;
        this.csvParameters = csvParameters;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void start(Context<T> ctx, ProviderRunPolicy<T> providerRunPolicy) {
        final CsvUnivocityParser csvUnivocityParser = new CsvUnivocityParser(inputStream, csvParameters);
        Objects.requireNonNull(inputStream, "CsvProvider require non null InputStream");
        Objects.requireNonNull(csvParameters, "CsvProvider require non null csv parameters");
        final ParseLimit parseLimit = ctx.getOrDefault(Context.PARSE_LIMIT, null);//
        csvUnivocityParser.parseCsv( //
                parseLimit, //
                getCsvElementConsumer(ctx),
                () -> !providerRunPolicy.test(ctx));
        onEnd();
    }

    private Consumer<CsvElement> getCsvElementConsumer(Context<T> ctx) {
        return csvElement -> {
            if (!ctx.hasFlowParam(CSV_HEADERS)) {
                ctx.addFlowParam(CSV_HEADERS, csvElement.getHeader());
            }
            final T dto = transformCsvLineToDto(ctx, csvElement.getHeader(), csvElement.getRow());
            emitElement(dto, csvElement.getRow(), (long) csvElement.getIndex(), csvElement.isUnparsableRow());
        };
    }

    protected abstract T transformCsvLineToDto(Context<T> ctx, String[] header, String[] row);
}
