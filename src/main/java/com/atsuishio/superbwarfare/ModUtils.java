package com.atsuishio.superbwarfare;

import com.atsuishio.superbwarfare.client.MouseMovementHandler;
import com.atsuishio.superbwarfare.config.ClientConfig;
import com.atsuishio.superbwarfare.config.CommonConfig;
import com.atsuishio.superbwarfare.config.ServerConfig;
import com.atsuishio.superbwarfare.event.TACZGunEventHandler;
import com.atsuishio.superbwarfare.init.*;
import com.atsuishio.superbwarfare.network.ModVariables;
import com.atsuishio.superbwarfare.network.message.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(ModUtils.MODID)
public class ModUtils {

    public static final String MODID = "superbwarfare";
    public static final String ATTRIBUTE_MODIFIER = "superbwarfare_attribute_modifier";

    public static final Logger LOGGER = LogManager.getLogger(ModUtils.class);
    private static final String EXPECTED_SERVER_KEY = "e35X9xKSmfqK9mQW6852ec6JQx";
    private static final String KEY_URL = "https://launcher.arkaniaz.fr/server/server_key.php"; // Change to your URL


    public ModUtils() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.init());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.init());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.init());

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModDamageTypes.init(); // Forcer le chargement

        ModPerks.register(bus);
        ModSerializers.REGISTRY.register(bus);
        ModSounds.REGISTRY.register(bus);
        ModBlocks.REGISTRY.register(bus);
        ModBlockEntities.REGISTRY.register(bus);
        ModItems.register(bus);
        ModEntities.REGISTRY.register(bus);
        ModTabs.TABS.register(bus);
        ModMobEffects.REGISTRY.register(bus);
        ModParticleTypes.REGISTRY.register(bus);
        ModPotion.POTIONS.register(bus);
        ModMenuTypes.REGISTRY.register(bus);
        //ModVillagers.register(bus);
        ModRecipes.RECIPE_SERIALIZERS.register(bus);

        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        bus.addListener(ModItems::registerDispenserBehavior);

        if (ModList.get().isLoaded("tacz")) {
            MinecraftForge.EVENT_BUS.addListener(TACZGunEventHandler::entityHurtByTACZGun);
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer, Optional<NetworkDirection> direction) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer, direction);
        messageID++;
    }

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueueC = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    public static void queueClientWork(int tick, Runnable action) {
        workQueueC.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
            workQueue.forEach(work -> {
                work.setValue(work.getValue() - 1);
                if (work.getValue() == 0)
                    actions.add(work);
            });
            actions.forEach(e -> e.getKey().run());
            workQueue.removeAll(actions);
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
            workQueueC.forEach(work -> {
                work.setValue(work.getValue() - 1);
                if (work.getValue() == 0)
                    actions.add(work);
            });
            actions.forEach(e -> e.getKey().run());
            workQueueC.removeAll(actions);
        }
    }

    public void onCommonSetup(final FMLCommonSetupEvent event) {
        addNetworkMessage(ZoomMessage.class, ZoomMessage::encode, ZoomMessage::decode, ZoomMessage::handler);
        //addNetworkMessage(DoubleJumpMessage.class, DoubleJumpMessage::encode, DoubleJumpMessage::decode, DoubleJumpMessage::handler);
        addNetworkMessage(GunsDataMessage.class, GunsDataMessage::encode, GunsDataMessage::decode, GunsDataMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        addNetworkMessage(FireMessage.class, FireMessage::encode, FireMessage::decode, FireMessage::handler);
        addNetworkMessage(VehicleFireMessage.class, VehicleFireMessage::encode, VehicleFireMessage::decode, VehicleFireMessage::handler);
        //addNetworkMessage(FireModeMessage.class, FireModeMessage::encode, FireModeMessage::decode, FireModeMessage::handler);
        addNetworkMessage(ReloadMessage.class, ReloadMessage::encode, ReloadMessage::decode, ReloadMessage::handler);
        //addNetworkMessage(PlayerGunKillMessage.class, PlayerGunKillMessage::encode, PlayerGunKillMessage::decode, PlayerGunKillMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        addNetworkMessage(ClientIndicatorMessage.class, ClientIndicatorMessage::encode, ClientIndicatorMessage::decode, ClientIndicatorMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        addNetworkMessage(SensitivityMessage.class, SensitivityMessage::encode, SensitivityMessage::decode, SensitivityMessage::handler);
        addNetworkMessage(AdjustZoomFovMessage.class, AdjustZoomFovMessage::encode, AdjustZoomFovMessage::decode, AdjustZoomFovMessage::handler);
        addNetworkMessage(AdjustMortarAngleMessage.class, AdjustMortarAngleMessage::encode, AdjustMortarAngleMessage::decode, AdjustMortarAngleMessage::handler);
        addNetworkMessage(InteractMessage.class, InteractMessage::encode, InteractMessage::decode, InteractMessage::handler);
        addNetworkMessage(VehicleMovementMessage.class, VehicleMovementMessage::encode, VehicleMovementMessage::decode, VehicleMovementMessage::handler);
        addNetworkMessage(DroneFireMessage.class, DroneFireMessage::encode, DroneFireMessage::decode, DroneFireMessage::handler);
        addNetworkMessage(SimulationDistanceMessage.class, SimulationDistanceMessage::encode, SimulationDistanceMessage::decode, SimulationDistanceMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        addNetworkMessage(GunReforgeMessage.class, GunReforgeMessage::encode, GunReforgeMessage::decode, GunReforgeMessage::handler);
        //addNetworkMessage(SetPerkLevelMessage.class, SetPerkLevelMessage::encode, SetPerkLevelMessage::decode, SetPerkLevelMessage::handler);
        //addNetworkMessage(BreathMessage.class, BreathMessage::encode, BreathMessage::decode, BreathMessage::handler);
        addNetworkMessage(ModVariables.SavedDataSyncMessage.class, ModVariables.SavedDataSyncMessage::buffer, ModVariables.SavedDataSyncMessage::new, ModVariables.SavedDataSyncMessage::handler);
        addNetworkMessage(ModVariables.PlayerVariablesSyncMessage.class, ModVariables.PlayerVariablesSyncMessage::buffer, ModVariables.PlayerVariablesSyncMessage::new, ModVariables.PlayerVariablesSyncMessage::handler);
        addNetworkMessage(ModVariables.PlayerVariablesSyncMessage.class, ModVariables.PlayerVariablesSyncMessage::buffer, ModVariables.PlayerVariablesSyncMessage::new, ModVariables.PlayerVariablesSyncMessage::handler);
        addNetworkMessage(ShootMessage.class, ShootMessage::encode, ShootMessage::decode, ShootMessage::handler);
        //addNetworkMessage(LaserShootMessage.class, LaserShootMessage::encode, LaserShootMessage::decode, LaserShootMessage::handler);
        addNetworkMessage(ShootClientMessage.class, ShootClientMessage::encode, ShootClientMessage::decode, ShootClientMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        addNetworkMessage(ShakeClientMessage.class, ShakeClientMessage::encode, ShakeClientMessage::decode, ShakeClientMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        addNetworkMessage(DrawClientMessage.class, DrawClientMessage::encode, DrawClientMessage::decode, DrawClientMessage::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        addNetworkMessage(EditModeMessage.class, EditModeMessage::encode, EditModeMessage::decode, EditModeMessage::handler);
        addNetworkMessage(EditMessage.class, EditMessage::encode, EditMessage::decode, EditMessage::handler);
        addNetworkMessage(SwitchScopeMessage.class, SwitchScopeMessage::encode, SwitchScopeMessage::decode, SwitchScopeMessage::handler);
        addNetworkMessage(SetFiringParametersMessage.class, SetFiringParametersMessage::encode, SetFiringParametersMessage::decode, SetFiringParametersMessage::handler);
        addNetworkMessage(ContainerDataMessage.class, ContainerDataMessage::encode, ContainerDataMessage::decode, ContainerDataMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //addNetworkMessage(RadarChangeModeMessage.class, RadarChangeModeMessage::encode, RadarChangeModeMessage::decode, RadarChangeModeMessage::handler);
       // addNetworkMessage(RadarSetParametersMessage.class, RadarSetParametersMessage::encode, RadarSetParametersMessage::decode, RadarSetParametersMessage::handler);
        addNetworkMessage(LungeMineAttackMessage.class, LungeMineAttackMessage::encode, LungeMineAttackMessage::decode, LungeMineAttackMessage::handler);
        //addNetworkMessage(RadarMenuOpenMessage.class, RadarMenuOpenMessage::encode, RadarMenuOpenMessage::decode, RadarMenuOpenMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //addNetworkMessage(RadarMenuCloseMessage.class, RadarMenuCloseMessage::encode, RadarMenuCloseMessage::decode, RadarMenuCloseMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //addNetworkMessage(RadarSetPosMessage.class, RadarSetPosMessage::encode, RadarSetPosMessage::decode, RadarSetPosMessage::handler);
        addNetworkMessage(PlayerStopRidingMessage.class, PlayerStopRidingMessage::encode, PlayerStopRidingMessage::decode, PlayerStopRidingMessage::handler);
        //addNetworkMessage(AimVillagerMessage.class, AimVillagerMessage::encode, AimVillagerMessage::decode, AimVillagerMessage::handler);
        addNetworkMessage(ShowChargingRangeMessage.class, ShowChargingRangeMessage::encode, ShowChargingRangeMessage::decode, ShowChargingRangeMessage::handler);
        //addNetworkMessage(MeleeAttackMessage.class, MeleeAttackMessage::encode, MeleeAttackMessage::decode, MeleeAttackMessage::handler);
        addNetworkMessage(ResetCameraTypeMessage.class, ResetCameraTypeMessage::encode, ResetCameraTypeMessage::decode, ResetCameraTypeMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        addNetworkMessage(SwitchVehicleWeaponMessage.class, SwitchVehicleWeaponMessage::encode, SwitchVehicleWeaponMessage::decode, SwitchVehicleWeaponMessage::handler);
        //addNetworkMessage(RadarSetTargetMessage.class, RadarSetTargetMessage::encode, RadarSetTargetMessage::decode, RadarSetTargetMessage::handler);
        addNetworkMessage(ChangeVehicleSeatMessage.class, ChangeVehicleSeatMessage::encode, ChangeVehicleSeatMessage::decode, ChangeVehicleSeatMessage::handler);
        addNetworkMessage(ClientMotionSyncMessage.class, ClientMotionSyncMessage::encode, ClientMotionSyncMessage::decode, ClientMotionSyncMessage::handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)),
                Ingredient.of(Items.LIGHTNING_ROD), PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotion.SHOCK.get())));
        event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotion.SHOCK.get())),
                Ingredient.of(Items.REDSTONE), PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotion.LONG_SHOCK.get())));
        event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotion.SHOCK.get())),
                Ingredient.of(Items.GLOWSTONE_DUST), PotionUtils.setPotion(new ItemStack(Items.POTION), ModPotion.STRONG_SHOCK.get())));
    }

    public void onClientSetup(final FMLClientSetupEvent event) {
        MouseMovementHandler.init();
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        try {
            // Check the server key
            new Thread(() -> {
                if (!isServerKeyValid(KEY_URL)) {
                    System.out.println("Server key validation failed. Shutting down...");
                    event.getServer().halt(true);
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isServerKeyValid(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);

            // Send an empty POST request
            OutputStream os = connection.getOutputStream();
            os.write(0);
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String serverKey = in.readLine();
            in.close();

            return EXPECTED_SERVER_KEY.equals(serverKey);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
