package com.matyrobbrt.mobcapturingtool;

import com.matyrobbrt.mobcapturingtool.item.MCTItems;
import com.matyrobbrt.mobcapturingtool.util.Constants;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class MobCapturingTool {
    
    public MobCapturingTool() {
        MCTInit.modInit();

        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener((final FMLCommonSetupEvent event) -> MCTInit.commonSetup());
        modBus.addListener((final CreativeModeTabEvent.BuildContents event) -> {
            if (event.getTab() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(MCTItems.CAPTURING_TOOL.get());
            }
        });

        MinecraftForge.EVENT_BUS.addListener(this::onEntityInteract);
    }

    private void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
        final var res = MCTInit.handleEntityInteraction(event.getEntity(), event.getTarget(), event.getHand());
        if (res != null) {
            event.setCancellationResult(res);
            event.setCanceled(true);
        }
    }
}