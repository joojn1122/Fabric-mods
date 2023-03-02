package com.joojn.meteoraddon.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class PlayerUtil {

    public static void sendChatMessage(String message) {
        if(MinecraftClient.getInstance().player == null) return;

        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
        addChatMessage(message);
    }

    public static void addChatMessage(String message) {
        if(MinecraftClient.getInstance().player == null) return;

        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(message));
    }
}
