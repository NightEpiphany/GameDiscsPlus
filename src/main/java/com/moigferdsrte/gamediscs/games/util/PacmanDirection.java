package com.moigferdsrte.gamediscs.games.util;

public enum PacmanDirection {
    UP(0, -1, (float) (-Math.PI * 0.5)),
    RIGHT(1, 0, 0.0F),
    DOWN(0, 1, (float) (Math.PI * 0.5)),
    LEFT(-1, 0, (float) Math.PI);

    private final int x;
    private final int y;
    private final float angle;

    PacmanDirection(int x, int y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public float angle() {
        return angle;
    }

    public PacmanDirection opposite() {
        return switch (this) {
            case UP -> DOWN;
            case RIGHT -> LEFT;
            case DOWN -> UP;
            case LEFT -> RIGHT;
        };
    }
}
