package com.joojn.meteoraddon.mixin;

import com.joojn.meteoraddon.events.PlayerDamageBlockEvent;
import com.joojn.meteoraddon.imixin.IClientInteractionManager;
import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientInteractionManagerMixin implements IClientInteractionManager {

    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private float currentBreakingProgress;

    @Shadow
    private int blockBreakingCooldown;

    @Shadow
    private boolean breakingBlock;

    @Shadow
    private native void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);

    @Override
    public float getCurrentBreakingProgress() {
        return currentBreakingProgress;
    }

    @Override
    public void setCurrentBreakingProgress(float currentBreakingProgress) {
        this.currentBreakingProgress = currentBreakingProgress;
    }

    @Override
    public int getBlockBreakingCooldown() {
        return blockBreakingCooldown;
    }

    @Override
    public void setBlockBreakingCooldown(int blockBreakingCooldown) {
        this.blockBreakingCooldown = blockBreakingCooldown;
    }

    @Override
    public boolean isBreakingBlock() {
        return breakingBlock;
    }

    @Override
    public void setBreakingBlock(boolean breakingBlock) {
        this.breakingBlock = breakingBlock;
    }

    @Override
    public void sendPlayerActionC2SPacket(PlayerActionC2SPacket.Action action, BlockPos blockPos, Direction direction)
    {
        sendSequencedPacket(client.world,
                i -> new PlayerActionC2SPacket(action, blockPos, direction, i));
    }

    @Inject(at = {
            @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;getId()I",
                    ordinal = 0)
        }, method = "updateBlockBreakingProgress(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z",
            cancellable = true
    )
    private void onPlayerDamageBlock(
            BlockPos blockPos,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir)
    {
        if(MeteorClient.EVENT_BUS.post(new PlayerDamageBlockEvent(
           blockPos, direction
        )).isCancelled())
            cir.setReturnValue(true);
    }
}
