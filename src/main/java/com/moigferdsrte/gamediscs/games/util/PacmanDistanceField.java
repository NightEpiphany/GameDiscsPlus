package com.moigferdsrte.gamediscs.games.util;

import com.moigferdsrte.gamediscs.games.math.Vec2f;

import java.util.Arrays;

/** 缓存所有格子到吃豆人所在格的最短路径距离。 */
public final class PacmanDistanceField {
    public static final int UNREACHABLE = Integer.MAX_VALUE;
    private static final PacmanDirection[] DIRECTIONS = PacmanDirection.values();

    private final int[] distances = new int[PacmanMaze.WIDTH * PacmanMaze.HEIGHT];
    private final int[] pending = new int[distances.length];
    private PacmanMaze.Tile target;

    public PacmanDistanceField() {
        reset();
    }

    public void update(PacmanMaze maze, Vec2f targetCenter) {
        PacmanMaze.Tile nextTarget = maze.centerTile(targetCenter);
        if (nextTarget.equals(target)) {
            return;
        }

        target = nextTarget;
        Arrays.fill(distances, UNREACHABLE);
        if (!maze.isOpen(target.x(), target.y())) {
            return;
        }

        int head = 0;
        int tail = 0;
        int targetIndex = index(target.x(), target.y());
        distances[targetIndex] = 0;
        pending[tail++] = targetIndex;
        while (head < tail) {
            int currentIndex = pending[head++];
            PacmanMaze.Tile current = tile(currentIndex);
            int nextDistance = distances[currentIndex] + 1;
            for (PacmanDirection direction : DIRECTIONS) {
                PacmanMaze.Tile neighbor = new PacmanMaze.Tile(
                        current.x() + direction.x(),
                        current.y() + direction.y()
                );
                if (!maze.canTravel(current, neighbor)) {
                    continue;
                }
                int neighborIndex = index(neighbor.x(), neighbor.y());
                if (distances[neighborIndex] != UNREACHABLE) {
                    continue;
                }
                distances[neighborIndex] = nextDistance;
                pending[tail++] = neighborIndex;
            }
        }
    }

    public int distance(PacmanMaze.Tile tile) {
        if (tile.x() < 0 || tile.y() < 0 || tile.x() >= PacmanMaze.WIDTH || tile.y() >= PacmanMaze.HEIGHT) {
            return UNREACHABLE;
        }
        return distances[index(tile.x(), tile.y())];
    }

    public void reset() {
        target = null;
        Arrays.fill(distances, UNREACHABLE);
    }

    private static PacmanMaze.Tile tile(int index) {
        return new PacmanMaze.Tile(index % PacmanMaze.WIDTH, index / PacmanMaze.WIDTH);
    }

    private static int index(int x, int y) {
        return x + y * PacmanMaze.WIDTH;
    }
}
