package com.atsuishio.superbwarfare.item.common.ammo;

import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.ModVariables;
import com.atsuishio.superbwarfare.tools.AmmoType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class AmmoSupplierItem extends Item {

    public final AmmoType type;
    public final int ammoToAdd;

    public AmmoSupplierItem(AmmoType type, int ammoToAdd, Properties properties) {
        super(properties);
        this.type = type;
        this.ammoToAdd = ammoToAdd;
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable("des.superbwarfare.ammo_supplier").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int count = stack.getCount();
        player.getCooldowns().addCooldown(this, 10);
        stack.shrink(count);

        ItemStack offhandItem = player.getOffhandItem();

        if (offhandItem.is(ModItems.AMMO_BOX.get())) {
            this.type.add(offhandItem, ammoToAdd * count);
        } else {
            player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
                this.type.add(capability, ammoToAdd * count);
                capability.syncPlayerVariables(player);
            });
        }

        if (!level.isClientSide()) {
            player.displayClientMessage(Component.translatable("item.superbwarfare.ammo_supplier.supply", Component.translatable(this.type.translatableKey), ammoToAdd * count), true);
            level.playSound(null, player.blockPosition(), ModSounds.BULLET_SUPPLY.get(), SoundSource.PLAYERS, 1, 1);
        }
        return InteractionResultHolder.success(stack);
    }
}
