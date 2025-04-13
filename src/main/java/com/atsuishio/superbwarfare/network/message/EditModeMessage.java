package com.atsuishio.superbwarfare.network.message;

import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.network.ModVariables;
import com.atsuishio.superbwarfare.tools.SoundTool;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EditModeMessage {

    private final int type;

    public EditModeMessage(int type) {
        this.type = type;
    }

    public static EditModeMessage decode(FriendlyByteBuf buffer) {
        return new EditModeMessage(buffer.readInt());
    }

    public static void encode(EditModeMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.type);
    }

    public static void handler(EditModeMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> pressAction(context.getSender(), message.type));
        context.setPacketHandled(true);
    }

    public static void pressAction(Player player, int type) {
        if (player == null) return;

        if (type == 0) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (!(mainHandItem.getItem() instanceof GunItem gunItem)) return;
            var cap = player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null);

            if (gunItem.isCustomizable(mainHandItem)) {
                if (!player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ModVariables.PlayerVariables()).edit) {
                    SoundTool.playLocalSound(player, ModSounds.EDIT_MODE.get(), 1f, 1f);
                }

                cap.ifPresent(capability -> {
                    capability.syncPlayerVariables(player);
                });
            }
        }
    }
}
