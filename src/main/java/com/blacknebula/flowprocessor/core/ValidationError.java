package com.blacknebula.flowprocessor.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author hazem
 */
public class ValidationError {
    private Integer column;
    private String errorCode;
    private String invalidValue;
    private String message;
    private String field;
    private Boolean handled;

    public ValidationError(String errorCode) {
        this.column = -1;
        this.errorCode = errorCode;
    }

    public ValidationError(String errorCode, String invalidValue) {
        this(errorCode);
        this.invalidValue = StringUtils.trim(invalidValue);
    }

    public ValidationError(Integer column, String errorCode, String invalidValue) {
        this(errorCode, invalidValue);
        this.column = column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ValidationError that = (ValidationError) o;
        return Objects.equals(column, that.column) && Objects.equals(errorCode, that.errorCode) && Objects.equals(invalidValue, that.invalidValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, errorCode, invalidValue);
    }

    @Override
    public String toString() {
        return "ValidationError [column=" + column + ", errorCode=" + errorCode + ", invalidValue=" + invalidValue + ", message=" + message + ", field=" + field + ", handled=" + handled + "]";
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(String invalidValue) {
        this.invalidValue = invalidValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Boolean isHandled() {
        return handled;
    }

    public void setHandled(Boolean handled) {
        this.handled = handled;
    }
}
