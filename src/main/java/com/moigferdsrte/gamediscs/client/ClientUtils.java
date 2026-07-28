package com.moigferdsrte.gamediscs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import com.moigferdsrte.gamediscs.client.screen.GamingConsoleScreen;
import com.moigferdsrte.gamediscs.games.gamediscs.*;
import com.moigferdsrte.gamediscs.games.util.Game;
import com.moigferdsrte.gamediscs.item.ItemRegistry;
import com.moigferdsrte.gamediscs.item.custom.GameDiscItem;

import java.util.Map;
import java.util.function.Supplier;

public final class ClientUtils {
    private static final Map<Item, Supplier<Game>> GAMES = Map.ofEntries(
            Map.entry(ItemRegistry.GAME_DISC_FLAPPY_BIRD, FlappyBirdGame::new),
            Map.entry(ItemRegistry.GAME_DISC_SLIME, SlimeGame::new),
            Map.entry(ItemRegistry.GAME_DISC_BLOCKTRIS, BlocktrisGame::new),
            Map.entry(ItemRegistry.GAME_DISC_TNT_SWEEPER, TntSweeperGame::new),
            Map.entry(ItemRegistry.GAME_DISC_PONG, PongGame::new),
            Map.entry(ItemRegistry.GAME_DISC_FROGGIE, FroggieGame::new),
            Map.entry(ItemRegistry.GAME_DISC_RABBIT, RabbitGame::new),
            Map.entry(ItemRegistry.GAME_DISC_GRID_2048, Grid2048Game::new),
            Map.entry(ItemRegistry.GAME_DISC_GOBANG, GobangGamePlayerEdition::new),
            Map.entry(ItemRegistry.GAME_DISC_GOBANG_AI, GobangGameAiEdition::new),
            Map.entry(ItemRegistry.GAME_DISC_PLANE_WAR, PlaneWarGame::new),
            Map.entry(ItemRegistry.GAME_DISC_ORE_CRAFTER, OreCrafterGame::new),
            Map.entry(ItemRegistry.GAME_DISC_PACMAN, PacmanGame::new)
    );

    private ClientUtils() {
    }

    public static void openConsoleScreen() {
        Minecraft.getInstance().gui.setScreen(new GamingConsoleScreen(Component.translatable("gui.gamingconsole.title")));
    }

    public static Game newGameFor(GameDiscItem item) {
        Supplier<Game> sup = GAMES.get(item);
        if (sup == null) {
            throw new IllegalArgumentException("No game specified for " + item + " (" + BuiltInRegistries.ITEM.getKey(item) + ")");
        }
        return sup.get();
    }
}
