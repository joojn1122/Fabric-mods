package com.joojn.meteoraddon.mixin;

import com.joojn.meteoraddon.imixin.IGameProfile;
import com.joojn.meteoraddon.modules.PlayerHider;
import com.joojn.meteoraddon.utils.FormattedStringReplacer;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {

    @Shadow @Final
    private GameProfile profile;

    @Inject(at = @At("HEAD"), method = "getSkinTexture", cancellable = true)
    public void getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        if(PlayerHider.INSTANCE.hideSkins())
        {
            cir.setReturnValue(DefaultSkinHelper.getTexture(
                    profile.getId()
            ));
        }
    }

    @Shadow
    private Text displayName;

    private MutableText spoofedName;

    @Inject(at = @At("HEAD"), method = "getDisplayName", cancellable = true)
    public void getDisplayName(CallbackInfoReturnable<Text> cir) {
        if(!PlayerHider.INSTANCE.hideNicks() || displayName == null) return;

        if(spoofedName == null) {
            // this is stupid, but whatever
            spoofedName = FormattedStringReplacer.replaceText(
                    (MutableText) displayName,
                    content -> content.replace(
                            profile.getName(),
                            ((IGameProfile) profile).getSpoofedName()
                    )
            );
        }

        cir.setReturnValue(spoofedName);
    }
}
