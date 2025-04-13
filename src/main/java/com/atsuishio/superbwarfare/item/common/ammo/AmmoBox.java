package com.atsuishio.superbwarfare.item.common.ammo;

import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.network.ModVariables;
import com.atsuishio.superbwarfare.tools.AmmoType;
import com.atsuishio.superbwarfare.tools.FormatTool;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AmmoBox extends Item {

    public AmmoBox() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hand == InteractionHand.OFF_HAND) return InteractionResultHolder.fail(stack);

        CompoundTag tag = stack.getOrCreateTag();
        player.getCooldowns().addCooldown(this, 10);
        String type = stack.getOrCreateTag().getString("Type");

        var cap = player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ModVariables.PlayerVariables());
        player.getCapability(ModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
            var types = type.equals("All") ? AmmoType.values() : new AmmoType[]{AmmoType.getType(type)};

            for (var ammoType : types) {
                if (ammoType == null) return;

            }
            capability.syncPlayerVariables(player);

            if (!level.isClientSide()) {
                level.playSound(null, player.blockPosition(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1, 1);
            }

            // 取出弹药时，若弹药盒为掉落物版本，则移除弹药盒物品
            if (!player.isCrouching() && tag.getBoolean("IsDrop")) {
                stack.shrink(1);
            }
        });
        return InteractionResultHolder.consume(stack);
    }

    private static final List<String> ammoTypeList = generateAmmoTypeList();

    private static List<String> generateAmmoTypeList() {
        var list = new ArrayList<String>();
        list.add("All");

        for (var ammoType : AmmoType.values()) {
            list.add(ammoType.name);
        }

        return list;
    }


    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity instanceof Player player && player.isCrouching()) {
            var tag = stack.getOrCreateTag();
            var index = Math.max(0, ammoTypeList.indexOf(tag.getString("Type")));
            var typeString = ammoTypeList.get((index + 1) % ammoTypeList.size());

            tag.putString("Type", typeString);
            entity.playSound(ModSounds.FIRE_RATE.get(), 1f, 1f);

            var type = AmmoType.getType(typeString);
            if (type == null) {
                player.displayClientMessage(Component.translatable("des.superbwarfare.ammo_box.type.all").withStyle(ChatFormatting.WHITE), true);
                return true;
            }

            switch (type) {
                case RIFLE ->
                        player.displayClientMessage(Component.translatable("des.superbwarfare.ammo_box.type.rifle").withStyle(ChatFormatting.GREEN), true);
                case HANDGUN ->
                        player.displayClientMessage(Component.translatable("des.superbwarfare.ammo_box.type.handgun").withStyle(ChatFormatting.AQUA), true);
                case SHOTGUN ->
                        player.displayClientMessage(Component.translatable("des.superbwarfare.ammo_box.type.shotgun").withStyle(ChatFormatting.RED), true);
                case SNIPER ->
                        player.displayClientMessage(Component.translatable("des.superbwarfare.ammo_box.type.sniper").withStyle(ChatFormatting.GOLD), true);
                case HEAVY ->
                        player.displayClientMessage(Component.translatable("des.superbwarfare.ammo_box.type.heavy").withStyle(ChatFormatting.LIGHT_PURPLE), true);
            }
        }

        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        var type = AmmoType.getType(stack.getOrCreateTag().getString("Type"));
        var tag = stack.getOrCreateTag();

        tooltip.add(Component.translatable("des.superbwarfare.ammo_box").withStyle(ChatFormatting.GRAY));

        tooltip.add(Component.translatable("des.superbwarfare.ammo_box.handgun").withStyle(ChatFormatting.AQUA)
                .append(Component.literal("").withStyle(ChatFormatting.RESET))
                .append(Component.literal(FormatTool.format0D(AmmoType.HANDGUN.get(tag)) + ((type != AmmoType.HANDGUN) ? " " : " ←-")).withStyle(ChatFormatting.BOLD)));

        tooltip.add(Component.translatable("des.superbwarfare.ammo_box.rifle").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("").withStyle(ChatFormatting.RESET))
                .append(Component.literal(FormatTool.format0D(AmmoType.RIFLE.get(tag)) + ((type != AmmoType.RIFLE) ? " " : " ←-")).withStyle(ChatFormatting.BOLD)));

        tooltip.add(Component.translatable("des.superbwarfare.ammo_box.shotgun").withStyle(ChatFormatting.RED)
                .append(Component.literal("").withStyle(ChatFormatting.RESET))
                .append(Component.literal(FormatTool.format0D(AmmoType.SHOTGUN.get(tag)) + ((type != AmmoType.SHOTGUN) ? " " : " ←-")).withStyle(ChatFormatting.BOLD)));

        tooltip.add(Component.translatable("des.superbwarfare.ammo_box.sniper").withStyle(ChatFormatting.GOLD)
                .append(Component.literal("").withStyle(ChatFormatting.RESET))
                .append(Component.literal(FormatTool.format0D(AmmoType.SNIPER.get(tag)) + ((type != AmmoType.SNIPER) ? " " : " ←-")).withStyle(ChatFormatting.BOLD)));

        tooltip.add(Component.translatable("des.superbwarfare.ammo_box.heavy").withStyle(ChatFormatting.LIGHT_PURPLE)
                .append(Component.literal("").withStyle(ChatFormatting.RESET))
                .append(Component.literal(FormatTool.format0D(AmmoType.HEAVY.get(tag)) + ((type != AmmoType.HEAVY) ? " " : " ←-")).withStyle(ChatFormatting.BOLD)));
    }
}
