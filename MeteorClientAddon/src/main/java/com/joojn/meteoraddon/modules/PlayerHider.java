package com.joojn.meteoraddon.modules;

import com.joojn.meteoraddon.MeteorClientUtils;
import com.joojn.meteoraddon.utils.FormattedStringReplacer;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class PlayerHider extends Module {

    public static final PlayerHider INSTANCE = new PlayerHider();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> hideNicks = sgGeneral.add(new BoolSetting.Builder()
            .name("hide-nicks")
            .description("Hides nicks of players.")
            .defaultValue(true)
            .build()
    );

    private final Setting<String> nickPattern = sgGeneral.add(new StringSetting.Builder()
            .name("nick-pattern")
            .description("Pattern of nick.")
            .defaultValue("Player")
            .visible(hideNicks::get)
            .build()
    );

    private final Setting<Boolean> hideSkins = sgGeneral.add(new BoolSetting.Builder()
            .name("hide-skins")
            .description("Hides skins of players.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> hideCapes = sgGeneral.add(new BoolSetting.Builder()
            .name("hide-capes")
            .description("Hides capes of players.")
            .defaultValue(false)
            .build()
    );

    public PlayerHider() {
        super(
                MeteorClientUtils.FUN_CATEGORY,
                "player-hider",
                "Hides nicks and skins of players. Entity needs to be reloaded to apply nick changes."
        );
    }

    public boolean hideNicks() {
        return isActive() && hideNicks.get();
    }

    public boolean hideSkins() {
        return isActive() && hideSkins.get();
    }

    public boolean hideCapes() {
        return isActive() && hideCapes.get();
    }

    public String generateName() {
        return this.nickPattern.get() + Math.round(Math.random() * 1000);
    }

    @EventHandler
    public void onChatMessage(ReceiveMessageEvent event) {
        if(!hideNicks() || mc.world == null) return;

        List<AbstractClientPlayerEntity> players = mc.world.getPlayers();

        Text newMessage = FormattedStringReplacer.replaceText(
                event.getMessage(),
                content -> {
                    for (AbstractClientPlayerEntity player : players) {
                        content = content.replace(
                                player.getName().getString(),
                                generateName()
                        );
                    }

                    return content;
                }
        );

        event.setMessage(newMessage);
    }
}
