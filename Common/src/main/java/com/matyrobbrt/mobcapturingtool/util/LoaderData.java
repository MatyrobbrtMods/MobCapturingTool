package com.matyrobbrt.mobcapturingtool.util;

import net.minecraft.Util;

import java.nio.file.Path;
import java.util.ServiceLoader;

public interface LoaderData {

    LoaderData INSTANCE = Util.make(() -> {
        final var loader = ServiceLoader.load(LoaderData.class).iterator();
        if (!loader.hasNext()) {
            throw new NullPointerException("No MCT LoaderData was found on the classpath");
        }
        final var api = loader.next();
        if (loader.hasNext()) {
            throw new IllegalArgumentException("More than one MCT LoaderData was found!");
        }
        return api;
    });

    Path getConfigDir();
}
