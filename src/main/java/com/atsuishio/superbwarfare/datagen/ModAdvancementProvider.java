package com.atsuishio.superbwarfare.datagen;

import com.atsuishio.superbwarfare.init.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Codes Based on @Create
 */
@SuppressWarnings("unused")
public class ModAdvancementProvider implements DataProvider {

    private final PackOutput packOutput;
    private final ExistingFileHelper existingFileHelper;

    public static final List<ModAdvancement> ADVANCEMENTS = new ArrayList<>();

    public static ModAdvancement START = null,
    /**
     * Main
     */
    MAIN_ROOT = advancement("root", builder -> builder.icon(ModItems.TASER.get())
            .type(ModAdvancement.Type.SILENT)
            .awardedForFree()),

    BEST_FRIEND = advancement("best_friend", builder -> builder.icon(ModItems.CLAYMORE_MINE.get())
            .whenIconCollected()
            .type(ModAdvancement.Type.SECRET)
            .parent(MAIN_ROOT)),

    END = null;


    public ModAdvancementProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        this.packOutput = output;
        this.existingFileHelper = existingFileHelper;
    }

    private static ModAdvancement advancement(String id, UnaryOperator<ModAdvancement.Builder> b) {
        return new ModAdvancement(id, b);
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        PackOutput.PathProvider pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");

        Consumer<Advancement> mainConsumer = (advancement) -> {
            ResourceLocation id = advancement.getId();
            if (existingFileHelper.exists(id, PackType.SERVER_DATA, ".json", "advancements")) {
                throw new IllegalStateException("Duplicate advancement " + id);
            }
            Path path = pathProvider.json(id);
            futures.add(DataProvider.saveStable(pOutput, advancement.deconstruct().serializeToJson(), path));
        };

        for (ModAdvancement advancement : ADVANCEMENTS) {
            advancement.save(mainConsumer);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Superb Warfare Advancements";
    }
}
