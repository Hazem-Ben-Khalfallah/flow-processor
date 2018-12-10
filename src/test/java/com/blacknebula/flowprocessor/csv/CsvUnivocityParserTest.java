package com.blacknebula.flowprocessor.csv;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CsvUnivocityParserTest {
    private static final int MAX_CHAR_PER_COLUMN_LIMIT = 4096;
    private static final int MAX_COLUMN_LIMIT = 512;
    private final CsvParameters DEFAULT_CSV_PARAMETER = CsvParameters.createCsvParameterWithDefaultValues();
    protected List<String> DEFAULT_HEADER = Arrays.asList("h1", "h2", "h3", "h4", "h5");
    protected String DEFAULT_FIELD_CONTENT = "l%dc%d";

    @Test
    public void testGetIndexIntervals() {
        // given
        final ParseLimit parseLimit = new ParseLimit(222, 78);
        // when
        final Set<Integer> result = CsvUnivocityParser.getIndexIntervals(parseLimit);
        // then
        final List<Integer> expected = IntStream.range(222, 300)
                .boxed()
                .collect(Collectors.toList());
        Assertions.assertThat(result.size()).isEqualTo(expected.size());
        Assertions.assertThat(result).containsAll(expected);
    }


    @Test
    public void parseCsv_shouldSetInvalidRowToTrueIfContentExceedsMaxCharPerColumnLimit() {
        // given
        // A csv with a number of fields exceeding the limit
        final String invalidCsv = CsvBuilder.generateCsvRows(DEFAULT_HEADER, DEFAULT_FIELD_CONTENT,
                CsvParameters.DEFAULT_FIELD_DELIMITER.toString(), CsvParameters.DEFAULT_LINE_DELIMITER,
                CsvParameters.DEFAULT_QUOTE.toString(), 10, MAX_COLUMN_LIMIT + 1);

        // when
        boolean isUnparsableRow = callCsvParser(invalidCsv, DEFAULT_CSV_PARAMETER);

        //Then
        Assertions.assertThat(isUnparsableRow).isTrue();
    }

    @Test
    public void parseCsv_shouldSetInvalidRowToFalseIfContentHasNotExceedsMaxCharPerColumnLimit() {
        // given
        final String invalidCsv = CsvBuilder.generateCsvRows(DEFAULT_HEADER, DEFAULT_FIELD_CONTENT,
                CsvParameters.DEFAULT_FIELD_DELIMITER.toString(), CsvParameters.DEFAULT_LINE_DELIMITER,
                CsvParameters.DEFAULT_QUOTE.toString(), 10,
                MAX_COLUMN_LIMIT - 1);

        // when
        boolean isUnparsableRow = callCsvParser(invalidCsv, DEFAULT_CSV_PARAMETER);

        //Then
        Assertions.assertThat(isUnparsableRow).isFalse();
    }

    private boolean callCsvParser(String csvContent, CsvParameters csvParameter) {
        return callCsvParser(csvContent.getBytes(), csvParameter);
    }

    private boolean callCsvParser(byte[] csvContent, CsvParameters csvParameter) {
        InputStream input = null;
        try {
            if (csvContent != null) {
                input = new ByteArrayInputStream(csvContent);
            }
            final CsvUnivocityParser parser = new CsvUnivocityParser(input, csvParameter);
            final boolean[] isInvalidRow = new boolean[1];
            parser.parseCsv( //
                    null, //
                    (csvElement) -> isInvalidRow[0] = csvElement.isUnparsableRow(),//
                    () -> false
            );
            return isInvalidRow[0];
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
