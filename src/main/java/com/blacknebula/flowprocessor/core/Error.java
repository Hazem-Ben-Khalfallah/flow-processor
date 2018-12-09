package com.blacknebula.flowprocessor.core;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("serial")
public class Error<T> extends HashMap<String, Object> {
    public static final String MESSAGE = "messages", INDEX = "index", ELEMENT = "element", RAW_ELEMENT = "raw.element";
    private boolean handled;

    @SuppressWarnings("unchecked")
    public Error(Map<String, List<ValidationError>> validationErrorsMap, long position, T element, Object rawElement) {
        final List<ValidationError> errors = new ArrayList<>();
        List<ValidationError> validationErrors;
        for (String field : validationErrorsMap.keySet()) {
            validationErrors = validationErrorsMap.get(field);
            validationErrors.forEach(validationError -> {
                validationError.setField(field);
                validationError.setMessage(validationError.getErrorCode());
            });
            errors.addAll(validationErrors);
        }

        put(MESSAGE, errors);
        put(INDEX, position);
        put(ELEMENT, element);
        put(RAW_ELEMENT, rawElement);
    }

    @SuppressWarnings("unchecked")
    public List<ValidationError> getMessages() {
        return (List<ValidationError>) get(MESSAGE);
    }

    public long getIndex() {
        return NumberUtils.toLong(Objects.toString(get(INDEX)), -1);
    }

    @SuppressWarnings("unchecked")
    public T getElement() {
        return (T) Objects.requireNonNull(get(ELEMENT));
    }

    public Object getRawElement() {
        return get(RAW_ELEMENT);
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }
}