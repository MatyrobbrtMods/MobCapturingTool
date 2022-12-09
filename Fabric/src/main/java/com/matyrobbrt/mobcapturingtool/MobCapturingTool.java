package com.matyrobbrt.mobcapturingtool;

import com.matyrobbrt.mobcapturingtool.item.MCTItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class MobCapturingTool implements ModInitializer {
    
    @Override
    public void onInitialize() {
        MCTInit.modInit();
        MCTInit.commonSetup();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                        .register(it -> it.accept(MCTItems.CAPTURING_TOOL.get()));
    }
}
