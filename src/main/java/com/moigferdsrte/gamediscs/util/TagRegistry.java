package com.moigferdsrte.gamediscs.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;
import com.moigferdsrte.gamediscs.GameDiscs;

public final class TagRegistry {
    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> GAME_DISCS =
                createTag("game_discs");

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, name));
        }
    }
}
