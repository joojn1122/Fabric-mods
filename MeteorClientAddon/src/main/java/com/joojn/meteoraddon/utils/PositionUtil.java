package com.joojn.meteoraddon.utils;

import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.block.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class PositionUtil {

    public static BlockState getBlockState(BlockPos blockPos) {
        return MinecraftClient.getInstance().world.getBlockState(blockPos);
    }

    @Nullable
    public static BlockPos getDoubleChestLocation(
            BlockPos chestPosition
    ) {
        BlockState chest = getBlockState(chestPosition);
        ChestType chestType = chest.get(ChestBlock.CHEST_TYPE);
        boolean left = chestType == ChestType.LEFT;

        if(chestType == ChestType.SINGLE) return null;

        Direction direction = chest.get(ChestBlock.FACING);
        Direction rotated;

        switch (direction){
            case NORTH -> rotated = left ? Direction.EAST  : Direction.WEST;
            case SOUTH -> rotated = left ? Direction.WEST  : Direction.EAST;
            case EAST -> rotated  = left ? Direction.SOUTH : Direction.NORTH;
            case WEST -> rotated  = left ? Direction.NORTH : Direction.SOUTH;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        }

        BlockPos doubleChestPos = chestPosition.add(rotated.getVector());

        BlockState doubleChest = getBlockState(doubleChestPos);
        if(doubleChest.getBlock() != Blocks.CHEST
                || doubleChest.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE) return null;

        return doubleChestPos;
    }

    public static boolean playerPosEquals(BlockPos pos, int x, int y, int z) {
        BlockPos playerPos = MinecraftClient.getInstance().player.getBlockPos();

        return playerPos.add(x, y, z).equals(pos);
    }

    public static void faceBlock(BlockPos blockPos) {

        PlayerEntity player = MinecraftClient.getInstance().player;

        float[] angle = PlayerUtils.calculateAngle(
                Vec3d.ofCenter(blockPos)
        );

        player.setYaw(angle[0]);
        player.setPitch(angle[1]);
    }
}
