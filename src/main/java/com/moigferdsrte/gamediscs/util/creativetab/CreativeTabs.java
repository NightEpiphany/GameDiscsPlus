package com.moigferdsrte.gamediscs.util.creativetab;

import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.item.ItemRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public final class CreativeTabs {
    private static final ResourceKey<CreativeModeTab> GAME_DISCS_TAB_KEY = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            GameDiscs.id("game_discs_tab")
    );

    public static final CreativeModeTab GAME_DISCS_TAB = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            GAME_DISCS_TAB_KEY,
            FabricCreativeModeTab.builder()
                    .title(Component.translatable("creativetab.game_discs_tab"))
                    .icon(() -> new ItemStack(ItemRegistry.GAMING_CONSOLE))
                    .displayItems((context, output) -> addItems(output))
                    .build()
    );

    private CreativeTabs() {
    }

    private static void addItems(CreativeModeTab.Output output) {
        output.accept(ItemRegistry.GAMING_CONSOLE);
        output.accept(ItemRegistry.REDSTONE_CIRCUIT);
        output.accept(ItemRegistry.PROCESSOR);
        output.accept(ItemRegistry.BATTERY);
        output.accept(ItemRegistry.DISPLAY);
        output.accept(ItemRegistry.CONTROL_PAD);
        output.accept(ItemRegistry.GAME_DISC_FLAPPY_BIRD);
        output.accept(ItemRegistry.GAME_DISC_SLIME);
        output.accept(ItemRegistry.GAME_DISC_BLOCKTRIS);
        output.accept(ItemRegistry.GAME_DISC_TNT_SWEEPER);
        output.accept(ItemRegistry.GAME_DISC_PONG);
        output.accept(ItemRegistry.GAME_DISC_FROGGIE);
        output.accept(ItemRegistry.GAME_DISC_RABBIT);
        output.accept(ItemRegistry.GAME_DISC_GRID_2048);
        output.accept(ItemRegistry.GAME_DISC_GOBANG);
        output.accept(ItemRegistry.GAME_DISC_GOBANG_AI);
        output.accept(ItemRegistry.GAME_DISC_PLANE_WAR);
        output.accept(ItemRegistry.GAME_DISC_ORE_CRAFTER);
        output.accept(ItemRegistry.GAME_DISC_PACMAN);
    }

    public static void registerItemGroups() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(CreativeTabs::addItems);
        GameDiscs.LOGGER.info("Registered item groups for {}", GameDiscs.MOD_ID);
    }
}
