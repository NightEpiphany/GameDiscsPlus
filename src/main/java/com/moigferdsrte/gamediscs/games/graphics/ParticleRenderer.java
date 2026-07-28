package com.moigferdsrte.gamediscs.games.graphics;

import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import net.minecraft.resources.Identifier;

public class ParticleRenderer extends Renderer {
    private Identifier file;
    private final int fileWidth;
    private final int fileHeight;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public ParticleRenderer(Identifier file, int fileWidth, int fileHeight, int x, int y, int width, int height) {

        this.file = file;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void render(GameGraphics graphics, int posX, int posY) {
        graphics.drawTexture(file, posX - (int)(0.5 * width), posY - (int)(0.5 * height), 0, x, y, width, height, fileWidth, fileHeight);
    }
}
