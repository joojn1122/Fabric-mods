package com.joojn.meteoraddon.hud.highlighter.theme;

import java.awt.*;

import static com.joojn.meteoraddon.hud.highlighter.CodeHighlighter.*;

public interface ITheme {
    int comments();
    int strings();
    int keywords();
    int methods();
    int fields();
    int classes();
    int locals();
    int defaultColor();
    int background();

    default int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    default int rgb(int r, int g, int b) {
        return new Color(r, g, b).getRGB();
    }

    default int getColor(int colorIndex) {
        return switch (colorIndex) {
            case COMMENTS -> comments();
            case STRINGS -> strings();
            case KEYWORDS -> keywords();
            case METHODS -> methods();
            case FIELDS -> fields();
            case CLASSES2, CLASSES -> classes();
            case LOCALS -> locals();
            default -> defaultColor();
        };
    }
}
