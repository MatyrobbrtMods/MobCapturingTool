package com.matyrobbrt.mobcapturingtool;

import com.matyrobbrt.mobcapturingtool.util.Constants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = Constants.MOD_ID)
public class MCTClient {

    @SubscribeEvent
    static void registerItemColours(RegisterColorHandlersEvent.Item event) {
        MCTClientInit.registerItemColours(event::register);
    }

}
