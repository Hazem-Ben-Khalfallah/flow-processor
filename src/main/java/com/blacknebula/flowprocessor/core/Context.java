package com.blacknebula.flowprocessor.core;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Context<T> {
    public static final String FLOW_INTERRUPTED = "isFlowInterrupted";

    private Map<String, Object> map = new HashMap<>();
    private List<Error<T>> errors = new ArrayList<>();
    private int index, nbFailedElements;

    public Context() {
        init();
    }

    public boolean hasFlowParam(String key) {
        return map.containsKey(key);
    }

    public Object getFlowParam(String key) {
        Objects.requireNonNull(key, "null key");
        Assert.isTrue(map.containsKey(key), "missing value for key:" + key);
        return map.get(key);
    }

    public <V> V addFlowParam(String key, V value) {
        Objects.requireNonNull(key, "null key");
        Assert.isTrue(!map.containsKey(key), "a value is already set for key:" + key);
        map.put(key, value);
        return value;
    }

    public <V> V getFlowParam(String key, Class<V> type) {
        Objects.requireNonNull(key, "null key");
        Objects.requireNonNull(type, "null type");
        Assert.isTrue(map.containsKey(key), "\nno such param with key " + key + " among flow params");
        final Object val = map.get(key);
        Assert.isInstanceOf(type, val, "\nflow param with key " + key + " is not an instance of " + type.getName());
        return (V) val;
    }

    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     * <p>
     * try to make use of {@link #hasFlowParam(String)}
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key the value to which the
     *                     specified key is mapped, or {@code defaultValue} if this map
     *                     contains no mapping for the key
     * @param <V>          value type
     * @return found value or default one
     * @throws ClassCastException if the key is of an inappropriate type for this map
     */
    public <V> V getOrDefault(String key, V defaultValue) {
        return (V) map.getOrDefault(key, defaultValue);
    }

    private void init() {
        map.put(FLOW_INTERRUPTED, false);
    }

    public void incrementIndex() {
        index++;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long currentIndex() {
        return index;
    }

    public void addError(Error<T> error) {
        errors.add(error);
        if (BooleanUtils.isNotTrue(error.isHandled())) {
            nbFailedElements++;
        }
    }

    public boolean hasErrors() {
        return nbFailedElements > 0;
    }

    public boolean isFlowInterrupted() {
        return BooleanUtils.isTrue((Boolean) getFlowParam(FLOW_INTERRUPTED));
    }

    public void setFlowInterrupted() {
        map.put(FLOW_INTERRUPTED, true);
    }

    public List<Error<T>> getErrors() {
        return errors;
    }

    public int getNbFailedElements() {
        return nbFailedElements;
    }
}
