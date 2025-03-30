package com.atsuishio.superbwarfare.init;

import com.atsuishio.superbwarfare.ModUtils;
import com.atsuishio.superbwarfare.item.SmallContainerBlockItem;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

@Mod.EventBusSubscriber(modid = ModUtils.MODID)
public class ModVillagers {

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, ModUtils.MODID);
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, ModUtils.MODID);

    /*public static final RegistryObject<PoiType> ARMORY_POI = POI_TYPES.register("armory",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.REFORGING_TABLE.get().getStateDefinition().getPossibleStates()), 1, 1));*/

  /*  public static final RegistryObject<VillagerProfession> ARMORY = VILLAGER_PROFESSIONS.register("armory",
            () -> new VillagerProfession("armory", holder -> holder.get() == ARMORY_POI.get(), holder -> holder.get() == ARMORY_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(), null));*/

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        VILLAGER_PROFESSIONS.register(eventBus);
    }

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
    }

    private static Holder<Item> getItemHolder(String name) {
        return ForgeRegistries.ITEMS.getHolder(new ResourceLocation(ModUtils.MODID, name)).orElse(new Holder.Direct<>(ItemStack.EMPTY.getItem()));
    }

    @SubscribeEvent
    public static void addWandererTrade(WandererTradesEvent event) {
        List<VillagerTrades.ItemListing> rareTrades = event.getRareTrades();

        rareTrades.add(new BasicItemListing(new ItemStack(Items.EMERALD, 16),
                SmallContainerBlockItem.createInstance(ModUtils.loc("containers/blueprints")), 10, 0, 0.05f));
    }
}
