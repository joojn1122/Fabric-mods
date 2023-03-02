package com.joojn.meteoraddon.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PlayerDamageBlockEvent extends Cancellable {

    private final BlockPos blockPos;
    private final Direction direction;

    public PlayerDamageBlockEvent(BlockPos blockPos, Direction direction) {
        this.blockPos = blockPos;
        this.direction = direction;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Direction getDirection() {
        return direction;
    }

}
