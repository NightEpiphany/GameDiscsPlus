package net.tejty.gamediscs.games.util;

import net.minecraft.util.Identifier;
import net.tejty.gamediscs.GameDiscsMod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Grid2048 {
    NO_GRID(0),
    GRID_2(2),
    GRID_4(4),
    GRID_8(8),
    GRID_16(16),
    GRID_32(32),
    GRID_64(64),
    GRID_128(128),
    GRID_256(256),
    GRID_512(512),
    GRID_1024(1024),
    GRID_2048(2048);

    private final int value;


    Grid2048(int name) {
        this.value = name;
    }

    public int getValue() {
        return value;
    }


    public @NotNull Identifier toIdentifier() {
        return Identifier.of(GameDiscsMod.MOD_ID, "textures/games/sprite/grid_" + this.getValue() + ".png");
    }

    public static @Nullable Grid2048 get(int value) {
        for (Grid2048 g : values()) {
            if (g.value == value) {
                return g;
            }
        }
        return null;
    }
}
