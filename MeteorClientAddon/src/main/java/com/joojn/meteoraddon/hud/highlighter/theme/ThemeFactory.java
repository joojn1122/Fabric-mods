package com.joojn.meteoraddon.hud.highlighter.theme;

import java.awt.*;
import java.util.ArrayList;

public class ThemeFactory {

    private static final ArrayList<ThemeImpl> themes = new ArrayList<>();

    public static void register(ThemeImpl theme) {
        themes.add(theme);
    }

    private static ArrayList<ThemeImpl> getThemes() {
        return themes;
    }

    public static int rgb(int r, int g, int b) {
        return new Color(r, g, b).getRGB();
    }

    public static int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static ThemeImpl ONE_DARK = new ThemeImpl.Builder()
            .classes(rgba(218,189,121,255))
            .comments(rgba(89,84,85,255))
            .strings(rgba(150,193,120,255))
            .keywords(rgba(170,108,202,255))
            .methods(rgb(79,145,205))
            .fields(rgba(215,90,82,255))
            .locals(rgba(198,129,78,255))
            .defaultColor(rgba(150,153,162,255))
            .background(rgba(44,49,60,255))
            .build();

    static {
        register(ONE_DARK);
    }
}
