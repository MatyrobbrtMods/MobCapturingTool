package com.matyrobbrt.mobcapturingtool;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class MobCapturingTool implements ModInitializer {

    @Override
    public void onInitialize(ModContainer mod) {
        MCTInit.modInit();
        MCTInit.commonSetup();
    }
}
