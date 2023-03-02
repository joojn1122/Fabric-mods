package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public class BeeChatReaction extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Pair<Integer, Text> pair = new Pair<>(0, null);

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay in ticks.")
            .defaultValue(5)
            .sliderRange(0, 100)
            .range(0, 100)
            .build()
    );

    public BeeChatReaction()
    {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "bee-chat-reaction",
                "Automatically reacts to solve message in chat, works only on Bee-Games.cz."
        );
    }

    @EventHandler
    public void onTick(TickEvent.Pre event)
    {
        if(pair.getRight() != null)
        {
            int time = pair.getLeft() - 1;

            if(time <= 0)
            {
                pair.setRight(null);

                if(mc.player != null)
                {
                    mc.player.networkHandler.sendChatMessage(
                            pair.getRight().getString()
                    );
                }
            }
            else
            {
                pair.setLeft(time);
            }
        }
    }

    @EventHandler
    public void onChatMessage(ReceiveMessageEvent event)
    {
        if(mc.player == null) return;

        String message = event.getMessage().getString();

        if(!(message.startsWith("BeeReakce")
                && message.contains("Najeď kurzorem na zprávu a slovo opiš!"))) return;

        HoverEvent hoverEvent = event.getMessage().getStyle().getHoverEvent();
        if(hoverEvent == null) return;

        Text value = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
        if(value == null) return;

        MeteorClientUtils.LOGGER.info("Sending chat reaction: " + value.getString());

        if(delay.get() == 0)
        {
            mc.player.networkHandler.sendChatMessage(
                    value.getString()
            );
        }
        else
        {
            pair.setLeft(delay.get());
            pair.setRight(value);
        }
    }

}
