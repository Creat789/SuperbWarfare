package com.atsuishio.superbwarfare.datagen;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.init.ModItems;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.BiConsumer;

public class ModContainerLootProvider implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceLocation, LootTable.Builder> pOutput) {
    }

    public LootPool.Builder singleItem(ItemLike item, int weight) {
        return singleItem(item, 1, 0, weight, 0);
    }

    public LootPool.Builder singleItem(ItemLike item, float rolls, float bonus, int weight, int quality) {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(rolls)).setBonusRolls(ConstantValue.exactly(bonus))
                .add(LootItem.lootTableItem(item).setWeight(weight).setQuality(quality));
    }

    public final LootPool.Builder multiItems(float rolls, float bonus, PoolTriple... triplet) {
        var builder = LootPool.lootPool().setRolls(ConstantValue.exactly(rolls)).setBonusRolls(ConstantValue.exactly(bonus));
        for (var t : triplet) {
            builder.add(LootItem.lootTableItem(t.item()).setWeight(t.weight()).setQuality(t.quality()));
        }
        return builder;
    }

    public record PoolTriple(ItemLike item, int weight, int quality) {

    }
}
