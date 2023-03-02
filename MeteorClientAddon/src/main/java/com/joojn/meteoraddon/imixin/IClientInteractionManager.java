package com.joojn.meteoraddon.imixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface IClientInteractionManager {

    float getCurrentBreakingProgress();

    void setCurrentBreakingProgress(float currentBreakingProgress);

    int getBlockBreakingCooldown();

    void setBlockBreakingCooldown(int blockBreakingCooldown);

    boolean isBreakingBlock();

    void setBreakingBlock(boolean breakingBlock);

    void sendPlayerActionC2SPacket(Action action, BlockPos blockPos, Direction direction);

    static IClientInteractionManager get() {
        return (IClientInteractionManager) MinecraftClient.getInstance().interactionManager;
    };

}
