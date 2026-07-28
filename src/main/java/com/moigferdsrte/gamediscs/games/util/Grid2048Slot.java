package com.moigferdsrte.gamediscs.games.util;

import com.moigferdsrte.gamediscs.games.math.Vec2f;

public class Grid2048Slot extends Sprite {


    private final Grid2048 grid2048;

    public Grid2048Slot(Vec2f pos, Vec2f size, Grid2048 grid) {
        super(pos, size, grid.toIdentifier());
        this.grid2048 = grid;

    }

    public Grid2048 getGrid2048() {
        return grid2048;
    }

    public Grid2048Slot setGrid2048(Grid2048 grid2048) {
        return new Grid2048Slot(getPos(), getSize(), grid2048);
    }
}
