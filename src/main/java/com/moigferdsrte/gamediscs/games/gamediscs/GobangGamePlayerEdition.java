package com.moigferdsrte.gamediscs.games.gamediscs;

import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.moigferdsrte.gamediscs.games.math.Vec2f;
import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.games.controls.Button;
import com.moigferdsrte.gamediscs.games.graphics.Image;
import com.moigferdsrte.gamediscs.games.graphics.MultiImage;
import com.moigferdsrte.gamediscs.games.util.*;

public class GobangGamePlayerEdition extends Game {

    private static final Identifier SELECT = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/select.png");

    private static final Identifier RIVAL_SELECT = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/rival_select.png");

    private final MultiImage PIECES = new MultiImage(
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/pieces.png"), 8, 24, 3);

    private static final int GAME_WIDTH = 16;
    private static final int GAME_HEIGHT = 12;

    private static final int NOTHING = 0;
    private static final int WHITE = 1;
    private static final int BLACK = 2;

    private boolean isWhiteTurn;

    private static final int TILE_SIZE = 8;
    private static final Vec2f GAME_POS = new Vec2f(7, 3);

    private Grid grid = new Grid(GAME_WIDTH, GAME_HEIGHT, TILE_SIZE, PIECES);

    private Vec2f selectionPos = VecUtil.round(new Vec2f(GAME_WIDTH - 1, GAME_HEIGHT - 1).multiply(0.5f));
    private final Sprite selection = new Sprite(calcPos(selectionPos).add(VecUtil.of(-1)), VecUtil.of(TILE_SIZE + 2), new Image(SELECT, 8, 8));
    private final Sprite selection2 = new Sprite(calcPos(selectionPos).add(VecUtil.of(-1)), VecUtil.of(TILE_SIZE + 2), new Image(RIVAL_SELECT, 8, 8));


    public GobangGamePlayerEdition() {
        super();
    }

    @Override
    public synchronized void prepare() {
        super.prepare();
        selection.show();
        selection2.hide();
        grid = new Grid(GAME_WIDTH, GAME_HEIGHT, TILE_SIZE, PIECES);
        this.isWhiteTurn = true;
    }

    private Vec2f calcPos(Vec2f tile) {
        return tile.multiply(TILE_SIZE).add(GAME_POS);
    }

    @Override
    public synchronized void buttonDown(Button button) {
        soundPlayer.playClick(true);

        // Prepares the game
        if (stage == GameStage.PLAYING || stage == GameStage.START) {
            if (button == Button.UP) {
                selectionPos = selectionPos.add(VecUtil.VEC_UP);
            }
            if (button == Button.DOWN) {
                selectionPos = selectionPos.add(VecUtil.VEC_DOWN);
            }
            if (button == Button.LEFT) {
                selectionPos = selectionPos.add(VecUtil.VEC_LEFT);
            }
            if (button == Button.RIGHT) {
                selectionPos = selectionPos.add(VecUtil.VEC_RIGHT);
            }
            selectionPos = new Vec2f(Math.min(Math.max(selectionPos.x, 0), GAME_WIDTH - 1), Math.min(Math.max(selectionPos.y, 0), GAME_HEIGHT - 1));
            selection.setPos(calcPos(selectionPos).add(VecUtil.of(-1)));
            selection2.setPos(calcPos(selectionPos).add(VecUtil.of(-1)));
            if (button == Button.BUTTON1) {
                if (stage != GameStage.PLAYING) start();
                if (grid.get(selectionPos) == NOTHING) {
                    if (this.isWhiteTurn) {
                        selection.hide();
                        selection2.show();
                        grid.set(selectionPos, WHITE);
                        this.isWhiteTurn = false;
                        this.checkResult(WHITE, selectionPos);
                    }else {
                        selection.show();
                        selection2.hide();
                        grid.set(selectionPos, BLACK);
                        this.isWhiteTurn = true;
                        this.checkResult(BLACK ,selectionPos);
                    }
                    soundPlayer.play(SoundEvents.PACKED_MUD_PLACE);
                }
            }
        }else if ((stage == GameStage.WON || stage == GameStage.DIED) && ticks > 8) {
            prepare();
        }
    }

    @Override
    public synchronized void win() {
        super.win();
        selection.hide();
    }

    @Override
    public synchronized void die() {
        super.die();
        selection.hide();
    }

    private void checkResult(int type, Vec2f placedPos) {
        final int[][] DIRECTIONS = {
                {1, 0},  // horizonal
                {0, 1},  // vertical
                {1, 1},  // right oblique
                {1, -1}  // left oblique
        };

        for (int[] dir : DIRECTIONS) {
            int dx = dir[0];
            int dy = dir[1];

            int count = 1;
            count += countDirection(placedPos, dx, dy, type);
            count += countDirection(placedPos, -dx, -dy, type);

            if (count >= 5) {
                if (type == WHITE) win();
                else die();
                return;
            }
        }
    }

    private int countDirection(Vec2f startPos, int dx, int dy, int type) {
        int count = 0;
        int x = (int)startPos.x + dx;
        int y = (int)startPos.y + dy;

        while (x >= 0 && x < GAME_WIDTH &&
                y >= 0 && y < GAME_HEIGHT &&
                grid.get(x, y) == type) {
            count++;
            x += dx;
            y += dy;
        }
        return count;
    }

    @Override
    public synchronized void render(GameGraphics graphics, int posX, int posY) {
        super.render(graphics, posX, posY);
        grid.render(graphics, posX + (int)GAME_POS.x -1, posY + (int)GAME_POS.y -1);
        selection.render(graphics, posX, posY);
        selection2.render(graphics, posX, posY);
        if (ticks % 20 >= 10)
            switch (stage) {
                case DIED -> graphics.drawTexture(Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/gui/die_board.png"), posX, posY, 0, 0, 0, 140, 100, 140, 100);
                case WON -> graphics.drawTexture(Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/gui/win_board.png"), posX, posY, 0, 0, 0, 140, 100, 140, 100);
            }
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_GRAY;
    }

    @Override
    public Component getName() {
        return Component.translatable("gamediscs.gobang");
    }

    @Override
    public boolean showPressAnyKey() {
        return false;
    }

    @Override
    public boolean showMutiPlayerInfo() {
        return true;
    }

    @Override
    public boolean renderScoreBoard() {
        return false;
    }

    @Override
    public Identifier getIcon() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/item/game_disc_gobang.png");
    }

    @Override
    public boolean showScore() {
        return false;
    }

    @Override
    public Identifier getBackground() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/background/gobang_background.png");
    }
}
