package com.blacknebula.flowprocessor.core;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Element Wrapper
 *
 * @param <T> Element type
 */
public class ElementWrapper<T> {

    private final Map<String, List<ValidationError>> errors = new HashMap<>();
    private T element;
    private Object rawElement;
    private Long index;
    private boolean unparseableRow;

    public ElementWrapper(T element, Object rawElement) {
        this(element);
        this.rawElement = rawElement;
    }

    public ElementWrapper(T element) {
        super();
        this.element = element;
    }

    public T getElement() {
        return element;
    }

    public Object getRawElement() {
        return rawElement;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        Assert.isNull(this.index, "should not be null");
        this.index = index;
    }

    public void addError(String field, List<ValidationError> errors) {
        Assert.isTrue(!getErrors().containsKey(field), "Error already added with key: " + field);
        getErrors().put(field, errors);
    }

    public void addError(String key, String error) {
        Assert.isTrue(!getErrors().containsKey(key), "Error already added with key: " + key);
        final List<ValidationError> validationErrors = new ArrayList<>();
        validationErrors.add(new ValidationError(error));
        getErrors().put(key, validationErrors);
    }

    public void addError(String key, String error, Boolean handled) {
        Assert.isTrue(!getErrors().containsKey(key), "Error already added with key: " + key);
        final List<ValidationError> validationErrors = new ArrayList<>();
        ValidationError validationError = new ValidationError(error);
        validationError.setHandled(handled);
        validationErrors.add(validationError);
        getErrors().put(key, validationErrors);
    }

    public void addError(ValidationError validationError) {
        Assert.isTrue(!getErrors().containsKey(validationError.getField()), "Error already added with key: " + validationError.getField());
        final List<ValidationError> validationErrors = new ArrayList<>();
        validationErrors.add(validationError);
        getErrors().put(validationError.getField(), validationErrors);
    }

    public boolean hasErrors() {
        return !getErrors().isEmpty();
    }

    public boolean isUnparseableRow() {
        return unparseableRow;
    }

    public void setUnparseableRow(boolean unparseableRow) {
        this.unparseableRow = unparseableRow;
    }

    public boolean allErrorHandled() {
        return getErrors().entrySet().stream().flatMap(e -> e.getValue().stream()).filter(e -> BooleanUtils.isNotTrue(e.isHandled())).collect(Collectors.toList()).isEmpty();
    }

    public boolean hasError(String key) {
        return getErrors().containsKey(key);
    }

    public Map<String, List<ValidationError>> getErrors() {
        return errors;
    }

    public List<ValidationError> getError(String key) {
        Assert.isTrue(hasError(key), "Error does not contain this key: " + key);
        return getErrors().get(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ElementWrapper [errors=").append(errors).append(", element=").append(element).append(", rawElement=").append(rawElement).append(", index=").append(index)
                .append("]");
        return builder.toString();
    }

}