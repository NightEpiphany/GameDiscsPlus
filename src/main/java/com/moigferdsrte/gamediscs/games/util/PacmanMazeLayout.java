package com.moigferdsrte.gamediscs.games.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.moigferdsrte.gamediscs.GameDiscs;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;

/** 从迷宫背景构建格点和格点间的通行关系。 */
final class PacmanMazeLayout {
    private static final Identifier BACKGROUND = GameDiscs.id("textures/games/background/pacman_background.png");
    private static final int PIXEL_WIDTH = PacmanMaze.WIDTH * PacmanMaze.TILE_SIZE;
    private static final int PIXEL_HEIGHT = PacmanMaze.HEIGHT * PacmanMaze.TILE_SIZE;
    private static final String[] FALLBACK_LAYOUT = {
            "############################",
            "#.........###..###.....#...#",
            "#.######..###..###.......#.#",
            "#.######.................#.#",
            "#.######.................#.#",
            "#..........#........#....#.#",
            "#.#####.............#....#.#",
            "#.#####........#..#.#......#",
            "#.#####........#..#........#",
            "#..............#..#........#",
            "#..............#..#........#",
            "#.#####........#..#........#",
            "#.#####........#..#.#......#",
            "#.#####.............#....#.#",
            "#..........#........#....#.#",
            "#.###..##................#.#",
            "#.###..##................#.#",
            "#.###..##.###..###.......#.#",
            "#.........###..###.....#...#",
            "############################"
    };

    private final boolean[] openTiles;
    private final boolean[] rightEdges;
    private final boolean[] downEdges;

    private PacmanMazeLayout(boolean[] openTiles, boolean[] rightEdges, boolean[] downEdges) {
        this.openTiles = openTiles;
        this.rightEdges = rightEdges;
        this.downEdges = downEdges;
    }

    static PacmanMazeLayout load() {
        try (InputStream stream = Minecraft.getInstance()
                .getResourceManager()
                .getResource(BACKGROUND)
                .orElseThrow(() -> new IOException("Missing Pac-Man background: " + BACKGROUND))
                .open(); NativeImage image = NativeImage.read(stream)) {
            if (image.getWidth() != PIXEL_WIDTH || image.getHeight() != PIXEL_HEIGHT) {
                throw new IOException("Unexpected Pac-Man background size: " + image.getWidth() + "x" + image.getHeight());
            }
            return fromImage(image);
        } catch (Exception exception) {
            GameDiscs.LOGGER.error("Failed to build Pac-Man collision map; using the built-in fallback", exception);
            return fallback();
        }
    }

    boolean isOpen(int x, int y) {
        return inBounds(x, y) && openTiles[index(x, y)];
    }

    boolean canTravel(PacmanMaze.Tile from, PacmanMaze.Tile to) {
        if (!isOpen(from.x(), from.y()) || !isOpen(to.x(), to.y())) {
            return false;
        }
        int dx = to.x() - from.x();
        int dy = to.y() - from.y();
        if (dx == 0 && dy == 0) {
            return isOpen(from.x(), from.y());
        }
        if (dy == 0 && dx == 1) {
            return rightEdges[index(from.x(), from.y())];
        }
        if (dy == 0 && dx == -1) {
            return rightEdges[index(to.x(), to.y())];
        }
        if (dx == 0 && dy == 1) {
            return downEdges[index(from.x(), from.y())];
        }
        if (dx == 0 && dy == -1) {
            return downEdges[index(to.x(), to.y())];
        }
        return false;
    }

    private static PacmanMazeLayout fromImage(NativeImage image) throws IOException {
        boolean[] floorPixels = new boolean[PIXEL_WIDTH * PIXEL_HEIGHT];
        for (int y = 0; y < PIXEL_HEIGHT; y++) {
            for (int x = 0; x < PIXEL_WIDTH; x++) {
                floorPixels[pixelIndex(x, y)] = (image.getPixel(x, y) & 0x00FFFFFF) == 0;
            }
        }

        boolean[] corridorPixels = floodCorridor(floorPixels, tilePixel(1), tilePixel(1));
        boolean[] openTiles = new boolean[PacmanMaze.WIDTH * PacmanMaze.HEIGHT];
        boolean[] rightEdges = new boolean[openTiles.length];
        boolean[] downEdges = new boolean[openTiles.length];
        for (int y = 0; y < PacmanMaze.HEIGHT; y++) {
            for (int x = 0; x < PacmanMaze.WIDTH; x++) {
                openTiles[index(x, y)] = corridorPixels[pixelIndex(tilePixel(x), tilePixel(y))];
            }
        }

        for (int y = 0; y < PacmanMaze.HEIGHT; y++) {
            for (int x = 0; x < PacmanMaze.WIDTH; x++) {
                int tileIndex = index(x, y);
                if (x + 1 < PacmanMaze.WIDTH && openTiles[tileIndex] && openTiles[tileIndex + 1]) {
                    rightEdges[tileIndex] = hasClearLine(corridorPixels, x, y, x + 1, y);
                }
                if (y + 1 < PacmanMaze.HEIGHT && openTiles[tileIndex] && openTiles[tileIndex + PacmanMaze.WIDTH]) {
                    downEdges[tileIndex] = hasClearLine(corridorPixels, x, y, x, y + 1);
                }
            }
        }
        return new PacmanMazeLayout(openTiles, rightEdges, downEdges);
    }

    private static boolean[] floodCorridor(boolean[] floorPixels, int startX, int startY) throws IOException {
        int start = pixelIndex(startX, startY);
        if (!floorPixels[start]) {
            throw new IOException("Pac-Man collision seed is inside a wall");
        }

        boolean[] corridor = new boolean[floorPixels.length];
        ArrayDeque<Integer> pending = new ArrayDeque<>();
        corridor[start] = true;
        pending.add(start);
        int[] offsetX = {1, -1, 0, 0};
        int[] offsetY = {0, 0, 1, -1};
        while (!pending.isEmpty()) {
            int current = pending.removeFirst();
            int x = current % PIXEL_WIDTH;
            int y = current / PIXEL_WIDTH;
            for (int direction = 0; direction < offsetX.length; direction++) {
                int nextX = x + offsetX[direction];
                int nextY = y + offsetY[direction];
                if (nextX < 0 || nextY < 0 || nextX >= PIXEL_WIDTH || nextY >= PIXEL_HEIGHT) {
                    continue;
                }
                int next = pixelIndex(nextX, nextY);
                if (floorPixels[next] && !corridor[next]) {
                    corridor[next] = true;
                    pending.addLast(next);
                }
            }
        }
        return corridor;
    }

    private static boolean hasClearLine(boolean[] corridor, int fromX, int fromY, int toX, int toY) {
        int x = tilePixel(fromX);
        int y = tilePixel(fromY);
        int endX = tilePixel(toX);
        int endY = tilePixel(toY);
        int stepX = Integer.signum(endX - x);
        int stepY = Integer.signum(endY - y);
        while (true) {
            if (!corridor[pixelIndex(x, y)]) {
                return false;
            }
            if (x == endX && y == endY) {
                return true;
            }
            x += stepX;
            y += stepY;
        }
    }

    private static PacmanMazeLayout fallback() {
        boolean[] openTiles = new boolean[PacmanMaze.WIDTH * PacmanMaze.HEIGHT];
        boolean[] rightEdges = new boolean[openTiles.length];
        boolean[] downEdges = new boolean[openTiles.length];
        for (int y = 0; y < PacmanMaze.HEIGHT; y++) {
            for (int x = 0; x < PacmanMaze.WIDTH; x++) {
                int tileIndex = index(x, y);
                openTiles[tileIndex] = FALLBACK_LAYOUT[y].charAt(x) == '.';
            }
        }
        for (int y = 0; y < PacmanMaze.HEIGHT; y++) {
            for (int x = 0; x < PacmanMaze.WIDTH; x++) {
                int tileIndex = index(x, y);
                rightEdges[tileIndex] = x + 1 < PacmanMaze.WIDTH && openTiles[tileIndex] && openTiles[tileIndex + 1];
                downEdges[tileIndex] = y + 1 < PacmanMaze.HEIGHT && openTiles[tileIndex] && openTiles[tileIndex + PacmanMaze.WIDTH];
            }
        }
        return new PacmanMazeLayout(openTiles, rightEdges, downEdges);
    }

    private static int tilePixel(int tile) {
        return tile * PacmanMaze.TILE_SIZE + PacmanMaze.TILE_SIZE / 2;
    }

    private static int pixelIndex(int x, int y) {
        return x + y * PIXEL_WIDTH;
    }

    private static int index(int x, int y) {
        return x + y * PacmanMaze.WIDTH;
    }

    private static boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < PacmanMaze.WIDTH && y < PacmanMaze.HEIGHT;
    }
}
