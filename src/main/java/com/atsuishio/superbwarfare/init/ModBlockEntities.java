package com.atsuishio.superbwarfare.init;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.block.entity.ChargingStationBlockEntity;
import com.atsuishio.superbwarfare.block.entity.ContainerBlockEntity;
import com.atsuishio.superbwarfare.block.entity.CreativeChargingStationBlockEntity;
import com.atsuishio.superbwarfare.block.entity.FuMO25BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ModUtils.MODID);

    public static final RegistryObject<BlockEntityType<ContainerBlockEntity>> CONTAINER = REGISTRY.register("container",
            () -> BlockEntityType.Builder.of(ContainerBlockEntity::new, ModBlocks.CONTAINER.get()).build(null));
   /* public static final RegistryObject<BlockEntityType<ChargingStationBlockEntity>> CHARGING_STATION = REGISTRY.register("charging_station",
            () -> BlockEntityType.Builder.of(ChargingStationBlockEntity::new, ModBlocks.CHARGING_STATION.get()).build(null));
    public static final RegistryObject<BlockEntityType<CreativeChargingStationBlockEntity>> CREATIVE_CHARGING_STATION = REGISTRY.register("creative_charging_station",
            () -> BlockEntityType.Builder.of(CreativeChargingStationBlockEntity::new, ModBlocks.CREATIVE_CHARGING_STATION.get()).build(null));
    public static final RegistryObject<BlockEntityType<FuMO25BlockEntity>> FUMO_25 = REGISTRY.register("fumo_25",
            () -> BlockEntityType.Builder.of(FuMO25BlockEntity::new, ModBlocks.FUMO_25.get()).build(null));*/

}
