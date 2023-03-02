package com.joojn.meteoraddon.utils;

import net.minecraft.client.MinecraftClient;

public class MathUtil {

    public static double getRealYaw(float a, float b) {
        float d = Math.abs(a - b) % 360.0F;
        if (d > 180.0F) {
            d = 360.0F - d;
        }
        return d;
    }

    public static float degrees(double ex, double ez) {
        double x = ex - MinecraftClient.getInstance().player.getX();
        double z = ez - MinecraftClient.getInstance().player.getZ();

        float y = (float) Math.toDegrees(-Math.atan(x / z));
        double v = Math.toDegrees(Math.atan(z / x));

        if (z < 0.0D && x < 0.0D) {
            y = (float) (90.0D + v);
        } else if (z < 0.0D && x > 0.0D) {
            y = (float) (-90.0D + v);
        }

        return y;
    }

}
