package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import com.joojn.meteoraddon.imixin.IClientInteractionManager;
import com.joojn.meteoraddon.utils.InventoryUtils;
import com.joojn.meteoraddon.utils.PositionUtil;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class AutoMinecart extends Module {

    private enum ContentState {
        WAITING,
        FOUND,
        NONE
    }

    private int startX;
    private int startY;
    private int startZ;

    private boolean stopped;
    private boolean init;

    private ContentState contentState;
    private int contentSize;

    private BlockPos lastChestPos;
    private float lastYaw = 0;
    private float lastPitch = 0;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> chestRange = sgGeneral.add(new IntSetting.Builder()
            .name("chest-range")
            .description("The range to search for chests.")
            .defaultValue(5)
            .sliderRange(1, 5)
            .range(1, 5)
            .onChanged(value -> onActivate())
            .build()
    );

    // TODO: Make module less blatant by adding a delay between placing minecarts etc.
    public AutoMinecart() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "auto-minecart",
                "Automatically places minecarts on rails from chests around you. (Lag machine)"
        );

        onActivate();
    }

    @Override
    public void onActivate() {
        startX = -chestRange.get();
        startY = -chestRange.get();
        startZ = -chestRange.get();

        stopped = false;
        init = false;
        contentSize = 0;

        contentState = ContentState.NONE;

        usedChests.clear();
    }

    private final List<Timer> tasks = new ArrayList<>();
    private final Set<BlockPos> usedChests = new HashSet<>();

    public static class Timer{
        private int delay;
        private final Runnable runnable;
        private final boolean blocking;

        public Timer(int delay, Runnable runnable, boolean blocking) {
            this.delay = delay;
            this.runnable = runnable;
            this.blocking = blocking;
        }
    }

    public void setTimeout(Runnable runnable, int delay, boolean blocking) {
        tasks.add(new Timer(delay, runnable, blocking));
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        if(mc.world == null || stopped) return;

        if(tasks.size() > 0){
            Iterator<Timer> iterator = tasks.iterator();
            boolean block = false;

            while(iterator.hasNext()) {
                Timer timer = iterator.next();

                if(--timer.delay < 0) {
                    timer.runnable.run();
                    iterator.remove();
                }

                if(timer.blocking) block = true;
            }

            if(block) return;
        }

        if(contentState != ContentState.NONE) {
            if(contentState == ContentState.WAITING) return;

            int emptySlots = InventoryUtils.getEmptySlotCount();

            // collect items from chest
            if(emptySlots == 0) {

                // close inventory
                mc.player.closeHandledScreen();
                mc.setScreen(null);

                contentState = ContentState.NONE;

                return;
            }

            int currentN = 0;

            ScreenHandler screenHandler = mc.player.currentScreenHandler;

            try{
                for(int i = 0; i < contentSize; i++) {
                    Slot slot = screenHandler.getSlot(i);
                    ItemStack stack = slot.getStack();

                    if(stack.getItem() != Items.MINECART) continue;
                    if(++currentN > emptySlots) break;

                    InventoryUtils.shiftClickSlot(
                            screenHandler,
                            i,
                            contentSize
                    );
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            if(currentN == 0) {
                // close inventory
                mc.player.closeHandledScreen();
                mc.setScreen(null);

                contentState = ContentState.NONE;

                BlockPos doubleChestPos = PositionUtil.getDoubleChestLocation(lastChestPos);
                if(doubleChestPos != null) usedChests.add(doubleChestPos);
                usedChests.add(lastChestPos);

                lastChestPos = null;
            }

            mc.player.setYaw(lastYaw);
            mc.player.setPitch(lastPitch);

            return;
        }

        // get block which player is looking at
        HitResult result = mc.crosshairTarget; // mc.player.raycast(5, 0, false);
        if(result == null || result.getType() != HitResult.Type.BLOCK) return;

        BlockState targetBlock = mc.world.getBlockState(((BlockHitResult) result).getBlockPos());
        if(targetBlock.getBlock() != Blocks.RAIL) return;

        // first start by looking in inventory for minecarts
        if(searchInventory()) return;

        if(!init) {
            init = true;
            ChatUtils.sendMsg(Text.literal("Starting to search for chests..."));
        }

        if(startX >= chestRange.get()) {
            stopped = true;

            ChatUtils.sendMsg(Text.literal("Finished searching for chests."));
            return;
        }

        // TODO: Add better chest search :P
        for( ; startX < chestRange.get(); startX++)
        {
            for(int y = startY ; y < chestRange.get(); y++)
            {
                for(int z = startZ ; z < chestRange.get(); z++)
                {
                    int finalY = y;
                    int finalZ = z;

                    if(usedChests.stream().anyMatch(pos -> PositionUtil.playerPosEquals(pos, startX, finalY, finalZ))) continue;

                    lastChestPos = mc.player.getBlockPos().add(startX, y, z);

                    BlockState blockState = mc.world.getBlockState(lastChestPos);
                    Block block = blockState.getBlock();

                    if(block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
                        if(ChestBlock.isChestBlocked(mc.world, lastChestPos)) continue;

                        lastYaw = mc.player.getYaw();
                        lastPitch = mc.player.getPitch();

                        PositionUtil.faceBlock(lastChestPos);

                        HitResult hitResult = mc.player.raycast(5, 0, false);
                        if(hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) continue;

                        setTimeout(() -> {

                            // send open chest packet
                            IClientInteractionManager.get().sendSequencedPacket(s -> new PlayerInteractBlockC2SPacket(
                                    Hand.MAIN_HAND,
                                    (BlockHitResult) hitResult,
                                    s
                            ));

                            contentState = ContentState.WAITING;

                        }, 5, true);

                        return;
                    }
                }
            }
        }

    }

    public boolean searchInventory() {
        PlayerInventory inventory = mc.player.getInventory();
        boolean found = false;

        int delay = 0;

        for(int i = 0; i < 36; i++) {
            ItemStack itemStack = inventory.main.get(i);

            if(itemStack.getItem() != Items.MINECART) continue;

            int finalI = i;

            setTimeout(() -> {
                // open inventory
                // mc.setScreen(new InventoryScreen(mc.player));
                if(mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) return;

                inventory.selectedSlot = 0;

                InventoryUtils.swapSlots(
                        inventory.main,
                        finalI,
                        0
                );

                IClientInteractionManager.get().sendSequencedPacket(s -> new PlayerInteractBlockC2SPacket(
                        Hand.MAIN_HAND,
                        (BlockHitResult) mc.crosshairTarget,
                        s
                ));

            }, (++delay) * 2, true);

            found = true;
        }

        return found;
    }

    @EventHandler
    private void onPacketReceived(PacketEvent.Receive event) {
        if(contentState == ContentState.WAITING && event.packet instanceof InventoryS2CPacket packet) {
            contentSize = packet.getContents().size() - 36; // minus inventory size
            contentState = ContentState.FOUND;
        }
    }
}
