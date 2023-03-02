package com.joojn.meteoraddon.mixin;

import com.joojn.meteoraddon.imixin.IGameProfile;
import com.joojn.meteoraddon.modules.PlayerHider;
import com.mojang.authlib.GameProfile;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = GameProfile.class, remap = false)
public class GameProfileMixin implements IGameProfile {

    private String spoofedName;

    @Override
    public String getSpoofedName() {
        if(spoofedName == null)
            spoofedName = PlayerHider.INSTANCE.generateName();

        return spoofedName;
    }
}
