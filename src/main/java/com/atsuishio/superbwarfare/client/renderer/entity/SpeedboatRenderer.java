package com.atsuishio.superbwarfare.client.renderer.entity;

import com.atsuishio.superbwarfare.client.layer.SpeedBoatHeatLayer;
import com.atsuishio.superbwarfare.client.layer.SpeedBoatLayer;
import com.atsuishio.superbwarfare.client.layer.SpeedBoatPowerLayer;
import com.atsuishio.superbwarfare.client.model.entity.SpeedboatModel;
import com.atsuishio.superbwarfare.entity.vehicle.SpeedboatEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static com.atsuishio.superbwarfare.entity.vehicle.base.MobileVehicleEntity.YAW;

public class SpeedboatRenderer extends GeoEntityRenderer<SpeedboatEntity> {

    public SpeedboatRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SpeedboatModel());
        this.addRenderLayer(new SpeedBoatLayer(this));
        this.addRenderLayer(new SpeedBoatPowerLayer(this));
        this.addRenderLayer(new SpeedBoatHeatLayer(this));
    }

    @Override
    public RenderType getRenderType(SpeedboatEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void preRender(PoseStack poseStack, SpeedboatEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
                          float blue, float alpha) {
        float scale = 1f;
        this.scaleHeight = scale;
        this.scaleWidth = scale;
        super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(SpeedboatEntity entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        poseStack.popPose();
    }

    @Override
    public void renderRecursively(PoseStack poseStack, SpeedboatEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        String name = bone.getName();
        if (name.equals("root")) {

            Player player = Minecraft.getInstance().player;
            bone.setHidden(ClientEventHandler.zoomVehicle && animatable.getFirstPassenger() == player);

            float a = animatable.getEntityData().get(YAW);
            float r = (Mth.abs(a) - 90f) / 90f;

            bone.setPosZ(r * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * 0.125f);
            bone.setRotX(r * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * Mth.DEG_TO_RAD * 0.5f);

            float r2;

            if (Mth.abs(a) <= 90f) {
                r2 = a / 90f;
            } else {
                if (a < 0) {
                    r2 = - (180f + a) / 90f;
                } else {
                    r2 = (180f - a) / 90f;
                }
            }

            bone.setPosX(r2 * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * 0.125f);
            bone.setRotZ(r2 * Mth.lerp(partialTick, (float) animatable.recoilShakeO, (float) animatable.getRecoilShake()) * Mth.DEG_TO_RAD * 0.75f);
        }

        if (name.equals("Rotor")) {
            bone.setRotZ(Mth.lerp(partialTick, animatable.rotorRotO, animatable.getRotorRot()));
        }
        if (name.equals("duo")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.rudderRotO, animatable.getRudderRot()));
        }
        if (name.equals("paota")) {
            bone.setRotY(Mth.lerp(partialTick, animatable.turretYRotO, animatable.getTurretYRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("gun")) {
            bone.setRotX(-Mth.lerp(partialTick, animatable.turretXRotO, animatable.getTurretXRot()) * Mth.DEG_TO_RAD);
        }
        if (name.equals("flare")) {
            bone.setRotZ((float) (0.5 * (Math.random() - 0.5)));
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected float getDeathMaxRotation(SpeedboatEntity entityLivingBaseIn) {
        return 0.0F;
    }
}
