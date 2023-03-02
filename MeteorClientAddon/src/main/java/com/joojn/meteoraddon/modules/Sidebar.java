package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Sidebar extends Module {

    public final static Sidebar INSTANCE = new Sidebar();

    public Sidebar() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "sidebar",
                "Modify your sidebar"
        );
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> showSidebar = sgGeneral.add(new BoolSetting.Builder()
            .name("show-sidebar")
            .description("Show sidebar")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> hideRedNumbers = sgGeneral.add(new BoolSetting.Builder()
            .name("hide-red-numbers")
            .description("Hide red numbers")
            .defaultValue(true)
            .build()
    );

    public boolean hideSidebar() {
        return this.isActive() && !showSidebar.get();
    }

    public boolean hideRedNumbers() {
        return this.isActive() && hideRedNumbers.get();
    }
}
