package com.matyrobbrt.mobcapturingtool;

import net.fabricmc.api.ModInitializer;

public class MobCapturingTool implements ModInitializer {
    
    @Override
    public void onInitialize() {
        MCTInit.modInit();
        MCTInit.commonSetup();
    }
}
