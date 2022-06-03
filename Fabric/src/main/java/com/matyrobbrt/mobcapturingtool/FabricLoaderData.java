package com.matyrobbrt.mobcapturingtool;

import com.google.auto.service.AutoService;
import com.matyrobbrt.mobcapturingtool.util.LoaderData;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

@AutoService(LoaderData.class)
public class FabricLoaderData implements LoaderData {
    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
