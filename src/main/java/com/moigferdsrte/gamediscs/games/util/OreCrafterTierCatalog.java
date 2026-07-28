package com.moigferdsrte.gamediscs.games.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moigferdsrte.gamediscs.GameDiscs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class OreCrafterTierCatalog {
    private static final String RESOURCE_PATH = "/data/gamediscs/games/ore_crafter/tier.json";
    private static final int MAX_SUPPORTED_TIERS = 32;
    private static final OreCrafterTierCatalog INSTANCE = load();

    private final List<Tier> tiers;

    private OreCrafterTierCatalog(List<Tier> tiers) {
        this.tiers = List.copyOf(tiers);
    }

    public static OreCrafterTierCatalog instance() {
        return INSTANCE;
    }

    public Tier get(int level) {
        if (level < 1 || level > tiers.size()) {
            throw new IllegalArgumentException("Unknown ore crafter tier: " + level);
        }
        return tiers.get(level - 1);
    }

    public int maxLevel() {
        return tiers.size();
    }

    private static OreCrafterTierCatalog load() {
        try (InputStream stream = OreCrafterTierCatalog.class.getResourceAsStream(RESOURCE_PATH)) {
            if (stream == null) {
                throw new IllegalStateException("Missing " + RESOURCE_PATH);
            }

            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject tierValues = root.getAsJsonObject("tier");
            JsonObject scoreValues = root.getAsJsonObject("score");
            int count = root.get("count").getAsInt();
            if (count < 2 || count > MAX_SUPPORTED_TIERS || tierValues == null || scoreValues == null) {
                throw new IllegalStateException("Invalid ore crafter tier count or sections");
            }

            List<Tier> loaded = new ArrayList<>(count);
            for (int level = 1; level <= count; level++) {
                String key = Integer.toString(level);
                Identifier blockId = Identifier.parse(tierValues.get(key).getAsString());
                Block block = BuiltInRegistries.BLOCK.getValue(blockId);
                if (!BuiltInRegistries.BLOCK.containsKey(blockId)) {
                    throw new IllegalStateException("Unknown block for ore crafter tier " + level + ": " + blockId);
                }

                int score = scoreValues.get(key).getAsInt();
                if (score < 0) {
                    throw new IllegalStateException("Negative score for ore crafter tier " + level);
                }
                loaded.add(new Tier(level, block, score, sizeFor(level)));
            }
            return new OreCrafterTierCatalog(loaded);
        } catch (Exception exception) {
            GameDiscs.LOGGER.error("Failed to load ore crafter tiers; using built-in defaults", exception);
            return defaults();
        }
    }

    private static OreCrafterTierCatalog defaults() {
        String[] blockIds = {
                "minecraft:coal_block", "minecraft:copper_block", "minecraft:iron_block",
                "minecraft:gold_block", "minecraft:lapis_block", "minecraft:redstone_block",
                "minecraft:diamond_block", "minecraft:emerald_block", "minecraft:obsidian",
                "minecraft:ancient_debris", "minecraft:netherite_block"
        };
        int[] scores = {100, 150, 300, 400, 550, 600, 800, 900, 1000, 2000, 2500};
        List<Tier> fallback = new ArrayList<>(blockIds.length);
        for (int i = 0; i < blockIds.length; i++) {
            int level = i + 1;
            fallback.add(new Tier(level, BuiltInRegistries.BLOCK.getValue(Identifier.parse(blockIds[i])), scores[i], sizeFor(level)));
        }
        return new OreCrafterTierCatalog(fallback);
    }

    private static float sizeFor(int level) {
        return Math.round(6.0F + (level - 1) * 1.5F);
    }

    public record Tier(int level, Block block, int score, float size) {
    }
}
