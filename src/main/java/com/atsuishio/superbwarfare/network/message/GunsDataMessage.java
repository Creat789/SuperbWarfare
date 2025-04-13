package com.atsuishio.superbwarfare.network.message;

import com.atsuishio.superbwarfare.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.function.Supplier;

public class GunsDataMessage {

    public final HashMap<String, HashMap<String, Double>> gunsData;

    public GunsDataMessage(HashMap<String, HashMap<String, Double>> gunsData) {
        this.gunsData = gunsData;
    }

    public static void encode(GunsDataMessage message, FriendlyByteBuf buffer) {
        buffer.writeMap(message.gunsData, FriendlyByteBuf::writeUtf,
                (k, v) -> buffer.writeMap(v, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeDouble));
    }

    public static GunsDataMessage decode(FriendlyByteBuf buffer) {
        return new GunsDataMessage(
                new HashMap<>(
                        buffer.readMap(FriendlyByteBuf::readUtf, k -> k.readMap(HashMap::new, FriendlyByteBuf::readUtf, FriendlyByteBuf::readDouble))
                ));
    }

    public static void handler(GunsDataMessage message, Supplier<NetworkEvent.Context> ctx) {
        //ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleGunsDataMessage(message, ctx)));
        ctx.get().setPacketHandled(true);
    }
}
