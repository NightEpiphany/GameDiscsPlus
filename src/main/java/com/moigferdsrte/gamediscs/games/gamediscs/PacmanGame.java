package com.moigferdsrte.gamediscs.games.gamediscs;

import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import com.moigferdsrte.gamediscs.games.controls.Button;
import com.moigferdsrte.gamediscs.games.math.Vec2f;
import com.moigferdsrte.gamediscs.games.util.Game;
import com.moigferdsrte.gamediscs.games.util.GameStage;
import com.moigferdsrte.gamediscs.games.util.PacmanActor;
import com.moigferdsrte.gamediscs.games.util.PacmanDistanceField;
import com.moigferdsrte.gamediscs.games.util.PacmanDirection;
import com.moigferdsrte.gamediscs.games.util.PacmanGhost;
import com.moigferdsrte.gamediscs.games.util.PacmanMaze;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PacmanGame extends Game {
    private static final Identifier[] PACMAN_IMG = {
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/pacman_close.png"),
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/pacman_open.png")
    };
    private static final Identifier[] GHOST_IMG = {
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/ghost_blue.png"),
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/ghost_orange.png"),
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/ghost_pink.png"),
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/ghost_red.png")
    };
    private static final Identifier WEAK_GHOST_IMG = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/ghost_weak.png");
    private static final Identifier NORMAL_BEAN_IMG = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/normal_bean.png");
    private static final Identifier POWER_BEAN_IMG = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/power_bean.png");
    private static final int SPRITE_TEXTURE_SIZE = 16;
    private static final int BEAN_TEXTURE_SIZE = 8;
    private static final float ACTOR_COLLISION_SIZE = 4.2F;
    private static final float ACTOR_RENDER_SIZE = PacmanMaze.TILE_SIZE;
    private static final float PLAYER_SPEED = 1.0F;
    private static final float GHOST_SPEED = 0.92F;
    private static final int POWER_TICKS = 20 * 7;
    private static final int GHOST_RESPAWN_TICKS = 20 * 2;
    private static final int NORMAL_BEAN_SCORE = 10;
    private static final int POWER_BEAN_SCORE = 50;
    private static final int GHOST_SCORE = 200;

    private final PacmanMaze maze = new PacmanMaze();
    private final PacmanActor pacman = new PacmanActor(ACTOR_COLLISION_SIZE, PLAYER_SPEED);
    private final PacmanDistanceField ghostDistanceField = new PacmanDistanceField();
    private final List<PacmanGhost> ghosts = new ArrayList<>();
    private final List<PacmanMaze.Tile> beans = new ArrayList<>();
    private final List<PacmanMaze.Tile> powerBeans = new ArrayList<>();
    private int powerTicks;

    @Override
    public synchronized void prepare() {
        ghosts.clear();
        ghosts.add(new PacmanGhost(0, maze.tileCenter(new PacmanMaze.Tile(13, 9)), ACTOR_COLLISION_SIZE, GHOST_SPEED));
        ghosts.add(new PacmanGhost(1, maze.tileCenter(new PacmanMaze.Tile(14, 9)), ACTOR_COLLISION_SIZE, GHOST_SPEED));
        ghosts.add(new PacmanGhost(2, maze.tileCenter(new PacmanMaze.Tile(13, 10)), ACTOR_COLLISION_SIZE, GHOST_SPEED));
        ghosts.add(new PacmanGhost(3, maze.tileCenter(new PacmanMaze.Tile(14, 10)), ACTOR_COLLISION_SIZE, GHOST_SPEED));
        beans.clear();
        beans.addAll(maze.beanTiles());
        powerBeans.clear();
        powerBeans.addAll(maze.powerBeanTiles());
        powerTicks = 0;
        super.prepare();
    }

    @Override
    public synchronized void respawn() {
        super.respawn();
        pacman.reset(maze.tileCenter(new PacmanMaze.Tile(14, 18)), PacmanDirection.RIGHT);
        for (PacmanGhost ghost : ghosts) {
            ghost.resetHome();
        }
        ghostDistanceField.reset();
        powerTicks = 0;
    }

    @Override
    public synchronized void gameTick() {
        pacman.tick(maze);
        if (powerTicks > 0) {
            powerTicks--;
        }

        ghostDistanceField.update(maze, pacman.center());
        for (PacmanGhost ghost : ghosts) {
            ghost.tick(maze, ghostDistanceField, isPowered(), random);
        }

        eatBeans();
        resolveGhostCollisions();
        if (beans.isEmpty() && powerBeans.isEmpty() && stage == GameStage.PLAYING) {
            score += 500;
            win();
        }
    }

    @Override
    public synchronized void render(GameGraphics graphics, int posX, int posY) {
        super.render(graphics, posX, posY);
        if (stage == GameStage.DIED || stage == GameStage.WON) {
            return;
        }

        renderBeans(graphics, posX, posY);
        Identifier pacmanTexture = PACMAN_IMG[(ticks / 5) % PACMAN_IMG.length];
        drawActor(graphics, pacmanTexture, pacman.center(), pacman.direction().angle(), posX, posY);
        for (PacmanGhost ghost : ghosts) {
            Identifier texture = isPowered() && !ghost.isRespawning() ? WEAK_GHOST_IMG : GHOST_IMG[ghost.imageIndex()];
            drawActor(graphics, texture, ghost.center(), ghost.direction().angle(), posX, posY);
        }
    }

    @Override
    public synchronized void buttonDown(Button button) {
        super.buttonDown(button);
        if (stage != GameStage.PLAYING) {
            return;
        }
        if (button == Button.UP) {
            pacman.requestDirection(PacmanDirection.UP);
        } else if (button == Button.RIGHT) {
            pacman.requestDirection(PacmanDirection.RIGHT);
        } else if (button == Button.DOWN) {
            pacman.requestDirection(PacmanDirection.DOWN);
        } else if (button == Button.LEFT) {
            pacman.requestDirection(PacmanDirection.LEFT);
        }
    }

    @Override
    public boolean scoreText() {
        return false;
    }

    @Override
    public boolean showScore() {
        return false;
    }

    @Override
    public Identifier getIcon() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/item/game_disc_pacman.png");
    }

    @Override
    public Component getName() {
        return Component.translatable("gamediscs.pacman");
    }

    @Override
    public Identifier getBackground() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/background/pacman_background.png");
    }

    private void eatBeans() {
        PacmanMaze.Tile tile = maze.centerTile(pacman.center());
        if (beans.remove(tile)) {
            score += NORMAL_BEAN_SCORE;
            soundPlayer.playPoint();
        }
        if (powerBeans.remove(tile)) {
            score += POWER_BEAN_SCORE;
            powerTicks = POWER_TICKS;
            soundPlayer.playPoint();
        }
    }

    private void resolveGhostCollisions() {
        for (PacmanGhost ghost : ghosts) {
            if (ghost.isRespawning() || !ghost.intersects(pacman)) {
                continue;
            }
            if (isPowered()) {
                score += GHOST_SCORE;
                ghost.sendHome(GHOST_RESPAWN_TICKS);
                soundPlayer.playPoint();
            } else {
                lostLife();
                return;
            }
        }
    }

    private boolean isPowered() {
        return powerTicks > 0;
    }

    private void renderBeans(GameGraphics graphics, int posX, int posY) {
        for (PacmanMaze.Tile bean : beans) {
            drawBean(graphics, NORMAL_BEAN_IMG, bean, posX, posY, 3);
        }
        for (PacmanMaze.Tile bean : powerBeans) {
            drawBean(graphics, POWER_BEAN_IMG, bean, posX, posY, 5);
        }
    }

    private void drawBean(GameGraphics graphics, Identifier texture, PacmanMaze.Tile tile, int posX, int posY, int size) {
        Vec2f center = maze.tileCenter(tile);
        graphics.drawTexture(
                texture,
                Math.round(posX + center.x - size * 0.5F),
                Math.round(posY + center.y - size * 0.5F),
                0,
                0,
                0,
                size,
                size,
                BEAN_TEXTURE_SIZE,
                BEAN_TEXTURE_SIZE,
                BEAN_TEXTURE_SIZE,
                BEAN_TEXTURE_SIZE
        );
    }

    private static void drawActor(GameGraphics graphics, Identifier texture, Vec2f center, float angle, int posX, int posY) {
        graphics.drawRotatedTexture(
                texture,
                posX + center.x,
                posY + center.y,
                ACTOR_RENDER_SIZE,
                ACTOR_RENDER_SIZE,
                angle,
                SPRITE_TEXTURE_SIZE,
                SPRITE_TEXTURE_SIZE
        );
    }
}
