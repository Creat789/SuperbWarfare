package com.atsuishio.superbwarfare.network.message;

import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.ModVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DoubleJumpMessage {

    private final boolean canDoubleJump;

    public DoubleJumpMessage(boolean canDoubleJump) {
        this.canDoubleJump = canDoubleJump;
    }

    public static DoubleJumpMessage decode(FriendlyByteBuf buffer) {
        return new DoubleJumpMessage(buffer.readBoolean());
    }

    public static void encode(DoubleJumpMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.canDoubleJump);
    }

    public static void handler(DoubleJumpMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if (player != null) {
                Level level = player.level();
                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();

                level.playSound(null, BlockPos.containing(x, y, z), ModSounds.DOUBLE_JUMP.get(), SoundSource.BLOCKS, 1, 1);

                player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                    capability.syncPlayerVariables(player);
                });
            }
        });
        context.setPacketHandled(true);
    }
}
