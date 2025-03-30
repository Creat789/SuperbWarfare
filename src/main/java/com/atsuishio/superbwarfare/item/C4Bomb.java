package com.atsuishio.superbwarfare.item;

import com.atsuishio.superbwarfare.entity.projectile.C4Entity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class C4Bomb extends Item implements DispenserLaunchable {

    public static final String TAG_CONTROL = "Control";

    public C4Bomb() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            boolean flag = stack.getOrCreateTag().getBoolean(TAG_CONTROL);

            C4Entity entity = new C4Entity(player, level, flag);
            entity.setPos(player.getX() + 0.25 * player.getLookAngle().x, player.getEyeY() - 0.2f + 0.25 * player.getLookAngle().y, player.getZ() + 0.25 * player.getLookAngle().z);
            entity.setDeltaMovement(0.5 * player.getLookAngle().x, 0.5 * player.getLookAngle().y, 0.5 * player.getLookAngle().z);
            entity.setOwner(player);

            level.addFreshEntity(entity);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.level().playSound(null, serverPlayer.getOnPos(), ModSounds.C4_THROW.get(), SoundSource.PLAYERS, 1, 1);
        }

        player.getCooldowns().addCooldown(this, 20);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        if (pStack.getOrCreateTag().getBoolean(TAG_CONTROL)) {
            pTooltipComponents.add(Component.translatable("des.superbwarfare.c4_bomb.control").withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.add(Component.translatable("des.superbwarfare.c4_bomb.time").withStyle(ChatFormatting.GRAY));
        }
    }

    public static ItemStack makeInstance() {
        ItemStack stack = new ItemStack(ModItems.C4_BOMB.get());
        stack.getOrCreateTag().putBoolean(TAG_CONTROL, true);
        return stack;
    }

    @Override
    public AbstractProjectileDispenseBehavior getLaunchBehavior() {
        return new AbstractProjectileDispenseBehavior() {

            @Override
            protected float getPower() {
                return 0.15F;
            }

            @Override
            @ParametersAreNonnullByDefault
            protected @NotNull Projectile getProjectile(Level level, Position position, ItemStack stack) {
                var c4 = new C4Entity((LivingEntity) null, level);
                c4.setPos(position.x(), position.y(), position.z());
                return c4;
            }

            @Override
            protected void playSound(BlockSource pSource) {
                pSource.getLevel().playSound(null, pSource.getPos(), ModSounds.C4_THROW.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        };
    }
}
