package com.moigferdsrte.gamediscs.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.moigferdsrte.gamediscs.games.controls.Button;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

/**
 * Runtime smoke test for the input mapping used by the console screen.
 * Fabric's GameTest runner discovers this method when game tests are enabled.
 */
public final class GamingConsoleGameTest {
    private GamingConsoleGameTest() {
    }

    @GameTest
    public static void wasdScancodesMapToVirtualButtons(GameTestHelper helper) {
        if (GamingConsoleScreen.buttonForKey(InputConstants.KEY_W) != Button.UP
                || GamingConsoleScreen.buttonForKey(InputConstants.KEY_A) != Button.LEFT
                || GamingConsoleScreen.buttonForKey(InputConstants.KEY_S) != Button.DOWN
                || GamingConsoleScreen.buttonForKey(InputConstants.KEY_D) != Button.RIGHT) {
            throw helper.assertionException("WASD scancodes must map to the console directions");
        }
        helper.succeed();
    }
}
