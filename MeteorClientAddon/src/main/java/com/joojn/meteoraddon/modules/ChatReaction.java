package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import com.joojn.meteoraddon.chat.MessageFilter;
import com.joojn.meteoraddon.hud.CodeViewer;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.Starscript;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.value.Value;
import meteordevelopment.starscript.value.ValueMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import org.lwjgl.glfw.GLFW;

public class ChatReaction extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Pair<Integer, String> pair = new Pair<>(0, null);

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay in ticks.")
            .defaultValue(5)
            .sliderRange(0, 200)
            .range(0, 200)
            .build()
    );

    private final Setting<MessageFilter> macroType = sgGeneral.add(new EnumSetting.Builder<MessageFilter>()
            .name("filter-type")
            .description("The filter type of message.")
            .defaultValue(MessageFilter.EXACT)
            .build()
    );

    private final Setting<String> macroFilter = sgGeneral.add(new StringSetting.Builder()
            .name("filter")
            .description("Filter of the message.")
            .defaultValue("Exact message match")
            .build()
    );

    private final Setting<String> macroReaction = sgGeneral.add(new StringSetting.Builder()
            .name("reaction")
            .description("Message to be sent.")
            .defaultValue("Hello world!")
            .onChanged(s ->
                    reactionScript = Compiler.compile(Parser.parse(s))
            )
            .renderer(StarscriptTextBoxRenderer.class)
            .build()
    );

    private Script reactionScript = Compiler.compile(Parser.parse(macroReaction.get()));

    public ChatReaction()
    {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "chat-reaction",
                "Automatically reacts to messages in chat"
        );

        MeteorStarscript.ss.set("message", new ValueMap()
                .set("hover", Value.null_())
                .set("click", Value.null_())
                .set("text" , Value.null_())
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
                if(mc.player != null)
                {
                    ChatUtils.sendPlayerMsg(pair.getRight());
                }

                pair.setRight(null);
            }
            else
            {
                pair.setLeft(time);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler
    public void onChatMessage(ReceiveMessageEvent event)
    {
        if(mc.player == null) return;

        String message = event.getMessage().getString();

        if(!macroType.get().matcher.apply(message, macroFilter.get())) return;

        Starscript ss = MeteorStarscript.ss;
        ValueMap messageMap = new ValueMap();
        ss.set("message", messageMap);

        HoverEvent hoverEvent = event.getMessage().getStyle().getHoverEvent();

        if(hoverEvent == null) {
            messageMap.set("hover", Value::null_);
        }
        else {
            HoverEvent.Action<?> action = hoverEvent.getAction();

            if(action == HoverEvent.Action.SHOW_TEXT) {
                messageMap.set("hover", Value.string(hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT).getString()));
            }
            else if(action == HoverEvent.Action.SHOW_ENTITY) {
                messageMap.set("hover", Value.string(hoverEvent.getValue(HoverEvent.Action.SHOW_ENTITY).name.getString()));
            }
            else if(action == HoverEvent.Action.SHOW_ITEM) {
                messageMap.set("hover", Value.string(hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM).asStack().getName().getString()));
            }
        }

        ClickEvent clickEvent = event.getMessage().getStyle().getClickEvent();

        if(clickEvent == null) {
            messageMap.set("click", Value::null_);
        }
        else {
            messageMap.set("click", Value.string(clickEvent.getValue()));
        }

        messageMap.set("text", Value.string(message));

        String result = MeteorStarscript.run(reactionScript);

        MeteorClientUtils.LOGGER.info("Sending chat reaction: " + result);

        if(delay.get() == 0)
        {
            ChatUtils.sendPlayerMsg(result);
        }
        else
        {
            pair.setLeft(delay.get());
            pair.setRight(result);
        }
    }

    @EventHandler
    public void onKey(KeyEvent event)
    {
        if(event.action != KeyAction.Press) return;

        if(event.key == GLFW.GLFW_KEY_P)
        {
            mc.setScreen(CodeViewer.INSTANCE);
        }
    }
}
