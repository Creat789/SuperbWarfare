package com.atsuishio.superbwarfare.datagen;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagProvider extends DamageTypeTagsProvider {

    public ModDamageTypeTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, ModUtils.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(ModTags.DamageTypes.PROJECTILE).add(ModDamageTypes.GUN_FIRE, ModDamageTypes.GUN_FIRE_HEADSHOT,
                        DamageTypes.ARROW, DamageTypes.TRIDENT, DamageTypes.THROWN)
                .addOptional(new ResourceLocation("tacz", "bullet"))
                .addOptional(new ResourceLocation("tacz", "bullet_void"));
        this.tag(ModTags.DamageTypes.PROJECTILE_ABSOLUTE).add(ModDamageTypes.GUN_FIRE_ABSOLUTE, ModDamageTypes.GUN_FIRE_HEADSHOT_ABSOLUTE)
                .addOptional(new ResourceLocation("tacz", "bullet_ignore_armor"))
                .addOptional(new ResourceLocation("tacz", "bullet_void_ignore_armor"));

    }

}
