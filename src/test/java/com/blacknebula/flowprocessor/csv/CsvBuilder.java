package com.blacknebula.flowprocessor.csv;


import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

public class CsvBuilder {

    public static String generateCsvRows(List<String> headers, String fieldContent, String fieldDelimiter, String recordDelimiter, String quote, int nbRows, int nbFieldsPerRow) {
        List<String> csvLines = new ArrayList<>();
        if (headers != null) {
            csvLines.add(getCsvRow(headers, fieldDelimiter, quote));
        }
        for (int i = 0; i < nbRows; i++) {
            csvLines.add(generateCsvRow(fieldContent, fieldDelimiter, quote, nbFieldsPerRow, i));
        }
        return getCsvRows(csvLines, recordDelimiter);
    }

    public static String getCsvRow(List<String> values, String fieldDelimiter, String quoteDelimiter) {
        if (values != null && values.size() > 0) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < values.size() - 1; i++) {
                builder.append(quoteStr(values.get(i), quoteDelimiter)).append(fieldDelimiter);
            }
            builder.append(quoteStr(values.get(values.size() - 1), quoteDelimiter));
            return builder.toString();
        }
        return null;
    }

    private static String getCsvRows(List<String> csvLines, String lineDelimiter) {
        final String unescapedLineDelimiter = StringEscapeUtils.unescapeJava(lineDelimiter);
        final StringBuilder sb = new StringBuilder();
        for (String line : csvLines) {
            sb.append(line).append(unescapedLineDelimiter);
        }
        return sb.toString();
    }

    private static String quoteStr(String val, String quote) {
        return quote + val + quote;
    }

    private static List<String> generateValues(String fieldContent, int nbField, int lineIndex) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < nbField; i++) {
            list.add(String.format(fieldContent, lineIndex, i));
        }
        return list;
    }

    private static String generateCsvRow(String fieldContent, String fieldDelimiter, String quote, int nbField, int lineIndex) {
        return getCsvRow(generateValues(fieldContent, nbField, lineIndex), fieldDelimiter, quote);
    }

}
