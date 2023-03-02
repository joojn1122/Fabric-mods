package com.joojn.meteoraddon.mixin;

import com.joojn.meteoraddon.imixin.IGameProfile;
import com.joojn.meteoraddon.modules.PlayerHider;
import com.joojn.meteoraddon.utils.FormattedStringReplacer;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    private MutableText spoofedName;

    @Final @Shadow
    private GameProfile gameProfile;

    @SuppressWarnings("ConstantConditions")
    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addTellClickEvent(Lnet/minecraft/text/MutableText;)Lnet/minecraft/text/MutableText;"), method = "getDisplayName")
    public MutableText getDisplayName(MutableText original){
        if(!PlayerHider.INSTANCE.hideNicks()) return original;

        if(spoofedName == null) {
            // this is stupid, but whatever
            spoofedName = FormattedStringReplacer.replaceText(
                    original,
                    content -> content.replace(
                            gameProfile.getName(),
                            ((IGameProfile) gameProfile).getSpoofedName()
                    )
            );
        }

        return spoofedName;
    }
}
