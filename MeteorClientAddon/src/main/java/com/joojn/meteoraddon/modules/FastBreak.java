package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import com.joojn.meteoraddon.events.PlayerDamageBlockEvent;
import com.joojn.meteoraddon.imixin.IClientInteractionManager;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;

public class FastBreak extends Module {

    // used for packet delays
    private final ArrayList<Pair<Integer, Runnable>> tasks = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> blatant = sgGeneral.add(new BoolSetting.Builder()
            .name("blatant")
            .description("Makes the breaking speed even faster.")
            .defaultValue(false)
            .onChanged(e -> tasks.clear())
            .build()
    );

    private final Setting<Integer> multiplier = sgGeneral.add(new IntSetting.Builder()
            .name("multiplier")
            .description("The multiplier for the breaking speed.")
            .defaultValue(1)
            .range(1, 10)
            .sliderRange(1, 10)
            .visible(blatant::get)
            .build()
    );

    public FastBreak() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "fast-break",
                "Breaks blocks faster. (Skidded from Wurst)"
        );
    }

    @EventHandler
    public void onTick(TickEvent.Pre event)
    {
        IClientInteractionManager.get().setBlockBreakingCooldown(0);

        if(!blatant.get())
            return;

        Iterator<Pair<Integer, Runnable>> iterator = tasks.iterator();
        while (iterator.hasNext())
        {
            Pair<Integer, Runnable> task = iterator.next();

            if (task.getLeft() <= 0)
            {
                task.getRight().run();
                iterator.remove();
            }
            else
            {
                task.setLeft(task.getLeft() - 1);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageBlock(PlayerDamageBlockEvent event)
    {
        IClientInteractionManager im = IClientInteractionManager.get();

        if(!blatant.get())
        {
            if(im.getCurrentBreakingProgress() < 0.7f)
                return;

            im.sendPlayerActionC2SPacket(
                    Action.STOP_DESTROY_BLOCK,
                    event.getBlockPos(),
                    event.getDirection()
            );

            return;
        }

        // blatant
        int value = multiplier.get();

        for(int j = 0; j < value; j++)
        {
            im.sendPlayerActionC2SPacket(
                    Action.STOP_DESTROY_BLOCK,
                    event.getBlockPos(),
                    event.getDirection()
            );
        }

        // send packets with delay
        for(int i = 1; i < value; i++)
        {
            tasks.add(new Pair<>(i,
                    () -> {
                        if(mc.player == null) return;

                        for(int j = 0; j < value; j++)
                        {
                            im.sendPlayerActionC2SPacket(
                                    Action.STOP_DESTROY_BLOCK,
                                    event.getBlockPos(),
                                    event.getDirection()
                            );
                        }
                    }));
        }
    }

}
