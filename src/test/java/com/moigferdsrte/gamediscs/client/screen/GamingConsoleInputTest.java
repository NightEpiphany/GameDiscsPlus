package com.moigferdsrte.gamediscs.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.moigferdsrte.gamediscs.games.controls.Button;
import net.minecraft.client.input.KeyEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GamingConsoleInputTest {
    @Test
    void maps26Point3Scancodes() {
        assertEquals(Button.UP, buttonFor(new KeyEvent(InputConstants.KEY_W, 119, 0)));
        assertEquals(Button.DOWN, buttonFor(new KeyEvent(InputConstants.KEY_S, 115, 0)));
        assertEquals(Button.LEFT, buttonFor(new KeyEvent(InputConstants.KEY_A, 97, 0)));
        assertEquals(Button.RIGHT, buttonFor(new KeyEvent(InputConstants.KEY_D, 100, 0)));
    }

    @Test
    void ignoresLegacyGlfwKeycodes() {
        assertNull(GamingConsoleScreen.buttonForKey(87));
        assertNull(GamingConsoleScreen.buttonForKey(83));
    }

    private static Button buttonFor(KeyEvent event) {
        return GamingConsoleScreen.buttonForKey(event.key());
    }
}
