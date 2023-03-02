package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import com.joojn.meteoraddon.utils.MathUtil;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.entity.player.PlayerEntity;

public class Misplace extends Module {

    public static final Misplace INSTANCE = new Misplace();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private double offset = 0;
    private final Setting<Double> offsetSetting = sgGeneral.add(new DoubleSetting.Builder()
            .name("offset")
            .description("Player position offset")
            .defaultValue(1)
            .range(-10, 10)
            .sliderRange(-10, 10)
            .onChanged(e -> offset = e / 10)
            .build()
    );

    private final Setting<Boolean> targetSpecific = sgGeneral.add(new BoolSetting.Builder()
            .name("target-specific")
            .description("Only target specific player.")
            .defaultValue(false)
            .build()
    );

    private final Setting<String> targetName = sgGeneral.add(new StringSetting.Builder()
            .name("target-name")
            .description("The name of the player to target.")
            .defaultValue("joojn")
            .visible(targetSpecific::get)
            .build()
    );

    public Misplace() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "misplace",
                "Moves players to the wrong place."
        );
    }

    public double[] modifyPosition(PlayerEntity player, double x_, double z_){

        if(
                !this.isActive() || (
                        targetSpecific.get()
                                && !player.getName().getString().equalsIgnoreCase(targetName.get())
        )) return new double[] {
                x_,
                z_
        };

        double x = x_ / 32.0D;
        double z = z_ / 32.0D;

        double f = offset;

        double c = Math.hypot(
                mc.player.getX() - x,
                mc.player.getZ() - z
        );

        if (f > c) {
            f -= c;
        }

        float r = MathUtil.degrees(x, z);

        if (MathUtil.getRealYaw(mc.player.getYaw(), r) > 180.0D) {
            return new double[] {
                    x,
                    z
            };
        }

        double a = Math.cos(Math.toRadians((r + 90.0F)));
        double b = Math.sin(Math.toRadians((r + 90.0F)));

        x -= a * f;
        z -= b * f;

        x *= 32.0D;
        z *= 32.0D;

        return new double[] {
                x,
                z
        };
    }
}
