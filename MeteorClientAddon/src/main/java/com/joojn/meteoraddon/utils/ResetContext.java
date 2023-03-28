package com.joojn.meteoraddon.utils;

import java.util.ArrayList;
import java.util.List;

public class ResetContext {

    private final List<VariableContext<?>> contexts = new ArrayList<>();

    public <T> VariableContext<T> create(T value) {
        VariableContext<T> context = new VariableContext<>(value);
        this.contexts.add(context);

        return context;
    }

    public void reset() {
        this.contexts.forEach(VariableContext::reset);
    }

    public static class VariableContext<T> {

        private T value;
        private final T defaultValue;

        public VariableContext(T value) {
            this.value = value;
            this.defaultValue = value;
        }

        public void set(T value) {
            this.value = value;
        }

        public T get() {
            return this.value;
        }

        public void reset() {
            this.value = this.defaultValue;
        }
    }
}
