package com.moigferdsrte.gamediscs.client.screen;

import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import net.minecraft.resources.Identifier;

public record VisualButton(
        Identifier image,
        int imageWidth,
        int imageHeight,
        int x,
        int y,
        int width,
        int height,
        int sourceX,
        int sourceY,
        int shift
) {
    public void render(GameGraphics graphics, int x, int y, boolean pressed) {
        graphics.drawTexture(image, x + this.x, y + this.y, 0, sourceX, pressed ? this.shift + sourceY : sourceY, width, height, imageWidth, imageHeight);
    }
}

