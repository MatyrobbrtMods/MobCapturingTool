package com.matyrobbrt.mobcapturingtool;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class MCTClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        MCTClientInit.registerItemColours(ColorProviderRegistry.ITEM::register);
    }
}
