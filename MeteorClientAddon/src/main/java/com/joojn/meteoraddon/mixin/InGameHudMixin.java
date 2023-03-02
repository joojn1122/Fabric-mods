package com.joojn.meteoraddon.mixin;

import com.joojn.meteoraddon.modules.Sidebar;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(at = @At("HEAD"), method = "renderScoreboardSidebar", cancellable = true)
    public void renderScoreboardSidebar(
            MatrixStack matrices,
            ScoreboardObjective objective,
            CallbackInfo ci
    ) {
        if(Sidebar.INSTANCE.hideSidebar()) ci.cancel();
    }

    // for some reason MCDev plugin is giving error about changing class type, just ignore it
    // Suppressing ALL, cuz I don't know the specific name
    @SuppressWarnings("ALL")
    @ModifyVariable(at = @At(value = "STORE"), method = "renderScoreboardSidebar", ordinal = 0)
    public String hideRedNumbers(String value) {
        if(!Sidebar.INSTANCE.hideRedNumbers()) return value;

        return "";
    }
}
