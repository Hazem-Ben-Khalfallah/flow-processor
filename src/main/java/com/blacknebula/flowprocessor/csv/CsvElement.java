package com.blacknebula.flowprocessor.csv;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author mariem
 */
public class CsvElement {
    private String[] Header;
    private String[] row;
    private int index;
    private boolean unparsableRow;

    public CsvElement(String[] header, String[] row, int index, boolean unparsableRow) {
        Header = header;
        this.row = row;
        this.index = index;
        this.unparsableRow = unparsableRow;
    }

    public String[] getHeader() {
        return Header;
    }

    public void setHeader(String[] header) {
        Header = header;
    }

    public String[] getRow() {
        return row;
    }

    public void setRow(String[] row) {
        this.row = row;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isUnparsableRow() {
        return unparsableRow;
    }

    public void setUnparsableRow(boolean unparsableRow) {
        this.unparsableRow = unparsableRow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CsvElement that = (CsvElement) o;

        return new EqualsBuilder()
                .append(index, that.index)
                .append(unparsableRow, that.unparsableRow)
                .append(Header, that.Header)
                .append(row, that.row)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(Header)
                .append(row)
                .append(index)
                .append(unparsableRow)
                .toHashCode();
    }
}
