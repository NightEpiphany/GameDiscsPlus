package com.moigferdsrte.gamediscs.item;

import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Rarity;
import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.item.custom.GameDiscItem;
import com.moigferdsrte.gamediscs.item.custom.GamingConsoleItem;

import java.util.function.Function;

public final class ItemRegistry {
    public static final Item GAMING_CONSOLE = registerItem("gaming_console",
            properties -> new GamingConsoleItem(properties.rarity(Rarity.UNCOMMON)));

    public static final Item GAME_DISC_FLAPPY_BIRD = registerItem("game_disc_flappy_bird",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.flappy_bird").withStyle(ChatFormatting.YELLOW)));
    public static final Item GAME_DISC_SLIME = registerItem("game_disc_slime",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.slime").withStyle(ChatFormatting.DARK_GREEN)));
    public static final Item GAME_DISC_BLOCKTRIS = registerItem("game_disc_blocktris",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.blocktris").withStyle(ChatFormatting.BLUE)));
    public static final Item GAME_DISC_TNT_SWEEPER = registerItem("game_disc_tnt_sweeper",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.tnt_sweeper").withStyle(ChatFormatting.RED)));
    public static final Item GAME_DISC_PONG = registerItem("game_disc_pong",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.pong_game").withStyle(ChatFormatting.WHITE)));
    public static final Item GAME_DISC_FROGGIE = registerItem("game_disc_froggie",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.froggie").withStyle(ChatFormatting.GREEN)));
    public static final Item GAME_DISC_RABBIT = registerItem("game_disc_rabbit",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.rabbit").withStyle(ChatFormatting.GOLD)));
    public static final Item GAME_DISC_GRID_2048 = registerItem("game_disc_grid_2048",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.grid_2048").withStyle(ChatFormatting.GRAY)));
    public static final Item GAME_DISC_GOBANG = registerItem("game_disc_gobang",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.gobang").withStyle(ChatFormatting.DARK_GRAY)));
    public static final Item GAME_DISC_GOBANG_AI = registerItem("game_disc_gobang_ai",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.gobang_ai").withStyle(ChatFormatting.DARK_GRAY)));
    public static final Item GAME_DISC_PLANE_WAR = registerItem("game_disc_plane_war",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.plane_war").withStyle(ChatFormatting.DARK_AQUA)));
    public static final Item GAME_DISC_ORE_CRAFTER = registerItem("game_disc_ore_crafter",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.ore_crafter").withStyle(ChatFormatting.GOLD)));
    public static final Item GAME_DISC_PACMAN = registerItem("game_disc_pacman",
            properties -> new GameDiscItem(properties.rarity(Rarity.RARE), Component.translatable("gamediscs.pacman").withStyle(ChatFormatting.YELLOW)));


    public static final Item REDSTONE_CIRCUIT = registerItem("redstone_circuit",
            Item::new);

    public static final Item PROCESSOR = registerItem("processor",
            Item::new);

    public static final Item BATTERY = registerItem("battery",
            Item::new);

    public static final Item DISPLAY = registerItem("display",
            Item::new);

    public static final Item CONTROL_PAD = registerItem("control_pad",
            Item::new);


    private static Item registerItem(String name, Function<Item.Properties, Item> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        Item item = factory.apply(new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void registerModItems() {
        GameDiscs.LOGGER.info("Registering Mod Items for " + GameDiscs.MOD_ID);
    }
}
