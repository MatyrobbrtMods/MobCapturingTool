package com.matyrobbrt.mobcapturingtool.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Config {
    private static final Gson GSON = new GsonBuilder()
            .setLenient()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    private static Config instance;
    public static Config getInstance() {
        return instance;
    }

    @SerializedName("blacklisted_entities")
    public List<String> blacklistedEntities = List.of(
            "minecraft:ender_dragon"
    );

    @SerializedName("enable_dispenser_behaviour")
    public boolean enableDispenserBehaviour = true;

    public static Path getConfigPath() {
        return LoaderData.INSTANCE.getConfigDir().resolve(Constants.MOD_ID + ".json");
    }

    @CanIgnoreReturnValue
    public static boolean load() {
        try {
            final var path = getConfigPath();
            if (!Files.exists(path)) {
                final var cfg = new Config();
                try (final var fw = Files.newBufferedWriter(path)) {
                    GSON.toJson(cfg, fw);
                }
                instance = cfg;
                return false;
            }
            try (final var fr = Files.newBufferedReader(path)) {
                instance = GSON.fromJson(fr, Config.class);
                return true;
            }
        } catch (IOException e) {
            Constants.LOG.error("Exception trying to load MobCapturingTool config: ", e);
            instance = new Config();
        }
        return false;
    }
}
