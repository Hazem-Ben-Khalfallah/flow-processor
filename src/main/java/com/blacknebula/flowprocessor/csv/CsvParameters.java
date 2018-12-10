package com.blacknebula.flowprocessor.csv;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CsvParameters {
    public static final Character DEFAULT_FIELD_DELIMITER = ';';
    public static final String DEFAULT_LINE_DELIMITER = "\n";
    public static final Character DEFAULT_QUOTE = '"';
    public static final Integer DEFAULT_HEADER = 1;
    public static final Character DEFAULT_ESCAPE = '\\';
    public static final Character DEFAULT_COMMENT = '#';

    private Character fieldDelimiter;
    private String lineDelimiter;
    private Character quote;
    private Character escape;
    private Integer headerLineCount;
    private Character comment;

    public CsvParameters() {

    }

    private CsvParameters(Builder builder) {
        setFieldDelimiter(builder.fieldDelimiter);
        setLineDelimiter(builder.recordDelimiter);
        setQuote(builder.quote);
        setEscape(builder.escape);
        setHeaderLineCount(builder.header);
        setComment(builder.comment);
    }


    public static CsvParameters createCsvParameterWithDefaultValues() {
        return CsvParameters.newBuilder()
                .setFieldDelimiter(DEFAULT_FIELD_DELIMITER)
                .setRecordDelimiter(DEFAULT_LINE_DELIMITER)
                .setQuote(DEFAULT_QUOTE)
                .setEscape(DEFAULT_ESCAPE)
                .setHeader(DEFAULT_HEADER)
                .setComment(DEFAULT_COMMENT)
                .build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Character getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(Character fieldSeparator) {
        fieldDelimiter = fieldSeparator;
    }

    public String getLineDelimiter() {
        return lineDelimiter;
    }

    public void setLineDelimiter(String lineDelimiter) {
        this.lineDelimiter = lineDelimiter;
    }

    public Character getQuote() {
        return quote;
    }

    public void setQuote(Character quote) {
        this.quote = quote;
    }

    public Character getEscape() {
        return escape;
    }

    public void setEscape(Character escape) {
        this.escape = escape;
    }

    public int getHeaderLineCount() {
        return headerLineCount;
    }

    public void setHeaderLineCount(int headerLineCount) {
        this.headerLineCount = headerLineCount;
    }

    public Character getComment() {
        return comment;
    }

    public void setComment(Character comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CsvParameters that = (CsvParameters) o;

        return new EqualsBuilder()
                .append(fieldDelimiter, that.fieldDelimiter)
                .append(quote, that.quote)
                .append(escape, that.escape)
                .append(headerLineCount, that.headerLineCount)
                .append(comment, that.comment)
                .append(lineDelimiter, that.lineDelimiter)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fieldDelimiter)
                .append(lineDelimiter)
                .append(quote)
                .append(escape)
                .append(headerLineCount)
                .append(comment)
                .toHashCode();
    }


    public static final class Builder {
        private Character fieldDelimiter;
        private String recordDelimiter;
        private Character quote;
        private Character escape;
        private int header;
        private Character comment;

        private Builder() {
        }

        public Builder setFieldDelimiter(Character fieldDelimiter) {
            this.fieldDelimiter = fieldDelimiter;
            return this;
        }

        public Builder setRecordDelimiter(String recordDelimiter) {
            this.recordDelimiter = recordDelimiter;
            return this;
        }

        public Builder setQuote(Character quote) {
            this.quote = quote;
            return this;
        }

        public Builder setEscape(Character escape) {
            this.escape = escape;
            return this;
        }

        public Builder setHeader(int header) {
            this.header = header;
            return this;
        }

        public Builder setComment(Character comment) {
            this.comment = comment;
            return this;
        }

        public CsvParameters build() {
            return new CsvParameters(this);
        }
    }
}
