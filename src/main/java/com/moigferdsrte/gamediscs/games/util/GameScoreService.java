package com.moigferdsrte.gamediscs.games.util;

import com.moigferdsrte.gamediscs.item.custom.GamingConsoleItem;
import com.moigferdsrte.gamediscs.util.networking.payload.SetBestScoreC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

final class GameScoreService {
    boolean submitIfNewBest(Class<? extends Game> gameType, int score) {
        Player player = Minecraft.getInstance().player;
        ItemStack console = findConsole(player);
        String gameId = gameType.getSimpleName();
        if (player == null || console.isEmpty() || GamingConsoleItem.getBestScore(console, gameId, player) >= score) {
            return false;
        }

        ClientPlayNetworking.send(new SetBestScoreC2SPayload(gameId, score));
        return true;
    }

    int getBestScore(Class<? extends Game> gameType) {
        Player player = Minecraft.getInstance().player;
        ItemStack console = findConsole(player);
        return player == null || console.isEmpty()
                ? 0
                : GamingConsoleItem.getBestScore(console, gameType.getSimpleName(), player);
    }

    private static ItemStack findConsole(Player player) {
        if (player == null) {
            return ItemStack.EMPTY;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof GamingConsoleItem) {
            return mainHand;
        }

        ItemStack offHand = player.getOffhandItem();
        return offHand.getItem() instanceof GamingConsoleItem ? offHand : ItemStack.EMPTY;
    }
}
