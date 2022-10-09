package com.joojn.guesser.mixin;

import com.joojn.guesser.guess.GuessTheBuild;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Objects;
import java.util.regex.Pattern;

@Mixin({MinecraftClient.class})
public class MinecraftClientMixin {

    final Pattern guessTheBuildPattern = Pattern.compile(
            "(?i)GUESS.*THE.*BUILD",
            Pattern.MULTILINE
    );

    String lastTitle = "";

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci)
    {
        if(MinecraftClient.getInstance().world != null)
        {
            Collection<ScoreboardObjective> objectives = MinecraftClient.getInstance().world.getScoreboard().getObjectives();

            if(objectives.size() > 0)
            {
                ScoreboardObjective o = null;

                for(ScoreboardObjective o_ : objectives)
                {
                    o = o_;
                    break;
                }

                String title = o.getDisplayName().getString();
                if(Objects.equals(lastTitle, title)) return;

                lastTitle = title;

                GuessTheBuild.inGame = guessTheBuildPattern.matcher(title).find();
            }
        }
    }

}
