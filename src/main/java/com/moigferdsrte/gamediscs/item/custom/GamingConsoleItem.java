package com.moigferdsrte.gamediscs.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import com.moigferdsrte.gamediscs.GameDiscs;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class GamingConsoleItem extends Item {
    private static final Set<String> VALID_GAMES = Set.of(
            "BlocktrisGame", "FlappyBirdGame", "FroggieGame", "GobangGameAiEdition",
            "GobangGamePlayerEdition", "Grid2048Game", "PlaneWarGame", "PongGame",
            "RabbitGame", "SlimeGame", "TntSweeperGame", "OreCrafterGame", "PacmanGame"
    );
    private static final AtomicReference<Runnable> SCREEN_OPENER = new AtomicReference<>();

    public GamingConsoleItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            Runnable opener = SCREEN_OPENER.get();
            if (opener != null) {
                opener.run();
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    public static void setScreenOpener(Runnable opener) {
        SCREEN_OPENER.set(opener);
    }

    public void setBestScore(ItemStack stack, String game, int score, Player player) {
        if (!VALID_GAMES.contains(game) || score < 0) {
            GameDiscs.LOGGER.warn("Rejected invalid score update from {}: game={}, score={}", player.getScoreboardName(), game, score);
            return;
        }

        String key = scoreKey(game, player);
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (score > tag.getIntOr(key, 0)) {
                tag.putInt(key, score);
            }
        });
    }

    public static int getBestScore(ItemStack stack, String game, Player player) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.copyTag().getIntOr(scoreKey(game, player), 0);
    }

    private static String scoreKey(String game, Player player) {
        return GameDiscs.MOD_ID + ":" + game + ";" + player.getScoreboardName();
    }
}
