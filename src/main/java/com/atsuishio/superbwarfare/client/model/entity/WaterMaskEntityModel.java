package com.atsuishio.superbwarfare.client.model.entity;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.entity.WaterMaskEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WaterMaskEntityModel extends GeoModel<WaterMaskEntity> {

    @Override
    public ResourceLocation getAnimationResource(WaterMaskEntity entity) {
        return null;
    }

    @Override
    public ResourceLocation getModelResource(WaterMaskEntity entity) {
        return ModUtils.loc("geo/water_mask_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WaterMaskEntity entity) {
        return ModUtils.loc("textures/entity/mortar_shell.png");
    }
}
