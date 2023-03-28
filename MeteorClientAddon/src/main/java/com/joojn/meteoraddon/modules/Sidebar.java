package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import meteordevelopment.meteorclient.gui.tabs.builtin.ModulesTab;
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
    private final Setting<Boolean> hideSidebar = sgGeneral.add(new BoolSetting.Builder()
            .name("hide-sidebar")
            .description("Hide sidebar")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> hideRedNumbers = sgGeneral.add(new BoolSetting.Builder()
            .name("hide-red-numbers")
            .description("Hide red numbers")
            .defaultValue(true)
            .build()
    );

    public boolean hideSidebar() {
        return this.isActive() && hideSidebar.get();
    }

    public boolean hideRedNumbers() {
        return this.isActive() && hideRedNumbers.get();
    }
}
