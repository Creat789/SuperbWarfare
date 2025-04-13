package com.atsuishio.superbwarfare.network.message;

import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.network.ModVariables;
import com.atsuishio.superbwarfare.tools.GunsTool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReloadMessage {

    private final int type;

    public ReloadMessage(int type) {
        this.type = type;
    }

    public static ReloadMessage decode(FriendlyByteBuf buffer) {
        return new ReloadMessage(buffer.readInt());
    }

    public static void encode(ReloadMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.type);
    }

    public static void handler(ReloadMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                pressAction(context.getSender(), message.type);
            }
        });
        context.setPacketHandled(true);
    }

    public static void pressAction(Player player, int type) {
        if (type == 0) {
            player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                capability.syncPlayerVariables(player);
            });

            ItemStack stack = player.getMainHandItem();
            var capability = player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ModVariables.PlayerVariables());

            if (!player.isSpectator()
                    && stack.getItem() instanceof GunItem gunItem
                    && !GunsTool.getGunBooleanTag(stack, "Charging")
                    && GunsTool.getGunIntTag(stack, "ReloadTime") == 0
                    && GunsTool.getGunIntTag(stack, "BoltActionTick") == 0
                    && !GunsTool.getGunBooleanTag(stack, "Reloading")
            ) {
                CompoundTag tag = stack.getOrCreateTag();

                boolean canSingleReload = gunItem.isIterativeReload(stack);
                boolean canReload = gunItem.isMagazineReload(stack) && !gunItem.isClipReload(stack);
                boolean clipLoad = GunsTool.getGunIntTag(stack, "Ammo", 0) == 0 && gunItem.isClipReload(stack);


                if (canReload || clipLoad) {
                    int magazine = GunsTool.getGunIntTag(stack, "Magazine", 0);

                    if (gunItem.isOpenBolt(stack)) {
                        if (gunItem.hasBulletInBarrel(stack)) {
                            if (GunsTool.getGunIntTag(stack, "Ammo", 0) < magazine + GunsTool.getGunIntTag(stack, "CustomMagazine", 0) + 1) {
                                GunsTool.setGunBooleanTag(stack, "StartReload", true);
                            }
                        } else {
                            if (GunsTool.getGunIntTag(stack, "Ammo", 0) < magazine + GunsTool.getGunIntTag(stack, "CustomMagazine", 0)) {
                                GunsTool.setGunBooleanTag(stack, "StartReload", true);
                            }
                        }
                    } else if (GunsTool.getGunIntTag(stack, "Ammo", 0) < magazine + GunsTool.getGunIntTag(stack, "CustomMagazine", 0)) {
                        GunsTool.setGunBooleanTag(stack, "StartReload", true);
                    }
                    return;
                }

                if (canSingleReload) {
                    if (GunsTool.getGunIntTag(stack, "Ammo", 0) < GunsTool.getGunIntTag(stack, "Magazine", 0)
                            + GunsTool.getGunIntTag(stack, "CustomMagazine", 0)) {
                        tag.putBoolean("start_single_reload", true);
                    }
                }
            }
        }
    }
}
