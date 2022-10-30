package me.joojn.nickchanger.mixin;

import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;

@Mixin({Session.class})
public class SessionMixin {

    @Mutable
    private String username;

}
