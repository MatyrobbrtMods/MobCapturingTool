package com.matyrobbrt.mobcapturingtool;

import com.matyrobbrt.mobcapturingtool.util.Constants;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class MobCapturingTool {
    
    public MobCapturingTool() {
        MCTInit.modInit();
        FMLJavaModLoadingContext.get().getModEventBus().addListener((final FMLCommonSetupEvent event) -> MCTInit.commonSetup());
    }
    
}