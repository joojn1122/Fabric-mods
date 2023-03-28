package com.joojn.meteoraddon.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.concurrent.atomic.AtomicInteger;

public class InventoryUtils {

    public static int getEmptySlotCount() {

        MinecraftClient mc = MinecraftClient.getInstance();

        AtomicInteger emptySlots = new AtomicInteger(0);

        mc.player.getInventory().main.forEach(itemStack -> {
            if (itemStack.isEmpty()) {
                emptySlots.incrementAndGet();
            }
        });

        return emptySlots.get();
    }

    public static int getEmptySlot(int size, ScreenHandler screenHandler) {

        for(int i = screenHandler.slots.size(); i > size; i -= 9) {
            for(int j = 9; j > 0; j--) {
                if (screenHandler.getSlot(i - j).getStack().isEmpty()) {
                    return i - j;
                }
            }
        }

        return -1;
    }

    public static void swapSlots(
            DefaultedList<ItemStack> inventory,
            int from,
            int to // hotkey 0-8
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();

        int inventoryTo = 36 + to;
        int inventoryFrom = from < 9 ? from + 36 : from;

        if(inventoryTo == inventoryFrom) return;

        ItemStack fromStack = inventory.get(from);
        ItemStack toStack   = inventory.get(to);

        // client side
        inventory.set(from, toStack);
        inventory.set(to, fromStack);

        // server side
        Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
        map.put(inventoryFrom, toStack);
        map.put(inventoryTo, fromStack);

        ClickSlotC2SPacket swapSlotPacket = new ClickSlotC2SPacket(
                mc.player.playerScreenHandler.syncId,
                mc.player.playerScreenHandler.getRevision(),
                inventoryFrom,
                to,
                SlotActionType.SWAP,
                ItemStack.EMPTY,
                map
        );

        mc.player.networkHandler.sendPacket(swapSlotPacket);
    }

    public static void shiftClickSlot(
            ScreenHandler screenHandler,
            int slot,
            int contentSize
    ) {
        int destSlotIndex = InventoryUtils.getEmptySlot(contentSize, screenHandler);
        if(destSlotIndex == -1) return;

        Slot fromSlot = screenHandler.getSlot(slot);
        Slot toSlot = screenHandler.getSlot(destSlotIndex);

        ItemStack fromStack = fromSlot.getStack();
        ItemStack toStack = toSlot.getStack();

        fromSlot.setStack(toStack);
        toSlot.setStack(fromStack);

        MinecraftClient mc = MinecraftClient.getInstance();

        Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
        map.put(slot, toStack);
        map.put(destSlotIndex, fromStack);

        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(
                screenHandler.syncId,
                screenHandler.getRevision(),
                slot,
                0,
                SlotActionType.QUICK_MOVE,
                ItemStack.EMPTY,
                map
        );

        mc.player.networkHandler.sendPacket(packet);
    }
}
