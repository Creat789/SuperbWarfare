package com.atsuishio.superbwarfare.client.model.entity;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.ArmedVehicleEntity;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import com.atsuishio.superbwarfare.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class ProjectileEntityModel extends GeoModel<ProjectileEntity> {

    @Override
    public ResourceLocation getAnimationResource(ProjectileEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(ProjectileEntity entity) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return ModUtils.loc("geo/projectile_entity2.geo.json");
        }

        if ((ClientEventHandler.zoom &&
                player.getVehicle() instanceof ArmedVehicleEntity)) {
            return ModUtils.loc("geo/projectile_entity.geo.json");
        } else {
            return ModUtils.loc("geo/projectile_entity2.geo.json");
        }
    }

    @Override
    public ResourceLocation getTextureResource(ProjectileEntity entity) {
        return ModUtils.loc("textures/entity/empty.png");
    }

    @Override
    public void setCustomAnimations(ProjectileEntity animatable, long instanceId, AnimationState animationState) {
        CoreGeoBone bone = getAnimationProcessor().getBone("bone");
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            bone.setHidden(animatable.position().distanceTo(player.position()) < 3 || animatable.tickCount < 1);
        }
    }
}
