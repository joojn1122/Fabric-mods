package com.joojn.guesser.mixin;

import com.joojn.guesser.GuesserMod;
import com.joojn.guesser.guess.GuessTheBuild;
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

    final Pattern guessTheBuildPattern = Pattern.compile(
            "(?i)(?<=The theme is ).*_.*",
            Pattern.MULTILINE
    );

    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    private void sendMessage(Text message, boolean tinted, CallbackInfo info)
    {
        if(GuessTheBuild.inGame)
        {
            Matcher matcher = guessTheBuildPattern.matcher(GuesserMod.getPlainText(message));

            GuessTheBuild.setTheme(matcher.find() ? matcher.group(0) : null);
        }
    }
}