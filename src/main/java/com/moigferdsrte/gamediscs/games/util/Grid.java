package com.moigferdsrte.gamediscs.games.util;

import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import com.moigferdsrte.gamediscs.games.math.Vec2f;
import com.moigferdsrte.gamediscs.games.graphics.Image;
import com.moigferdsrte.gamediscs.games.graphics.MultiImage;

import java.util.List;

public class Grid {
    private final int[][] map;
    private final int size;
    private final MultiImage images;

    public Grid(int width, int height, int tileSize, MultiImage images) {
        map = new int[width][height];
        size = tileSize;
        this.images = images;
    }

    public Grid(int width, int height, int tileSize, List<Image> images) {
        map = new int[width][height];
        size = tileSize;
        this.images = new MultiImage(images);
    }

    public int tileSize() {
        return size;
    }

    public int width() {
        return map.length;
    }
    public int height() {
        return map[0].length;
    }

    public int get(Vec2f pos) {
        return map[(int)pos.x][(int)pos.y];
    }
    public int get(int x, int y) {
        return map[x][y];
    }

    public void set(Vec2f pos, int value) {
        map[(int)pos.x][(int)pos.y] = value;
    }
    public void set(int x, int y, int value) {
        map[x][y] = value;
    }

    public MultiImage getImages() {
        return images;
    }

    public void render(GameGraphics graphics, int posX, int posY) {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                renderTile(graphics, posX, posY, x, y);
            }
        }
    }

    public boolean isIn(Vec2f pos) {
        return pos.x >= 0 && pos.y >= 0 && pos.x < width() && pos.y < height();
    }

    private void renderTile(GameGraphics graphics, int posX, int posY, int x, int y) {
        images.setImage(map[x][y]);
        images.render(graphics, posX + x * size, posY + y * size);
    }
}
