package com.joojn.meteoraddon.hud.highlighter.theme;

import com.google.gson.Gson;

public class ThemeImpl implements ITheme {

    private final int comments;
    private final int strings;
    private final int keywords;
    private final int methods;
    private final int fields;
    private final int classes;
    private final int locals;
    private final int defaultColor;
    private final int background;

    public ThemeImpl(
            int comments,
            int strings,
            int keywords,
            int methods,
            int fields,
            int classes,
            int locals,
            int defaultColor,
            int background
    ) {
        this.comments = comments;
        this.strings = strings;
        this.keywords = keywords;
        this.methods = methods;
        this.fields = fields;
        this.classes = classes;
        this.locals = locals;
        this.defaultColor = defaultColor;
        this.background = background;
    }

    @Override
    public int comments() {
        return this.comments;
    }

    @Override
    public int strings() {
        return this.strings;
    }

    @Override
    public int keywords() {
        return this.keywords;
    }

    @Override
    public int methods() {
        return this.methods;
    }

    @Override
    public int fields() {
        return this.fields;
    }

    @Override
    public int classes() {
        return this.classes;
    }

    @Override
    public int locals() {
        return this.locals;
    }

    @Override
    public int defaultColor() {
        return this.defaultColor;
    }

    @Override
    public int background() {
        return this.background;
    }

    public static class Builder {

        // Default values
        private int comments;
        private int strings;
        private int keywords;
        private int methods;
        private int fields;
        private int classes;
        private int locals;
        private int defaultColor;
        private int background;

        public Builder comments(int comments) {
            this.comments = comments;

            return this;
        }

        public Builder strings(int strings) {
            this.strings = strings;

            return this;
        }

        public Builder keywords(int keywords) {
            this.keywords = keywords;

            return this;
        }

        public Builder methods(int methods) {
            this.methods = methods;

            return this;
        }

        public Builder fields(int fields) {
            this.fields = fields;

            return this;
        }

        public Builder classes(int classes) {
            this.classes = classes;

            return this;
        }

        public Builder locals(int locals) {
            this.locals = locals;

            return this;
        }

        public Builder defaultColor(int defaultColor) {
            this.defaultColor = defaultColor;

            return this;
        }

        public Builder background(int background) {
            this.background = background;

            return this;
        }

        public ThemeImpl build() {
            return new ThemeImpl(
                    this.comments,
                    this.strings,
                    this.keywords,
                    this.methods,
                    this.fields,
                    this.classes,
                    this.locals,
                    this.defaultColor,
                    this.background
            );
        }
    }

    private static final Gson gson = new Gson();

    public static ThemeImpl fromJson(String json) {
        return gson.fromJson(json, ThemeImpl.class);
    }

    public String toJson() {
        return gson.toJson(this, ThemeImpl.class);
    }
}
