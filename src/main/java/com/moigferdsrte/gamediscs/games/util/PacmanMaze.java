package com.moigferdsrte.gamediscs.games.util;

import com.moigferdsrte.gamediscs.games.math.Vec2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PacmanMaze {
    public static final int TILE_SIZE = 5;
    public static final int WIDTH = 28;
    public static final int HEIGHT = 20;
    private static final PacmanMazeLayout LAYOUT = PacmanMazeLayout.load();

    private final List<Tile> beanTiles = new ArrayList<>();
    private final List<Tile> powerBeanTiles = List.of(
            new Tile(1, 1),
            new Tile(26, 1),
            new Tile(1, 18),
            new Tile(26, 18)
    );

    public PacmanMaze() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile(x, y);
                if (isOpen(x, y) && !isSpawnTile(tile) && !powerBeanTiles.contains(tile)) {
                    beanTiles.add(tile);
                }
            }
        }
    }

    public boolean isOpen(int x, int y) {
        return LAYOUT.isOpen(x, y);
    }

    public boolean canOccupy(Vec2f center, float entitySize) {
        float radius = entitySize * 0.5F - 0.35F;
        return isOpen(pixelToTile(center.x - radius), pixelToTile(center.y - radius))
                && isOpen(pixelToTile(center.x + radius), pixelToTile(center.y - radius))
                && isOpen(pixelToTile(center.x - radius), pixelToTile(center.y + radius))
                && isOpen(pixelToTile(center.x + radius), pixelToTile(center.y + radius));
    }

    public boolean canMove(Vec2f from, Vec2f to, float entitySize) {
        if (!canOccupy(to, entitySize)) {
            return false;
        }
        return LAYOUT.canTravel(centerTile(from), centerTile(to));
    }

    boolean canTravel(Tile from, Tile to) {
        return LAYOUT.canTravel(from, to);
    }

    public Vec2f tileCenter(Tile tile) {
        return new Vec2f(tile.x() * TILE_SIZE + TILE_SIZE * 0.5F, tile.y() * TILE_SIZE + TILE_SIZE * 0.5F);
    }

    public Tile centerTile(Vec2f center) {
        return new Tile(pixelToTile(center.x), pixelToTile(center.y));
    }

    public boolean isNearTileCenter(Vec2f center, float threshold) {
        Vec2f tileCenter = tileCenter(centerTile(center));
        return Math.abs(center.x - tileCenter.x) <= threshold && Math.abs(center.y - tileCenter.y) <= threshold;
    }

    public List<Tile> beanTiles() {
        return Collections.unmodifiableList(beanTiles);
    }

    public List<Tile> powerBeanTiles() {
        return powerBeanTiles;
    }

    private static int pixelToTile(float pixel) {
        return (int) Math.floor(pixel / TILE_SIZE);
    }

    private static boolean isSpawnTile(Tile tile) {
        return tile.y() >= 8 && tile.y() <= 11 && tile.x() >= 12 && tile.x() <= 15;
    }

    public record Tile(int x, int y) {
    }
}
