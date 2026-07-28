package com.moigferdsrte.gamediscs.games.gamediscs;

import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import com.moigferdsrte.gamediscs.games.controls.Button;
import com.moigferdsrte.gamediscs.games.util.OreBlockBody;
import com.moigferdsrte.gamediscs.games.util.OreCrafterPhysicsWorld;
import com.moigferdsrte.gamediscs.games.util.OreCrafterTierCatalog;
import com.moigferdsrte.gamediscs.games.util.Game;
import com.moigferdsrte.gamediscs.games.util.GameStage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class OreCrafterGame extends Game {
    private static final float PLAY_LEFT = 42.0F;
    private static final float PLAY_RIGHT = 110.0F;
    private static final float FLOOR = 100.0F;
    private static final float DANGER_LINE = 15.0F;
    private static final float PREVIEW_Y = 8.0F;
    private static final float MOVE_SPEED = 1.25F;
    private static final int DROP_COOLDOWN = 8;
    // 3 sec
    private static final int LOSS_ACTIVATION_TICKS = 20 * 3;
    private static final int MAX_BODIES = 96;
    private static final float SIDEBAR_TEXT_WIDTH = 32.0F;
    private static final float SIDEBAR_LABEL_SCALE = 0.68F;
    private static final float SIDEBAR_TIER_SCALE = 0.72F;
    private static final float SIDEBAR_LINE_HEIGHT = 7.0F;

    private final OreCrafterTierCatalog tiers = OreCrafterTierCatalog.instance();
    private final OreCrafterPhysicsWorld physics = new OreCrafterPhysicsWorld(PLAY_LEFT, PLAY_RIGHT, FLOOR);
    private int previewTier = 1;
    private float previewX = (PLAY_LEFT + PLAY_RIGHT) * 0.5F;
    private int highestTier;
    private int dropCooldown;

    @Override
    public synchronized void prepare() {
        physics.clear();
        previewTier = randomSpawnTier();
        previewX = (PLAY_LEFT + PLAY_RIGHT) * 0.5F;
        highestTier = 0;
        dropCooldown = 0;
        super.prepare();
    }

    @Override
    public synchronized void gameTick() {
        movePreview();
        if (dropCooldown > 0) {
            dropCooldown--;
        }

        boolean mergedAny = false;
        for (OreCrafterPhysicsWorld.Collision collision : physics.step()) {
            OreBlockBody first = collision.first();
            OreBlockBody second = collision.second();
            if (first.tier() != second.tier() || first.tier() >= tiers.maxLevel()) {
                continue;
            }

            int mergedTier = first.tier() + 1;
            OreCrafterTierCatalog.Tier tier = tiers.get(mergedTier);
            OreBlockBody merged = physics.merge(collision, mergedTier, tier.size());
            if (merged == null) {
                continue;
            }

            score += tier.score();
            highestTier = Math.max(highestTier, mergedTier);
            mergedAny = true;
            if (mergedTier == tiers.maxLevel()) {
                soundPlayer.playPoint();
                win();
                return;
            }
        }

        if (mergedAny) {
            soundPlayer.playPoint();
        }
        if (physics.bodyCount() > MAX_BODIES) {
            die();
            return;
        }

        if (physics.hasBodyAbove(DANGER_LINE, LOSS_ACTIVATION_TICKS)) {
            die();
        }
    }

    @Override
    public synchronized void render(GameGraphics graphics, int posX, int posY) {
        graphics.drawTexture(getBackground(), posX, posY, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);

        if (stage != GameStage.DIED && stage != GameStage.WON) {
            for (OreBlockBody body : physics.bodies()) {
                drawTier(graphics, body.tier(), posX + body.x(), posY + body.y(), body.size(), body.angle());
            }
            OreCrafterTierCatalog.Tier preview = tiers.get(previewTier);
            drawTier(graphics, previewTier, posX + previewX, posY + PREVIEW_Y, preview.size(), 0.0F);
            if (highestTier > 0) {
                drawTier(graphics, highestTier, posX + 18.0F, posY + 33.0F, 14.0F, 0.0F);
                drawHighestTierText(graphics, posX, posY);
            }
        }

    }

    @Override
    public synchronized void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage == GameStage.PLAYING) {
            if (button == Button.LEFT) {
                adjustPreview(-MOVE_SPEED);
            } else if (button == Button.RIGHT) {
                adjustPreview(MOVE_SPEED);
            } else if (button.isActionButton() && dropCooldown == 0) {
                dropPreview();
            }
        }
    }

    private void movePreview() {
        float movement = 0.0F;
        if (controls.isButtonDown(Button.LEFT)) {
            movement -= MOVE_SPEED;
        }
        if (controls.isButtonDown(Button.RIGHT)) {
            movement += MOVE_SPEED;
        }
        adjustPreview(movement);
    }

    private void adjustPreview(float movement) {
        OreCrafterTierCatalog.Tier tier = tiers.get(previewTier);
        previewX = physics.clampX(previewX + movement, tier.size());
    }

    private void dropPreview() {
        OreCrafterTierCatalog.Tier tier = tiers.get(previewTier);
        physics.spawn(previewTier, tier.size(), previewX, PREVIEW_Y);
        highestTier = Math.max(highestTier, previewTier);
        previewTier = randomSpawnTier();
        previewX = physics.clampX(previewX, tiers.get(previewTier).size());
        dropCooldown = DROP_COOLDOWN;
    }

    private int randomSpawnTier() {
        return random.nextBoolean() ? 1 : 2;
    }

    private void drawTier(GameGraphics graphics, int level, float x, float y, float size, float angle) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getBlockStateModelSet()
                .getParticleMaterial(tiers.get(level).block().defaultBlockState())
                .sprite();
        graphics.drawRotatedSprite(sprite, x, y, size, angle);
    }

    private void drawHighestTierText(GameGraphics graphics, int posX, int posY) {
        Font font = Minecraft.getInstance().font;
        Component label = Component.translatable("gamediscs.ore_crafter.current_stage");
        Component tierName = Component.translatable("gamediscs.ore_crafter.tier." + highestTier);
        float nextY = drawSidebarLines(graphics, font, label, posX, posY + 46.0F, SIDEBAR_LABEL_SCALE);
        drawSidebarLines(graphics, font, tierName, posX, nextY, SIDEBAR_TIER_SCALE);
    }

    private float drawSidebarLines(
            GameGraphics graphics,
            Font font,
            Component text,
            int posX,
            float y,
            float scale
    ) {
        int wrapWidth = Math.max(1, (int) (SIDEBAR_TEXT_WIDTH / scale));
        List<FormattedCharSequence> lines = font.split(text, wrapWidth);
        for (FormattedCharSequence line : lines) {
            drawSidebarLine(graphics, font, line, posX, y, scale);
            y += SIDEBAR_LINE_HEIGHT;
        }
        return y;
    }

    private void drawSidebarLine(
            GameGraphics graphics,
            Font font,
            FormattedCharSequence text,
            int posX,
            float y,
            float maximumScale
    ) {
        int textWidth = Math.max(1, font.width(text));
        float scale = Math.min(maximumScale, SIDEBAR_TEXT_WIDTH / textWidth);
        float x = posX + 18.0F - textWidth * scale * 0.5F;
        graphics.drawScaledText(font, text, x, y, 0x696969, false, scale);
    }

    @Override
    public boolean showScoreBox() {
        return false;
    }

    @Override
    public boolean scoreText() {
        return false;
    }

    @Override
    public int scoreColor() {
        return 0x696969;
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GOLD;
    }

    @Override
    public Identifier getIcon() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/item/game_disc_ore_crafter.png");
    }

    @Override
    public Component getName() {
        return Component.translatable("gamediscs.ore_crafter");
    }

    @Override
    public Identifier getBackground() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/background/ore_crafter_background.png");
    }
}
