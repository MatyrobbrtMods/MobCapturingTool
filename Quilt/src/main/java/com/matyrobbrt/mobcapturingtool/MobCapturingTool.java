package com.matyrobbrt.mobcapturingtool;

import com.matyrobbrt.mobcapturingtool.item.MCTItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class MobCapturingTool implements ModInitializer {

    @Override
    public void onInitialize(ModContainer mod) {
        MCTInit.modInit();
        MCTInit.commonSetup();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(it -> it.accept(MCTItems.CAPTURING_TOOL.get()));
    }
}
