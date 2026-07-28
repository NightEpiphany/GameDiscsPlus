package com.moigferdsrte.gamediscs.games.gamediscs;

import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.moigferdsrte.gamediscs.games.math.Vec2f;
import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.games.controls.Button;
import com.moigferdsrte.gamediscs.games.util.Game;
import com.moigferdsrte.gamediscs.games.util.GameStage;
import com.moigferdsrte.gamediscs.games.util.Grid2048;
import com.moigferdsrte.gamediscs.games.util.Grid2048Slot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Grid2048Game extends Game {

    private static final int COLUMN_1 = 27;

    private static final int COLUMN_2 = 49;

    private static final int COLUMN_3 = 71;

    private static final int COLUMN_4 = 93;

    private static final int[] COLUMNS = { COLUMN_1, COLUMN_2, COLUMN_3, COLUMN_4 };

    private static final int ROW_1 = 13;

    private static final int ROW_2 = 35;

    private static final int ROW_3 = 57;

    private static final int ROW_4 = 79;

    private static final int[] ROWS = { ROW_1, ROW_2, ROW_3, ROW_4 };

    private final List<Grid2048Slot> grids = new ArrayList<>();

    private int moveCooldown = 0;

    public Grid2048Game() {
        super();
    }

    @Override
    public synchronized void prepare() {
        for (int row :ROWS) {
            for (int column :COLUMNS) {
                grids.add(new Grid2048Slot(new Vec2f(column, row), new Vec2f(16, 16), Grid2048.NO_GRID));
            }
        }
        super.prepare();
    }

    @Override
    public synchronized void die() {
        grids.clear();
        soundPlayer.playGameOver();
        super.die();
    }

    @Override
    public synchronized void win() {
        grids.clear();
        spawnConfetti();
        soundPlayer.playNewBest();
        super.win();
    }

    @Override
    public synchronized void start() {
        super.start();
        int init1 = random.nextInt(grids.size());
        int init2 = random.nextInt(grids.size());
        grids.set(init1, grids.get(init1).setGrid2048(Grid2048.GRID_2));
        grids.set(init2, grids.get(init2).setGrid2048(Grid2048.GRID_2));
    }

    @Override
    public synchronized void gameTick() {
        super.gameTick();
        if (moveCooldown < 2) {
            moveCooldown++;
        }
    }

    @Override
    public synchronized void render(GameGraphics graphics, int posX, int posY) {
        super.render(graphics, posX, posY);
        for (Grid2048Slot slot : grids) {
            slot.render(graphics, posX, posY);
        }
    }

    @Override
    public synchronized void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage == GameStage.PLAYING && ticks > 5) {
            if (moveCooldown >= 2) {
                switch (button) {
                    case LEFT -> {
                        if (moveLeft()) {
                            soundPlayer.playPoint();
                            this.moveCooldown = 0;
                            this.spawnNewTile();
                            checkWin();
                            if (isGameOver()) die();
                        }
                    }case RIGHT -> {
                        if (moveRight()) {
                            soundPlayer.playPoint();
                            this.moveCooldown = 0;
                            this.spawnNewTile();
                            checkWin();
                            if (isGameOver()) die();
                        }
                    }case UP -> {
                        if (moveUp()) {
                            soundPlayer.playPoint();
                            this.moveCooldown = 0;
                            this.spawnNewTile();
                            checkWin();
                            if (isGameOver()) die();
                        }
                    }case DOWN -> {
                        if (moveDown()) {
                            soundPlayer.playPoint();
                            this.moveCooldown = 0;
                            this.spawnNewTile();
                            checkWin();
                            if (isGameOver()) die();
                        }
                    }default -> this.moveCooldown = 0;
                }
            }
        }
    }


    private boolean moveLeft() {
        boolean moved = false;
        for (int row = 0; row < 4; row++) {
            int[] currentRow = new int[4];
            for (int col = 0; col < 4; col++) {
                currentRow[col] = grids.get(row * 4 + col).getGrid2048().getValue();
            }
            int[] newRow = processLine(currentRow, false);
            if (!Arrays.equals(currentRow, newRow)) {
                moved = true;
                for (int col = 0; col < 4; col++) {
                    Grid2048Slot slot = grids.get(row * 4 + col);
                    Grid2048 newGrid = Grid2048.get(newRow[col]);
                    grids.set(row * 4 + col, slot.setGrid2048(newGrid));
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < 4; row++) {
            int[] currentRow = new int[4];
            for (int col = 0; col < 4; col++) {
                currentRow[col] = grids.get(row * 4 + col).getGrid2048().getValue();
            }
            int[] newRow = processLine(currentRow, true);
            if (!Arrays.equals(currentRow, newRow)) {
                moved = true;
                for (int col = 0; col < 4; col++) {
                    Grid2048Slot slot = grids.get(row * 4 + col);
                    Grid2048 newGrid = Grid2048.get(newRow[col]);
                    grids.set(row * 4 + col, slot.setGrid2048(newGrid));
                }
            }
        }
        return moved;
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < 4; col++) {
            int[] currentCol = new int[4];
            for (int row = 0; row < 4; row++) {
                currentCol[row] = grids.get(row * 4 + col).getGrid2048().getValue();
            }
            int[] newCol = processLine(currentCol, false);
            if (!Arrays.equals(currentCol, newCol)) {
                moved = true;
                for (int row = 0; row < 4; row++) {
                    Grid2048Slot slot = grids.get(row * 4 + col);
                    Grid2048 newGrid = Grid2048.get(newCol[row]);
                    grids.set(row * 4 + col, slot.setGrid2048(newGrid));
                }
            }
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < 4; col++) {
            int[] currentCol = new int[4];
            for (int row = 0; row < 4; row++) {
                currentCol[row] = grids.get(row * 4 + col).getGrid2048().getValue();
            }
            int[] newCol = processLine(currentCol, true);
            if (!Arrays.equals(currentCol, newCol)) {
                moved = true;
                for (int row = 0; row < 4; row++) {
                    Grid2048Slot slot = grids.get(row * 4 + col);
                    Grid2048 newGrid = Grid2048.get(newCol[row]);
                    grids.set(row * 4 + col, slot.setGrid2048(newGrid));
                }
            }
        }
        return moved;
    }

    private int[] processLine(int[] line, boolean reverse) {
        List<Integer> nonZero = new ArrayList<>();
        for (int num : line) {
            if (num != 0) nonZero.add(num);
        }
        if (reverse) Collections.reverse(nonZero);

        List<Integer> merged = new ArrayList<>();
        int i = 0;
        while (i < nonZero.size()) {
            if (i + 1 < nonZero.size() && nonZero.get(i).equals(nonZero.get(i + 1))) {
                merged.add(nonZero.get(i) * 2);
                score += nonZero.get(i) * 2;
                i += 2;
            } else {
                merged.add(nonZero.get(i));
                i += 1;
            }
        }

        int[] newLine = new int[4];
        if (reverse) {
            int index = 3;
            for (int num : merged) {
                if (index < 0) break;
                newLine[index--] = num;
            }
        } else {
            int index = 0;
            for (int num : merged) {
                if (index >= 4) break;
                newLine[index++] = num;
            }
        }
        return newLine;
    }

    private void checkWin() {
        for (Grid2048Slot slot : grids) {
            if (slot.getGrid2048() == Grid2048.GRID_2048) {
                win();
                break;
            }
        }
    }

    private boolean isGameOver() {
        boolean hasEmpty = grids.stream().anyMatch(slot -> slot.getGrid2048() == Grid2048.NO_GRID);
        if (hasEmpty) return false;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Grid2048 current = grids.get(row * 4 + col).getGrid2048();
                if (col < 3 && current == grids.get(row * 4 + col + 1).getGrid2048()) {
                    return false;
                }
                if (row < 3 && current == grids.get((row + 1) * 4 + col).getGrid2048()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void spawnNewTile() {
        List<Grid2048Slot> emptySlots = grids.stream()
                .filter(slot -> slot.getGrid2048() == Grid2048.NO_GRID)
                .toList();

        if (!emptySlots.isEmpty()) {
            Grid2048Slot slot = emptySlots.get(random.nextInt(emptySlots.size()));
            Grid2048 newGrid = random.nextFloat() < 0.9 ? Grid2048.GRID_2 : Grid2048.GRID_4;
            grids.set(grids.indexOf(slot), slot.setGrid2048(newGrid));
        }
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GRAY;
    }

    @Override
    public boolean useLongScoreBox() {
        return true;
    }

    @Override
    public Identifier getIcon() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/item/game_disc_grid_2048.png");
    }

    @Override
    public Component getName() {
        return Component.translatable("gamediscs.grid_2048");
    }

    @Override
    public Identifier getBackground() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/background/grid_2048_background.png");
    }
}