package com.moigferdsrte.gamediscs.client;

import com.moigferdsrte.gamediscs.item.custom.GamingConsoleItem;
import net.fabricmc.api.ClientModInitializer;

public final class GameDiscsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        GamingConsoleItem.setScreenOpener(ClientUtils::openConsoleScreen);
    }
}
