package com.atsuishio.superbwarfare.network.message;

import com.atsuishio.superbwarfare.network.ModVariables;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BreathMessage {

    private final boolean type;

    public BreathMessage(boolean type) {
        this.type = type;
    }

    public static BreathMessage decode(FriendlyByteBuf buffer) {
        return new BreathMessage(buffer.readBoolean());
    }

    public static void encode(BreathMessage message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.type);
    }

    public static void handler(BreathMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
        });
        context.setPacketHandled(true);
    }
}
