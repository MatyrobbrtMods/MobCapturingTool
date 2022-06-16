package com.matyrobbrt.mobcapturingtool;

import com.matyrobbrt.mobcapturingtool.util.Constants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class MobCapturingTool {
    
    public MobCapturingTool() {
        MCTInit.modInit();
        FMLJavaModLoadingContext.get().getModEventBus().addListener((final FMLCommonSetupEvent event) -> MCTInit.commonSetup());

        MinecraftForge.EVENT_BUS.addListener(this::onEntityInteract);
    }

    private void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
        final var res = MCTInit.handleEntityInteraction(event.getPlayer(), event.getTarget(), event.getHand());
        if (res != null) {
            event.setCancellationResult(res);
            event.setCanceled(true);
        }
    }
    
}