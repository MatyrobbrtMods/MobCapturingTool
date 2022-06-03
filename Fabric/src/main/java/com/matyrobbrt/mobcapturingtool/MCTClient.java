package com.matyrobbrt.mobcapturingtool;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public class MCTClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MCTClientInit.registerItemColours(ColorProviderRegistry.ITEM::register);
    }
}
