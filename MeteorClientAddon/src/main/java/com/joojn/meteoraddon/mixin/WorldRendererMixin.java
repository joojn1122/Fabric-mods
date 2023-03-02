package com.joojn.meteoraddon.mixin;

import com.joojn.meteoraddon.modules.ClientOffset;
import com.joojn.meteoraddon.modules.Misplace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @ModifyArgs(method = "renderEntity", at = @At(
            value="INVOKE",
            target="Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
    ))
    private void modifyRenderArgs(
            Args args
    )
    {
        Entity entity = args.get(0);
        if(entity instanceof PlayerEntity player)
        {
            if(player == MinecraftClient.getInstance().player) return;

            double x = args.get(1);
            double z = args.get(3);

            double[] xz = Misplace.INSTANCE.modifyPosition(player, x, z);

            args.set(1, xz[0]);
            args.set(3, xz[1]);
        }
    }

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 0)
    private Vec3d modifyCameraPos(Vec3d vec3d)
    {
        return ClientOffset.INSTANCE.getOffset(vec3d);
    }
}
