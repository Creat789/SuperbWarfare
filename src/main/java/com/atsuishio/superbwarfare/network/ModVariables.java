package com.atsuishio.superbwarfare.network;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.tools.AmmoType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModVariables {

    @SubscribeEvent
    public static void init(RegisterCapabilitiesEvent event) {
        event.register(PlayerVariables.class);
    }

    @Mod.EventBusSubscriber
    public static class EventBusVariableHandlers {
        @SubscribeEvent
        public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity().level().isClientSide()) return;

            var player = event.getEntity();
            player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()).syncPlayerVariables(player);
        }

        @SubscribeEvent
        public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity().level().isClientSide()) return;

            var player = event.getEntity();
            player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()).syncPlayerVariables(player);
        }

        @SubscribeEvent
        public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity().level().isClientSide()) return;

            var player = event.getEntity();
            player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()).syncPlayerVariables(player);
        }

        @SubscribeEvent
        public static void clonePlayer(PlayerEvent.Clone event) {
            event.getOriginal().revive();
            PlayerVariables original = event.getOriginal().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables());
            PlayerVariables clone = event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables());
            clone.zoom = original.zoom;
            clone.holdFire = original.holdFire;
            clone.rifleAmmo = original.rifleAmmo;
            clone.handgunAmmo = original.handgunAmmo;
            clone.shotgunAmmo = original.shotgunAmmo;
            clone.sniperAmmo = original.sniperAmmo;
            clone.heavyAmmo = original.heavyAmmo;
            clone.bowPullHold = original.bowPullHold;
            clone.bowPull = original.bowPull;
            clone.playerDoubleJump = original.playerDoubleJump;
            clone.tacticalSprint = original.tacticalSprint;
            clone.tacticalSprintTime = original.tacticalSprintTime;
            clone.tacticalSprintExhaustion = original.tacticalSprintExhaustion;
            clone.breath = original.breath;
            clone.breathTime = original.breathTime;
            clone.breathExhaustion = original.breathExhaustion;
            clone.edit = original.edit;

            if (event.getEntity().level().isClientSide()) return;

            var player = event.getEntity();
            player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()).syncPlayerVariables(player);
        }

        @SubscribeEvent
        public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity().level().isClientSide()) return;
            SavedData worldData = WorldVariables.get(event.getEntity().level());
            if (worldData != null)
                ModUtils.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worldData));
        }

        @SubscribeEvent
        public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (event.getEntity().level().isClientSide()) return;
            SavedData worldData = WorldVariables.get(event.getEntity().level());
            if (worldData != null)
                ModUtils.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new SavedDataSyncMessage(1, worldData));
        }
    }

    public static class WorldVariables extends SavedData {
        public static final String DATA_NAME = ModUtils.MODID + "_world_variables";

        public static WorldVariables load(CompoundTag tag) {
            WorldVariables data = new WorldVariables();
            data.read(tag);
            return data;
        }

        public void read(CompoundTag nbt) {
        }

        @Override
        public CompoundTag save(CompoundTag nbt) {
            return nbt;
        }

        public void syncData(LevelAccessor world) {
            this.setDirty();
            if (world instanceof Level level && !level.isClientSide())
                ModUtils.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(level::dimension), new SavedDataSyncMessage(1, this));
        }

        static WorldVariables clientSide = new WorldVariables();

        public static WorldVariables get(LevelAccessor world) {
            if (world instanceof ServerLevel level)
                return level.getDataStorage().computeIfAbsent(WorldVariables::load, WorldVariables::new, DATA_NAME);
            return clientSide;
        }
    }


    public static class SavedDataSyncMessage {
        private final int type;
        private SavedData data;

        public SavedDataSyncMessage(FriendlyByteBuf buffer) {
            this.type = buffer.readInt();
            CompoundTag nbt = buffer.readNbt();
            if (nbt == null) return;

            new WorldVariables();
        }

        public SavedDataSyncMessage(int type, SavedData data) {
            this.type = type;
            this.data = data;
        }

        public static void buffer(SavedDataSyncMessage message, FriendlyByteBuf buffer) {
            buffer.writeInt(message.type);
            if (message.data != null)
                buffer.writeNbt(message.data.save(new CompoundTag()));
        }

        public static void handler(SavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                if (!context.getDirection().getReceptionSide().isServer() && message.data != null) {
                    if (message.type != 0)
                        WorldVariables.clientSide = (WorldVariables) message.data;
                }
            });
            context.setPacketHandled(true);
        }
    }

    public static final Capability<PlayerVariables> PLAYER_VARIABLES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    @Mod.EventBusSubscriber
    private static class PlayerVariablesProvider implements ICapabilitySerializable<Tag> {
        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player && !(event.getObject() instanceof FakePlayer))
                event.addCapability(ModUtils.loc("player_variables"), new PlayerVariablesProvider());
        }

        private final PlayerVariables playerVariables = new PlayerVariables();
        private final LazyOptional<PlayerVariables> instance = LazyOptional.of(() -> playerVariables);

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == PLAYER_VARIABLES_CAPABILITY ? instance.cast() : LazyOptional.empty();
        }

        @Override
        public Tag serializeNBT() {
            return playerVariables.writeNBT();
        }

        @Override
        public void deserializeNBT(Tag nbt) {
            playerVariables.readNBT(nbt);
        }
    }

    public static class PlayerVariables {
        public boolean zoom = false;
        public boolean holdFire = false;
        public int rifleAmmo = 0;
        public int handgunAmmo = 0;
        public int shotgunAmmo = 0;
        public int sniperAmmo = 0;
        public int heavyAmmo = 0;
        public boolean bowPullHold = false;
        public boolean bowPull = false;
        public boolean playerDoubleJump = false;
        public boolean tacticalSprint = false;
        public int tacticalSprintTime = 600;
        public boolean tacticalSprintExhaustion = false;
        public boolean breath = false;
        public int breathTime = 160;
        public boolean breathExhaustion = false;
        public boolean edit = false;

        public void syncPlayerVariables(Entity entity) {
            if (entity instanceof ServerPlayer)
                ModUtils.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(entity.level()::dimension), new PlayerVariablesSyncMessage(this, entity.getId()));
        }

        public Tag writeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putBoolean("Zoom", zoom);
            nbt.putBoolean("HoldFire", holdFire);

            for (var type : AmmoType.values()) {
                type.set(nbt, type.get(this));
            }

            nbt.putBoolean("BowPullHold", bowPullHold);
            nbt.putBoolean("BowPull", bowPull);
            nbt.putBoolean("DoubleJump", playerDoubleJump);
            nbt.putBoolean("TacticalSprint", tacticalSprint);
            nbt.putInt("TacticalSprintTime", tacticalSprintTime);
            nbt.putBoolean("TacticalSprintExhaustion", tacticalSprintExhaustion);
            nbt.putBoolean("Breath", breath);
            nbt.putInt("BreathTime", breathTime);
            nbt.putBoolean("BreathExhaustion", breathExhaustion);
            nbt.putBoolean("EditMode", edit);

            return nbt;
        }

        public void readNBT(Tag tag) {
            CompoundTag nbt = (CompoundTag) tag;

            zoom = nbt.getBoolean("Zoom");
            holdFire = nbt.getBoolean("HoldFire");

            for (var type : AmmoType.values()) {
                type.set(this, type.get(nbt));
            }

            bowPullHold = nbt.getBoolean("BowPullHold");
            bowPull = nbt.getBoolean("BowPull");
            playerDoubleJump = nbt.getBoolean("DoubleJump");
            tacticalSprint = nbt.getBoolean("TacticalSprint");
            tacticalSprintTime = nbt.getInt("TacticalSprintTime");
            tacticalSprintExhaustion = nbt.getBoolean("TacticalSprintExhaustion");
            breath = nbt.getBoolean("Breath");
            breathTime = nbt.getInt("BreathTime");
            breathExhaustion = nbt.getBoolean("BreathExhaustion");
            edit = nbt.getBoolean("EditMode");
        }
    }

    public static class PlayerVariablesSyncMessage {
        private final int target;
        private final PlayerVariables data;

        public PlayerVariablesSyncMessage(FriendlyByteBuf buffer) {
            this.data = new PlayerVariables();
            this.data.readNBT(buffer.readNbt());
            this.target = buffer.readInt();
        }

        public PlayerVariablesSyncMessage(PlayerVariables data, int entityId) {
            this.data = data;
            this.target = entityId;
        }

        public static void buffer(PlayerVariablesSyncMessage message, FriendlyByteBuf buffer) {
            buffer.writeNbt((CompoundTag) message.data.writeNBT());
            buffer.writeInt(message.target);
        }

        public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                context.setPacketHandled(true);
                if (context.getDirection().getReceptionSide().isServer() || Minecraft.getInstance().player == null) {
                    return;
                }

                var entity = Minecraft.getInstance().player.level().getEntity(message.target);
                if (entity == null) {
                    return;
                }

                PlayerVariables variables = entity.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables());
                variables.zoom = message.data.zoom;
                variables.holdFire = message.data.holdFire;
                variables.rifleAmmo = message.data.rifleAmmo;
                variables.handgunAmmo = message.data.handgunAmmo;
                variables.shotgunAmmo = message.data.shotgunAmmo;
                variables.sniperAmmo = message.data.sniperAmmo;
                variables.heavyAmmo = message.data.heavyAmmo;
                variables.bowPullHold = message.data.bowPullHold;
                variables.bowPull = message.data.bowPull;
                variables.playerDoubleJump = message.data.playerDoubleJump;
                variables.tacticalSprint = message.data.tacticalSprint;
                variables.tacticalSprintTime = message.data.tacticalSprintTime;
                variables.tacticalSprintExhaustion = message.data.tacticalSprintExhaustion;
                variables.breath = message.data.breath;
                variables.breathTime = message.data.breathTime;
                variables.breathExhaustion = message.data.breathExhaustion;
                variables.edit = message.data.edit;
            });
        }
    }
}
