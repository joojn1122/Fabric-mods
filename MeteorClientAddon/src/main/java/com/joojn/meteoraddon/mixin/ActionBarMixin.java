package com.joojn.meteoraddon.mixin;

import com.joojn.meteoraddon.modules.HypixelGuessTheBuild;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(InGameHud.class)
public class ActionBarMixin {

    private final Pattern guessTheBuildPattern = Pattern.compile(
            "The theme is (.*_.*)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
    );

    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    private void setOverlayMessage(Text message, boolean tinted, CallbackInfo info)
    {
        if(HypixelGuessTheBuild.inGame)
        {
            Matcher matcher = guessTheBuildPattern.matcher(
                    message.getString().replaceAll("§.", "")
            );

            HypixelGuessTheBuild.setTheme(matcher.find() ? matcher.group(1) : null);
        }
    }

}
