package com.atsuishio.superbwarfare.client;

import com.atsuishio.superbwarfare.client.renderer.block.ChargingStationBlockEntityRenderer;
import com.atsuishio.superbwarfare.client.renderer.block.ContainerBlockEntityRenderer;
import com.atsuishio.superbwarfare.client.renderer.block.FuMO25BlockEntityRenderer;
import com.atsuishio.superbwarfare.client.tooltip.*;
import com.atsuishio.superbwarfare.client.tooltip.component.*;
import com.atsuishio.superbwarfare.init.ModBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRenderHandler {

    @SubscribeEvent
    public static void registerTooltip(RegisterClientTooltipComponentFactoriesEvent event) {
        //event.register(GunImageComponent.class, ClientGunImageTooltip::new);
        //event.register(ShotgunImageComponent.class, ClientShotgunImageTooltip::new);
        //event.register(BocekImageComponent.class, ClientBocekImageTooltip::new);
        event.register(EnergyImageComponent.class, ClientEnergyImageTooltip::new);
        event.register(CellImageComponent.class, ClientCellImageTooltip::new);
       // event.register(SentinelImageComponent.class, ClientSentinelImageTooltip::new);
       // event.register(LauncherImageComponent.class, ClientLauncherImageTooltip::new);
        //event.register(SecondaryCataclysmImageComponent.class, ClientSecondaryCataclysmImageTooltip::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CONTAINER.get(), context -> new ContainerBlockEntityRenderer());
        /*event.registerBlockEntityRenderer(ModBlockEntities.FUMO_25.get(), context -> new FuMO25BlockEntityRenderer());
        event.registerBlockEntityRenderer(ModBlockEntities.CHARGING_STATION.get(), context -> new ChargingStationBlockEntityRenderer());*/
    }

}
