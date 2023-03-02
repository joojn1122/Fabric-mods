package com.joojn.meteoraddon.mixin;

import com.joojn.meteoraddon.modules.PlayerHider;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "getModel", cancellable = true)
    public void getModel(CallbackInfoReturnable<String> cir) {
        if(PlayerHider.INSTANCE.hideSkins())
        {
            cir.setReturnValue(DefaultSkinHelper.getModel(
                    ((AbstractClientPlayerEntity) (Object) this).getUuid()
            ));
        }
    }

    @Inject(at = @At("HEAD"), method = "getSkinTexture", cancellable = true)
    public void getSkinTexture(CallbackInfoReturnable<Identifier> cir) {
        if(PlayerHider.INSTANCE.hideSkins())
        {
            cir.setReturnValue(DefaultSkinHelper.getTexture(
                    ((AbstractClientPlayerEntity) (Object) this).getUuid()
            ));
        }
    }

    @Inject(at = @At("HEAD"), method = "getCapeTexture", cancellable = true)
    public void getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        if(PlayerHider.INSTANCE.hideCapes())
        {
            cir.setReturnValue(null);
        }
    }
}
