package com.moigferdsrte.gamediscs.util;

import com.moigferdsrte.gamediscs.item.ItemRegistry;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.Map;

public final class DiscLootTablesModifiers {
    private static final float CHEST_DROP_CHANCE = 0.3F;
    private static final Map<Identifier, Float> CHEST_TABLES = Map.ofEntries(
            chest("chests/simple_dungeon"),
            chest("chests/stronghold_corridor"),
            chest("chests/stronghold_crossing"),
            chest("chests/stronghold_library"),
            chest("chests/end_city_treasure"),
            chest("chests/woodland_mansion"),
            chest("chests/buried_treasure"),
            chest("chests/ruined_portal"),
            chest("chests/ancient_city"),
            chest("chests/ancient_city_ice_box"),
            chest("chests/abandoned_mineshaft"),
            chest("chests/jungle_temple"),
            chest("chests/desert_pyramid"),
            chest("chests/bastion_bridge"),
            chest("chests/bastion_hoglin_stable"),
            chest("chests/bastion_other"),
            chest("chests/bastion_treasure")
    );
    private static final Map<Identifier, Item> MOB_DISCS = Map.of(
            vanilla("entities/bee"), ItemRegistry.GAME_DISC_FLAPPY_BIRD,
            vanilla("entities/slime"), ItemRegistry.GAME_DISC_SLIME,
            vanilla("entities/frog"), ItemRegistry.GAME_DISC_FROGGIE,
            vanilla("entities/rabbit"), ItemRegistry.GAME_DISC_RABBIT
    );

    private DiscLootTablesModifiers() {
    }

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }

            Float chance = CHEST_TABLES.get(key.identifier());
            if (chance != null) {
                tableBuilder.withPool(LootPool.lootPool()
                        .add(TagEntry.expandTag(TagRegistry.Items.GAME_DISCS))
                        .when(LootItemRandomChanceCondition.randomChance(chance)));
            }

            Item disc = MOB_DISCS.get(key.identifier());
            if (disc != null) {
                EntityPredicate.Builder skeleton = EntityPredicate.Builder.entity()
                        .of(registries.lookupOrThrow(Registries.ENTITY_TYPE), EntityTypeTags.SKELETONS);
                tableBuilder.withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(disc))
                        .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, skeleton)));
            }
        });
    }

    private static Map.Entry<Identifier, Float> chest(String path) {
        return Map.entry(vanilla(path), CHEST_DROP_CHANCE);
    }

    private static Identifier vanilla(String path) {
        return Identifier.withDefaultNamespace(path);
    }
}
