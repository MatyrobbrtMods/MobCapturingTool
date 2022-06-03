package com.matyrobbrt.mobcapturingtool;

import com.google.auto.service.AutoService;
import com.matyrobbrt.mobcapturingtool.util.LoaderData;
import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Path;

@AutoService(LoaderData.class)
public class QuiltLoaderData implements LoaderData {
    @Override
    public Path getConfigDir() {
        return QuiltLoader.getConfigDir();
    }
}
