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

import java.util.ArrayList;
import java.util.List;

public class GobangGameAiEdition extends Game {

    private static final Identifier SELECT = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/select.png");

    private static final Identifier RIVAL_SELECT = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/rival_select.png");

    private final MultiImage PIECES = new MultiImage(
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/pieces.png"), 8, 24, 3);

    private static final int GAME_WIDTH = 16;
    private static final int GAME_HEIGHT = 12;

    private static final int NOTHING = 0;
    private static final int WHITE = 1;
    private static final int BLACK = 2;

    private int aiRetryCount = 0;

    private boolean isWhiteTurn;

    private static final int TILE_SIZE = 8;
    private static final Vec2f GAME_POS = new Vec2f(7, 3);

    private Grid grid = new Grid(GAME_WIDTH, GAME_HEIGHT, TILE_SIZE, PIECES);

    private Vec2f selectionPos = VecUtil.round(new Vec2f(GAME_WIDTH - 1, GAME_HEIGHT - 1).multiply(0.5f));
    private final Sprite selection = new Sprite(calcPos(selectionPos).add(VecUtil.of(-1)), VecUtil.of(TILE_SIZE + 2), new Image(SELECT, 8, 8));
    private final Sprite selection2 = new Sprite(calcPos(selectionPos.add(-4)).add(VecUtil.of(-1)), VecUtil.of(TILE_SIZE + 2), new Image(RIVAL_SELECT, 8, 8));


    public GobangGameAiEdition() {
        super();
    }

    @Override
    public synchronized void prepare() {
        super.prepare();
        selection.show();
        grid = new Grid(GAME_WIDTH, GAME_HEIGHT, TILE_SIZE, PIECES);
        this.isWhiteTurn = true;
    }

    @Override
    public synchronized void start() {
        super.start();
        selection2.show();
    }

    private Vec2f calcPos(Vec2f tile) {
        return tile.multiply(TILE_SIZE).add(GAME_POS);
    }

    @Override
    public synchronized void tick() {
        super.tick();
        if (stage != GameStage.PLAYING) return;

        if (ticks % 2 == 0 && isWhiteTurn) {
            if (controls.isButtonDown(Button.UP) && !controls.wasButtonDown(Button.UP)) {
                selectionPos = selectionPos.add(VecUtil.VEC_UP);
            }
            if (controls.isButtonDown(Button.DOWN) && !controls.wasButtonDown(Button.DOWN)) {
                selectionPos = selectionPos.add(VecUtil.VEC_DOWN);
            }
            if (controls.isButtonDown(Button.LEFT) && !controls.wasButtonDown(Button.LEFT)) {
                selectionPos = selectionPos.add(VecUtil.VEC_LEFT);
            }
            if (controls.isButtonDown(Button.RIGHT) && !controls.wasButtonDown(Button.RIGHT)) {
                selectionPos = selectionPos.add(VecUtil.VEC_RIGHT);
            }
            selectionPos = new Vec2f(Math.min(Math.max(selectionPos.x, 0), GAME_WIDTH - 1), Math.min(Math.max(selectionPos.y, 0), GAME_HEIGHT - 1));
            selection.setPos(calcPos(selectionPos).add(VecUtil.of(-1)));
        }
    }

    @Override
    public synchronized void gameTick() {
        super.gameTick();
        if (stage == GameStage.PLAYING && ticks % 15 == 0) {
            if (!isWhiteTurn) {
                List<Vec2f> emptySlots = new ArrayList<>();
                for (int x = 0; x < GAME_WIDTH; x++) {
                    for (int y = 0; y < GAME_HEIGHT; y++) {
                        if (grid.get(x, y) == NOTHING) {
                            emptySlots.add(new Vec2f(x, y));
                        }
                    }
                }

                if (!emptySlots.isEmpty()) {
                    Vec2f aiMove = findBestMove();
                    if (aiMove == null) {
                        aiMove = emptySlots.get(0);
                    }
                    grid.set(aiMove, BLACK);
                    selection2.setPos(calcPos(aiMove).add(VecUtil.of(-1)));
                    this.isWhiteTurn = true;
                    this.checkResult(BLACK, aiMove);
                    soundPlayer.play(SoundEvents.PACKED_MUD_PLACE);
                } else {
                    die();
                }
            }
        }
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
            if (button == Button.BUTTON1) {
                if (stage != GameStage.PLAYING) start();
                if (grid.get(selectionPos) == NOTHING) {
                    if (this.isWhiteTurn) {
                        grid.set(selectionPos, WHITE);
                        this.isWhiteTurn = false;
                        this.checkResult(WHITE, selectionPos);
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
        selection2.hide();
    }

    @Override
    public synchronized void die() {
        super.die();
        selection.hide();
        selection2.hide();
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

    @Deprecated
    @SuppressWarnings("unused")
    private void checkResult(int type) {
        //5 adjacent pieces in a row
        for (int i = 0; i < GAME_WIDTH; i++) {
            int index = 0;
            for (int j = 0; j < GAME_HEIGHT; j++) {
                if (grid.get(i ,j) == type) {
                    index ++;
                }else {
                    index = 0;
                }
                if (index == 5) {
                    if (type == WHITE) win();
                    else die();
                }
            }
        }

        //5 adjacent pieces in a column
        for (int i = 0; i < GAME_HEIGHT; i++) {
            int index = 0;
            for (int j = 0; j < GAME_WIDTH; j++) {
                if (grid.get(j ,i) == type) {
                    index ++;
                }else {
                    index = 0;
                }
                if (index == 5) {
                    if (type == WHITE) win();
                    else die();
                }
            }
        }

        //5 adjacent pieces in left oblique pos
        for (int k = 0; k < GAME_WIDTH + GAME_HEIGHT - 2; k++) {
            int startRow = Math.max(0, k - GAME_HEIGHT + 1);
            int endRow = Math.min(GAME_WIDTH - 1, k);
            int index = 0;
            for (int i = startRow; i <= endRow; i++) {
                int j = k - i;
                if (j >= 0 && j < GAME_HEIGHT) {
                    if (grid.get(i ,j) == type) {
                        index ++;
                    }else {
                        index = 0;
                    }
                    if (index == 5) {
                        if (type == WHITE) win();
                        else die();
                    }
                }
            }
        }

        //5 adjacent pieces in right oblique pos
        for (int k = 0; k < GAME_HEIGHT; k++) {
            int i = 0;
            int j = k;
            int index = 0;
            while (i < GAME_WIDTH && j < GAME_HEIGHT) {
                if (grid.get(i ,j) == type) {
                    index ++;
                }else {
                    index = 0;
                }
                if (index == 5) {
                    if (type == WHITE) win();
                    else die();
                }
                i++;
                j++;
            }
        }

        for (int k = 1; k < GAME_WIDTH; k++) {
            int i = k;
            int j = 0;
            int index = 0;
            while (i < GAME_WIDTH && j < GAME_HEIGHT) {
                if (grid.get(i ,j) == type) {
                    index ++;
                }else {
                    index = 0;
                }
                if (index == 5) {
                    if (type == WHITE) win();
                    else die();
                }
                i++;
                j++;
            }
        }
    }

    private void resetAIState() {
        aiRetryCount = 0;
        this.isWhiteTurn = true;
    }

    private Vec2f findBestMove() {
        int maxScore = -1;
        List<Vec2f> candidates = new ArrayList<>();

        for (int x = 0; x < GAME_WIDTH; x++) {
            for (int y = 0; y < GAME_HEIGHT; y++) {
                if (grid.get(x, y) == NOTHING) {
                    int score = evaluatePosition(x, y);
                    if (score > maxScore) {
                        maxScore = score;
                        candidates.clear();
                        candidates.add(new Vec2f(x, y));
                    } else if (score == maxScore) {
                        candidates.add(new Vec2f(x, y));
                    }
                }
            }
        }

        if (!candidates.isEmpty()) {
            aiRetryCount = 0;
            return candidates.get(random.nextInt(candidates.size()));
        }


        if (aiRetryCount++ > 3) {
            resetAIState();
        }
        return null;
    }

    private int evaluatePosition(int x, int y) {
        int score = 0;

        int[][] directions = {{1,0}, {0,1}, {1,1}, {1,-1}};

        for (int[] dir : directions) {

            score += evaluateLine(x, y, dir[0], dir[1], BLACK);

            score += evaluateLine(x, y, dir[0], dir[1], WHITE) * 2;
        }
        return score;
    }

    private int evaluateLine(int x, int y, int dx, int dy, int type) {
        int count = 0;
        int openEnds;

        boolean frontOpen = false;
        boolean backOpen = false;

        int cx = x + dx, cy = y + dy;
        while (cx >= 0 && cx < GAME_WIDTH && cy >= 0 && cy < GAME_HEIGHT) {
            if (grid.get(cx, cy) == type) {
                count++;
            } else {
                frontOpen = (grid.get(cx, cy) == NOTHING);
                break;
            }
            cx += dx;
            cy += dy;
        }

        cx = x - dx;
        cy = y - dy;
        while (cx >= 0 && cx < GAME_WIDTH && cy >= 0 && cy < GAME_HEIGHT) {
            if (grid.get(cx, cy) == type) {
                count++;
            } else {
                backOpen = (grid.get(cx, cy) == NOTHING);
                break;
            }
            cx -= dx;
            cy -= dy;
        }

        openEnds = (frontOpen ? 1 : 0) + (backOpen ? 1 : 0);

        if (count >= 4) return 1000;
        if (count == 3 && openEnds >= 1) return 800;
        if (count == 2 && openEnds >= 2) return 300;
        if (count == 2 && openEnds == 1) return 100;
        return Math.max(1, count * 5 + openEnds * 10);
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
    public Component getName() {
        return Component.translatable("gamediscs.gobang_ai");
    }

    @Override
    public boolean showPressAnyKey() {
        return false;
    }

    @Override
    public Identifier getIcon() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/item/game_disc_gobang_ai.png");
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_GRAY;
    }

    @Override
    public boolean showScore() {
        return false;
    }

    @Override
    public boolean renderScoreBoard() {
        return false;
    }

    @Override
    public boolean showAiReactInfo() {
        return true;
    }

    @Override
    public Identifier getBackground() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/background/gobang_background.png");
    }
}