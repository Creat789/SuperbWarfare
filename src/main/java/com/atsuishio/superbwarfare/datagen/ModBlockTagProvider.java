package com.atsuishio.superbwarfare.datagen;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.init.ModBlocks;
import com.atsuishio.superbwarfare.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModUtils.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
       /* this.tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.GALENA_ORE.get(), ModBlocks.SCHEELITE_ORE.get(),
                ModBlocks.DEEPSLATE_GALENA_ORE.get(), ModBlocks.DEEPSLATE_SCHEELITE_ORE.get(), ModBlocks.DRAGON_TEETH.get(),
                ModBlocks.SILVER_ORE.get(), ModBlocks.DEEPSLATE_SILVER_ORE.get());*/

      //  this.tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.BARBED_WIRE.get());
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CONTAINER.get());//ModBlocks.CHARGING_STATION.get(),
                //ModBlocks.FUMO_25.get());
       // this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.SANDBAG.get());

        this.tag(ModTags.Blocks.SOFT_COLLISION)
                .addTags(BlockTags.LEAVES, BlockTags.FENCES, BlockTags.FENCE_GATES, BlockTags.DOORS, BlockTags.TRAPDOORS, BlockTags.WALLS)
                .add(Blocks.CACTUS, Blocks.BAMBOO, Blocks.MELON, Blocks.PUMPKIN, Blocks.HAY_BLOCK, Blocks.BELL, Blocks.CHAIN, Blocks.SNOW_BLOCK,
                        Blocks.COBWEB, Blocks.MUSHROOM_STEM, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK);
        this.tag(ModTags.Blocks.HARD_COLLISION)
                .addTags(BlockTags.LOGS, BlockTags.PLANKS, Tags.Blocks.GLASS, Tags.Blocks.GLASS_PANES, BlockTags.WOODEN_SLABS, BlockTags.WOODEN_STAIRS)
                .add(Blocks.ICE, Blocks.FROSTED_ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE);
    }
}
