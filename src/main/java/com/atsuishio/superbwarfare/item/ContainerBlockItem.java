package com.atsuishio.superbwarfare.item;

import com.atsuishio.superbwarfare.client.renderer.item.ContainerBlockItemRenderer;
import com.atsuishio.superbwarfare.init.ModBlockEntities;
import com.atsuishio.superbwarfare.init.ModBlocks;
import com.atsuishio.superbwarfare.init.ModEntities;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContainerBlockItem extends BlockItem implements GeoItem {

    /**
     * 集装箱可用实体列表
     */
    public static final List<Supplier<ItemStack>> CONTAINER_ENTITIES = List.of(
            //() -> ContainerBlockItem.createInstance(ModEntities.MK_42.get()),
           // () -> ContainerBlockItem.createInstance(ModEntities.MLE_1934.get()),
           // () -> ContainerBlockItem.createInstance(ModEntities.ANNIHILATOR.get()),
            //() -> ContainerBlockItem.createInstance(ModEntities.LASER_TOWER.get()),
            () -> ContainerBlockItem.createInstance(ModEntities.SPEEDBOAT.get(), true),
            () -> ContainerBlockItem.createInstance(ModEntities.AH_6.get()),
            () -> ContainerBlockItem.createInstance(ModEntities.LAV_150.get(), true),
            () -> ContainerBlockItem.createInstance(ModEntities.BMP_2.get(), true)
           // () -> ContainerBlockItem.createInstance(ModEntities.YX_100.get()),
           // () -> ContainerBlockItem.createInstance(ModEntities.WHEEL_CHAIR.get()),
          //  () -> ContainerBlockItem.createInstance(ModEntities.TOM_6.get())
    );

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ContainerBlockItem() {
        super(ModBlocks.CONTAINER.get(), new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.getTag() != null && stack.getTag().getBoolean("CanPlacedAboveWater")) {
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getTag() != null && stack.getTag().getBoolean("CanPlacedAboveWater")) {
            BlockHitResult playerPOVHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.WATER);
            if (playerPOVHitResult.getType() == HitResult.Type.MISS) {
                return super.use(level, player, hand);
            }
            BlockHitResult blockHitResult = playerPOVHitResult.withPosition(playerPOVHitResult.getBlockPos().above());
            InteractionResult interactionresult = super.useOn(new UseOnContext(player, hand, blockHitResult));
            return new InteractionResultHolder<>(interactionresult, player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult place(BlockPlaceContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        var res = super.place(pContext);

        if (player != null) {
            var tag = BlockItem.getBlockEntityData(stack);
            if (tag != null && tag.get("Entity") != null) {
                if (player.level().isClientSide && res == InteractionResult.SUCCESS) {
                    player.getInventory().removeItem(stack);
                }
                if (!player.level().isClientSide && res == InteractionResult.CONSUME) {
                    player.getInventory().removeItem(stack);
                }
            }
        }
        return res;
    }

    private PlayState predicate(AnimationState<ContainerBlockItem> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final BlockEntityWithoutLevelRenderer renderer = new ContainerBlockItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    public static ItemStack createInstance(Entity entity) {
        return createInstance(entity, false);
    }

    public static ItemStack createInstance(EntityType<?> entityType) {
        return createInstance(entityType, false);
    }

    public static ItemStack createInstance(Entity entity, boolean canPlacedAboveWater) {
        ItemStack stack = new ItemStack(ModBlocks.CONTAINER.get());
        CompoundTag tag = new CompoundTag();
        tag.put("Entity", entity.serializeNBT());
        tag.putString("EntityType", EntityType.getKey(entity.getType()).toString());
        BlockItem.setBlockEntityData(stack, ModBlockEntities.CONTAINER.get(), tag);
        stack.getOrCreateTag().putBoolean("CanPlacedAboveWater", canPlacedAboveWater);
        return stack;
    }

    public static ItemStack createInstance(EntityType<?> entityType, boolean canPlacedAboveWater) {
        ItemStack stack = new ItemStack(ModBlocks.CONTAINER.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("EntityType", EntityType.getKey(entityType).toString());
        BlockItem.setBlockEntityData(stack, ModBlockEntities.CONTAINER.get(), tag);
        stack.getOrCreateTag().putBoolean("CanPlacedAboveWater", canPlacedAboveWater);
        return stack;
    }
}
