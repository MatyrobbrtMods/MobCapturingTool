package com.matyrobbrt.mobcapturingtool.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String MOD_ID = "mobcapturingtool";
	public static final String MOD_NAME = "MobCapturingTool";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final TagKey<EntityType<?>> BLACKLISTED_TAG = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MOD_ID, "blacklisted"));

	public static MutableComponent getTranslation(String type, Object... args) {
		return Component.translatable("tooltip." + MOD_ID + "." + type, args);
	}
}