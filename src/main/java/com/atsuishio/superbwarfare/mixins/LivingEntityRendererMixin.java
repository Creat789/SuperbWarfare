package com.atsuishio.superbwarfare.mixins;

import com.atsuishio.superbwarfare.entity.vehicle.Ah6Entity;
import com.atsuishio.superbwarfare.entity.vehicle.Tom6Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// From Immersive_Aircraft
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "setupRotations", at = @At("TAIL"))
    public void render(T entity, PoseStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
        if (entity.getRootVehicle() != entity && entity.getRootVehicle() instanceof Ah6Entity ah6Entity) {
            if (entity == ah6Entity.getNthEntity(2)) {
                matrices.mulPose(Axis.XP.rotationDegrees(-ah6Entity.getRoll(tickDelta)));
                matrices.mulPose(Axis.ZP.rotationDegrees(ah6Entity.getViewXRot(tickDelta)));
            } else if (entity == ah6Entity.getNthEntity(3)) {
                matrices.mulPose(Axis.XP.rotationDegrees(ah6Entity.getRoll(tickDelta)));
                matrices.mulPose(Axis.ZP.rotationDegrees(-ah6Entity.getViewXRot(tickDelta)));
            } else {
                matrices.mulPose(Axis.XP.rotationDegrees(-ah6Entity.getViewXRot(tickDelta)));
                matrices.mulPose(Axis.ZP.rotationDegrees(-ah6Entity.getRoll(tickDelta)));
            }
        }
        if (entity.getRootVehicle() != entity && entity.getRootVehicle() instanceof Tom6Entity tom6Entity) {
            matrices.mulPose(Axis.XP.rotationDegrees(-tom6Entity.getViewXRot(tickDelta)));
            matrices.mulPose(Axis.ZP.rotationDegrees(-tom6Entity.getRoll(tickDelta)));
        }
    }
}
