package com.joojn.meteoraddon;

import com.joojn.meteoraddon.modules.*;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MeteorClientUtils extends MeteorAddon {

    // public static final Category BLATANT_CATEGORY = new Category("Blatant", Items.BARRIER.getDefaultStack());
    public static final Category FUN_CATEGORY = new Category("Fun", Items.TNT.getDefaultStack());
    public static final Logger LOGGER = LoggerFactory.getLogger("MeteorClientUtils");

    @Override
    public void onInitialize() {
        Modules.get().add(new PacketLogger());
        Modules.get().add(new BeeChatReaction());
        Modules.get().add(new FastBreak());

        Modules.get().add(Misplace.INSTANCE);
        Modules.get().add(ClientOffset.INSTANCE);
        Modules.get().add(HypixelGuessTheBuild.INSTANCE);
        Modules.get().add(PlayerHider.INSTANCE);
        Modules.get().add(Sidebar.INSTANCE);
    }

    @Override
    public String getPackage() {
        return "com.joojn.meteoraddon";
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(FUN_CATEGORY);
    }
}
