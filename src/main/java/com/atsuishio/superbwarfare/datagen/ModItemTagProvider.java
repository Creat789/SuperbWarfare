package com.atsuishio.superbwarfare.datagen;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_,
                              CompletableFuture<TagLookup<Block>> p_275322_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, ModUtils.MODID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
       /* this.tag(Tags.Items.DUSTS).addTags(forgeTag("dusts/coal_coke"), forgeTag("dusts/tungsten"));
        this.tag(forgeTag("dusts/coal_coke")).add(ModItems.COAL_POWDER.get());
        this.tag(forgeTag("dusts/iron")).add(ModItems.IRON_POWDER.get());
        this.tag(forgeTag("dusts/tungsten")).add(ModItems.TUNGSTEN_POWDER.get());

        this.tag(Tags.Items.INGOTS).addTags(forgeTag("ingots/lead"), forgeTag("ingots/steel"), forgeTag("ingots/tungsten"), forgeTag("ingots/silver"));
        this.tag(forgeTag("ingots/lead")).add(ModItems.LEAD_INGOT.get());
        this.tag(forgeTag("ingots/steel")).add(ModItems.STEEL_INGOT.get());
        this.tag(forgeTag("ingots/tungsten")).add(ModItems.TUNGSTEN_INGOT.get());
        this.tag(forgeTag("ingots/silver")).add(ModItems.SILVER_INGOT.get());

        this.tag(ModTags.Items.INGOTS_STEEL).addTag(forgeTag("ingots/steel"))
                .addOptional(new ResourceLocation("dreamaticvoyage", "fukamizu_bread_ingot"));
        this.tag(ModTags.Items.INGOTS_CEMENTED_CARBIDE).add(ModItems.CEMENTED_CARBIDE_INGOT.get())
                .addOptional(new ResourceLocation("dreamaticvoyage", "hqss_bread_ingot"));

        this.tag(Tags.Items.STORAGE_BLOCKS).addTags(forgeTag("storage_blocks/lead"), forgeTag("storage_blocks/steel"), forgeTag("storage_blocks/tungsten"), forgeTag("storage_blocks/silver"));
        /*this.tag(forgeTag("storage_blocks/lead")).add(ModItems.LEAD_BLOCK.get());
        this.tag(forgeTag("storage_blocks/steel")).add(ModItems.STEEL_BLOCK.get());
        this.tag(forgeTag("storage_blocks/tungsten")).add(ModItems.TUNGSTEN_BLOCK.get());
        this.tag(forgeTag("storage_blocks/silver")).add(ModItems.SILVER_BLOCK.get());*/

        /*this.tag(ModTags.Items.STORAGE_BLOCK_STEEL).addTag(forgeTag("storage_blocks/steel"))
                .addOptional(new ResourceLocation("dreamaticvoyage", "fukamizu_bread_bricks"));*/
        /*this.tag(ModTags.Items.STORAGE_BLOCK_CEMENTED_CARBIDE).add(ModItems.CEMENTED_CARBIDE_BLOCK.get())
                .addOptional(new ResourceLocation("dreamaticvoyage", "hqss_bread_bricks"));*/

       // this.tag(Tags.Items.ORES).addTags(forgeTag("ores/lead"), forgeTag("ores/tungsten"), forgeTag("ores/silver"));
        /*this.tag(forgeTag("ores/lead")).add(ModItems.GALENA_ORE.get(), ModItems.DEEPSLATE_GALENA_ORE.get());
        this.tag(forgeTag("ores/tungsten")).add(ModItems.SCHEELITE_ORE.get(), ModItems.DEEPSLATE_SCHEELITE_ORE.get());
        this.tag(forgeTag("ores/silver")).add(ModItems.SILVER_ORE.get(), ModItems.DEEPSLATE_SILVER_ORE.get());*/

       /* this.tag(Tags.Items.RAW_MATERIALS).addTags(forgeTag("raw_materials/lead"), forgeTag("raw_materials/tungsten"), forgeTag("raw_materials/silver"));
        this.tag(forgeTag("raw_materials/lead")).add(ModItems.GALENA.get());
        this.tag(forgeTag("raw_materials/tungsten")).add(ModItems.SCHEELITE.get());
        this.tag(forgeTag("raw_materials/silver")).add(ModItems.RAW_SILVER.get());*/

        /*this.tag(Tags.Items.ORE_RATES_SINGULAR).add(ModItems.GALENA_ORE.get(), ModItems.DEEPSLATE_GALENA_ORE.get(),
                ModItems.SCHEELITE_ORE.get(), ModItems.DEEPSLATE_SCHEELITE_ORE.get(),
                ModItems.SILVER_ORE.get(), ModItems.DEEPSLATE_SILVER_ORE.get());*/

        /*this.tag(Tags.Items.ORES_IN_GROUND_STONE).add(ModItems.GALENA_ORE.get(), ModItems.SCHEELITE_ORE.get(), ModItems.SILVER_ORE.get());
        this.tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(ModItems.DEEPSLATE_GALENA_ORE.get(), ModItems.DEEPSLATE_SCHEELITE_ORE.get(), ModItems.DEEPSLATE_SILVER_ORE.get());*/

        /*this.tag(forgeTag("plates")).addTags(forgeTag("plates/copper"));
        this.tag(forgeTag("plates/copper")).add(ModItems.COPPER_PLATE.get());*/

        //ModItems.GUNS.getEntries().forEach(registryObject -> this.tag(ModTags.Items.GUN).add(registryObject.get()));

       /* this.tag(ModTags.Items.SMG).add(ModItems.VECTOR.get());*/

      /*  this.tag(ModTags.Items.HANDGUN).add(ModItems.TRACHELIUM.get(), ModItems.GLOCK_17.get(), ModItems.GLOCK_18.get(), ModItems.M_1911.get(), ModItems.MP_443.get());*/

       /* this.tag(ModTags.Items.RIFLE).add(ModItems.M_4.get(), ModItems.HK_416.get(), ModItems.SKS.get(),
                ModItems.MK_14.get(), ModItems.MARLIN.get(), ModItems.AK_47.get(), ModItems.AK_12.get(), ModItems.QBZ_95.get());*/

        /*this.tag(ModTags.Items.SNIPER_RIFLE).add(ModItems.HUNTING_RIFLE.get(), ModItems.SENTINEL.get(),
                ModItems.SVD.get(), ModItems.M_98B.get(), ModItems.K_98.get(), ModItems.MOSIN_NAGANT.get());*/

        //this.tag(ModTags.Items.HEAVY_WEAPON).add(ModItems.NTW_20.get());

        //this.tag(ModTags.Items.SHOTGUN).add(ModItems.HOMEMADE_SHOTGUN.get(), ModItems.M_870.get(), ModItems.AA_12.get());

       // this.tag(ModTags.Items.MACHINE_GUN).add(ModItems.MINIGUN.get(), ModItems.DEVOTION.get(), ModItems.RPK.get(), ModItems.M_60.get());

        /*this.tag(ModTags.Items.NORMAL_GUN).add(ModItems.HOMEMADE_SHOTGUN.get(), ModItems.AK_47.get(), ModItems.AK_12.get(), ModItems.SVD.get(), ModItems.M_60.get(), ModItems.MK_14.get(), ModItems.VECTOR.get(),
                ModItems.SKS.get(), ModItems.RPK.get(), ModItems.HK_416.get(), ModItems.AA_12.get(), ModItems.M_4.get(), ModItems.DEVOTION.get(), ModItems.TRACHELIUM.get(),
                ModItems.HUNTING_RIFLE.get(), ModItems.NTW_20.get(), ModItems.M_98B.get(), ModItems.SENTINEL.get(), ModItems.M_870.get(), ModItems.MARLIN.get(), ModItems.GLOCK_17.get(),
                ModItems.GLOCK_18.get(), ModItems.M_1911.get(), ModItems.QBZ_95.get(), ModItems.K_98.get(), ModItems.MOSIN_NAGANT.get(), ModItems.MP_443.get());*/

       // this.tag(ModTags.Items.USE_HANDGUN_AMMO).add(ModItems.GLOCK_17.get(), ModItems.GLOCK_18.get(), ModItems.M_1911.get(), ModItems.VECTOR.get(), ModItems.MP_443.get());

      /*  this.tag(ModTags.Items.USE_RIFLE_AMMO).add(ModItems.M_4.get(), ModItems.HK_416.get(), ModItems.SKS.get(), ModItems.MINIGUN.get(), ModItems.DEVOTION.get(), ModItems.M_60.get(),
                ModItems.MK_14.get(), ModItems.MARLIN.get(), ModItems.AK_47.get(), ModItems.AK_12.get(), ModItems.QBZ_95.get(), ModItems.RPK.get(), ModItems.TRACHELIUM.get());*/

      /*  this.tag(ModTags.Items.USE_SHOTGUN_AMMO).add(ModItems.HOMEMADE_SHOTGUN.get(), ModItems.M_870.get(), ModItems.AA_12.get());*/

     /*   this.tag(ModTags.Items.USE_SNIPER_AMMO).add(ModItems.HUNTING_RIFLE.get(), ModItems.SENTINEL.get(),
                ModItems.SVD.get(), ModItems.M_98B.get(), ModItems.K_98.get(), ModItems.MOSIN_NAGANT.get());*/

       // this.tag(ModTags.Items.USE_HEAVY_AMMO).add(ModItems.NTW_20.get());

        //this.tag(ModTags.Items.LAUNCHER).add(ModItems.M_79.get(), ModItems.RPG.get(), ModItems.JAVELIN.get(), ModItems.SECONDARY_CATACLYSM.get());

    //    this.tag(ModTags.Items.REVOLVER).add(ModItems.TRACHELIUM.get());

        //this.tag(ModTags.Items.MILITARY_ARMOR).add(ModItems.RU_CHEST_6B43.get(), ModItems.US_CHEST_IOTV.get());
    }

    public static TagKey<Item> forgeTag(String name) {
        return ItemTags.create(new ResourceLocation("forge", name));
    }
}
