package com.matyrobbrt.mobcapturingtool.item;

import com.matyrobbrt.mobcapturingtool.util.Constants;
import com.matyrobbrt.mobcapturingtool.reg.RegistrationProvider;
import com.matyrobbrt.mobcapturingtool.reg.RegistryObject;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class MCTItems {
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registry.ITEM_REGISTRY, Constants.MOD_ID);

    public static final RegistryObject<CapturingToolItem> CAPTURING_TOOL = ITEMS.register("mob_capturing_tool",
            () -> new CapturingToolItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC).rarity(Rarity.UNCOMMON)));

    public static void loadClass() {}
}
