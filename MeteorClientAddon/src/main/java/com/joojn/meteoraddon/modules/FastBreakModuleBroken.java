package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import com.joojn.meteoraddon.imixin.IClientInteractionManager;
import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class FastBreakModuleBroken extends Module {

    // used for packet delays
    private final ArrayList<Pair<Integer, Runnable>> tasks = new ArrayList<>();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> multiplier = sgGeneral.add(new IntSetting.Builder()
            .name("multiplier")
            .description("The multiplier for the breaking speed.")
            .defaultValue(1)
            .range(1, 100)
            .sliderRange(1, 100)
            .build()
    );

    private final Setting<Boolean> shouldRender = sgGeneral.add(new BoolSetting.Builder()
            .name("render-target")
            .description("Renders the target block.")
            .defaultValue(true)
            .build()
    );

    private final Setting<ShapeMode> shapeModeBox = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
            .name("box-mode")
            .description("How the shape for the bounding box is rendered.")
            .defaultValue(ShapeMode.Both)
            .visible(shouldRender::get)
            .build()
    );

    private final Setting<SettingColor> sideColorBox = sgGeneral.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color of the bounding box.")
            .defaultValue(new SettingColor(16,106,144, 100))
            .visible(shouldRender::get)
            .build()
    );

    private final Setting<SettingColor> lineColorBox = sgGeneral.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color of the bounding box.")
            .defaultValue(new SettingColor(16,106,144, 255))
            .visible(shouldRender::get)
            .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
            .name("swing-tool")
            .description("Swing tool")
            .defaultValue(true)
            .build()
    );

    public FastBreakModuleBroken() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "fast-break",
                "Breaks blocks faster. (Skidded from Wurst)"
        );
    }

    private boolean mining = false;
    private boolean init = false;
    private BlockHitResult result = null;

    @EventHandler
    public void onRightClick(InteractBlockEvent event)
    {
        if(event.hand != Hand.MAIN_HAND) return;

        mining = !mining;
        result = event.result;
        init = true;
    }

    @Override
    public void onDeactivate()
    {
        mining = false;
        init = false;
        result = null;
    }

    @EventHandler
    private void onRender(Render3DEvent event)
    {
        if(!(shouldRender.get() && mining)) return;

        event.renderer.box(
                result.getBlockPos(),
                sideColorBox.get(),
                lineColorBox.get(),
                shapeModeBox.get(),
                0
       );
    }

    @EventHandler
    public void onTick(TickEvent.Pre event)
    {
        if(!mining) return;

        IClientInteractionManager im = IClientInteractionManager.get();
        im.setBlockBreakingCooldown(0);

        BlockPos pos = result.getBlockPos();
        BlockState state = mc.world.getBlockState(pos);

        if(!BlockUtils.canBreak(pos))
            return;

        if(swing.get())
            BlockUtils.breakBlock(pos, false);

        if(
                state == null ||
                        mc.player == null ||
                        !pos.isWithinDistance(mc.player.getPos(), 5.0D)
        )
        {
            mining = false;
            return;
        }

        if(init)
        {
            init = false;
            im.sendPlayerActionC2SPacket(
                    Action.START_DESTROY_BLOCK,
                    result.getBlockPos(),
                    result.getSide()
            );


//            mc.interactionManager.updateBlockBreakingProgress(
//                    result.getBlockPos(),
//                    result.getSide()
//            );
        }
        else
        {
            for(int i = 0; i < multiplier.get(); i++)
            {
                im.sendPlayerActionC2SPacket(
                        Action.STOP_DESTROY_BLOCK,
                        pos,
                        result.getSide()
                );
            }
        }
    }
}
