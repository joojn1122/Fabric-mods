package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.util.math.Vec3d;

public class ClientOffset extends Module {

    public static final ClientOffset INSTANCE = new ClientOffset();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> x = sgGeneral.add(new DoubleSetting.Builder()
            .name("x")
            .description("X offset")
            .defaultValue(0)
            .range(-10, 10)
            .sliderRange(-10, 10)
            .build()
    );

    private final Setting<Double> y = sgGeneral.add(new DoubleSetting.Builder()
            .name("y")
            .description("Y offset")
            .defaultValue(0)
            .range(-10, 10)
            .sliderRange(-10, 10)
            .build()
    );

    private final Setting<Double> z = sgGeneral.add(new DoubleSetting.Builder()
            .name("z")
            .description("Z offset")
            .defaultValue(0)
            .range(-10, 10)
            .sliderRange(-10, 10)
            .build()
    );

    public ClientOffset() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "client-offset",
                "Allows you to offset your client."
        );
    }

    public Vec3d getOffset(Vec3d vec3d) {
        if(!this.isActive()) return vec3d;

        return vec3d.add(x.get(), y.get(), z.get());
    }

}
