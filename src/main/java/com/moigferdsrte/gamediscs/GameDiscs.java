package com.moigferdsrte.gamediscs;

import com.moigferdsrte.gamediscs.item.ItemRegistry;
import com.moigferdsrte.gamediscs.sounds.SoundRegistry;
import com.moigferdsrte.gamediscs.util.DiscLootTablesModifiers;
import com.moigferdsrte.gamediscs.util.creativetab.CreativeTabs;
import com.moigferdsrte.gamediscs.util.networking.ModMessages;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameDiscs implements ModInitializer {
	public static final String MOD_ID = "gamediscs";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ItemRegistry.registerModItems();
		CreativeTabs.registerItemGroups();
		DiscLootTablesModifiers.modifyLootTables();
		SoundRegistry.registerSounds();
		ModMessages.registerC2SPackets();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
