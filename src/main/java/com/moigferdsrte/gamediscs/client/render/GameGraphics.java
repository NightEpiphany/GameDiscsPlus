package com.moigferdsrte.gamediscs.client.render;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

/**
 * 隔离小游戏渲染代码与 Minecraft GUI 提取接口。
 */
public final class GameGraphics {
    private final GuiGraphicsExtractor delegate;

    public GameGraphics(GuiGraphicsExtractor delegate) {
        this.delegate = delegate;
    }

    public void drawTexture(
            Identifier texture,
            int x,
            int y,
            int z,
            float u,
            float v,
            int width,
            int height,
            int textureWidth,
            int textureHeight
    ) {
        delegate.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, textureWidth, textureHeight);
    }

    public void drawTexture(
            Identifier texture,
            int x,
            int y,
            int z,
            float u,
            float v,
            int width,
            int height,
            int sourceWidth,
            int sourceHeight,
            int textureWidth,
            int textureHeight
    ) {
        delegate.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x,
                y,
                u,
                v,
                width,
                height,
                sourceWidth,
                sourceHeight,
                textureWidth,
                textureHeight
        );
    }

    public void drawText(Font font, Component text, int x, int y, int color, boolean shadow) {
        delegate.text(font, text, x, y, opaqueArgb(color), shadow);
    }

    public void drawText(Font font, String text, int x, int y, int color, boolean shadow) {
        delegate.text(font, text, x, y, opaqueArgb(color), shadow);
    }

    public void drawScaledText(Font font, Component text, float x, float y, int color, boolean shadow, float scale) {
        drawScaledText(font, text.getVisualOrderText(), x, y, color, shadow, scale);
    }

    public void drawScaledText(Font font, FormattedCharSequence text, float x, float y, int color, boolean shadow, float scale) {
        if (scale <= 0.0F) {
            return;
        }
        delegate.pose().pushMatrix();
        try {
            delegate.pose().translate(x, y).scale(scale, scale);
            delegate.text(font, text, 0, 0, opaqueArgb(color), shadow);
        } finally {
            delegate.pose().popMatrix();
        }
    }

    public void drawRotatedSprite(TextureAtlasSprite sprite, float centerX, float centerY, float size, float angle) {
        int drawSize = Math.max(1, Math.round(size));
        int x = Math.round(centerX - drawSize * 0.5F);
        int y = Math.round(centerY - drawSize * 0.5F);
        delegate.pose().pushMatrix();
        try {
            delegate.pose().rotateAbout(angle, centerX, centerY);
            delegate.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, drawSize, drawSize);
        } finally {
            delegate.pose().popMatrix();
        }
    }

    public void drawRotatedTexture(
            Identifier texture,
            float centerX,
            float centerY,
            float width,
            float height,
            float angle,
            int textureWidth,
            int textureHeight
    ) {
        int drawWidth = Math.max(1, Math.round(width));
        int drawHeight = Math.max(1, Math.round(height));
        int x = Math.round(centerX - drawWidth * 0.5F);
        int y = Math.round(centerY - drawHeight * 0.5F);
        delegate.pose().pushMatrix();
        try {
            delegate.pose().rotateAbout(angle, centerX, centerY);
            delegate.blit(
                    RenderPipelines.GUI_TEXTURED,
                    texture,
                    x,
                    y,
                    0,
                    0,
                    drawWidth,
                    drawHeight,
                    textureWidth,
                    textureHeight,
                    textureWidth,
                    textureHeight
            );
        } finally {
            delegate.pose().popMatrix();
        }
    }

    public void nextStratum() {
        delegate.nextStratum();
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        delegate.enableScissor(x0, y0, x1, y1);
    }

    public void disableScissor() {
        delegate.disableScissor();
    }

    private static int opaqueArgb(int color) {
        return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
    }
}
