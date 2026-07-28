package com.moigferdsrte.gamediscs.games.gamediscs;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import com.moigferdsrte.gamediscs.games.math.Vec2f;
import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.games.controls.Button;
import com.moigferdsrte.gamediscs.games.graphics.DirectionalImage;
import com.moigferdsrte.gamediscs.games.graphics.Image;
import com.moigferdsrte.gamediscs.games.util.Game;
import com.moigferdsrte.gamediscs.games.util.GameStage;
import com.moigferdsrte.gamediscs.games.util.Sprite;
import com.moigferdsrte.gamediscs.games.util.VecUtil;

import java.util.ArrayList;
import java.util.List;

public class FroggieGame extends Game {
    private static final int TILE_SIZE = 7;
    private static final int[][] MINECART_LAYOUT = {
            {20, 12, -2}, {19, 12, -2}, {18, 12, -2}, {12, 12, -2}, {11, 12, -2},
            {0, 11, 2}, {10, 11, 2},
            {15, 10, -4}, {16, 10, -4}, {5, 10, -4}, {6, 10, -4},
            {13, 9, 6},
            {8, 8, -2}, {9, 8, -2}, {10, 8, -2}, {16, 8, -2}, {17, 8, -2}, {18, 8, -2}
    };
    private static final List<Integer> HOLES = List.of(2, 6, 10, 14, 18);

    private final Sprite frog = new Sprite(
            new Vec2f(0, 0),
            new Vec2f(7, 7),
            new DirectionalImage(
                    Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/frog.png"),
                    7,
                    28
            )
    );
    private int moveCooldown = 0;

    private final List<Sprite> minecarts = new ArrayList<>();
    private final List<Sprite> logs = new ArrayList<>();
    private final List<Boolean> isHoleFull = new ArrayList<>();

    private int lastLine = 0;

    private static Vec2f getPos(Vec2f tile) {
        return tile.multiply(TILE_SIZE);
    }

    private static Vec2f getTile(Vec2f pos) {
        return VecUtil.round(pos.multiply(1f / TILE_SIZE));
    }

    public FroggieGame() {
        super();
        resetHoles();
    }

    @Override
    public synchronized void prepare() {
        resetHoles();

        // Calls prepare of super
        super.prepare();

        // Resets everything
        minecarts.clear();
        logs.clear();
        for (int[] minecart : MINECART_LAYOUT) {
            addMinecart(minecart[0], minecart[1], minecart[2]);
        }

        addPlatforms("log.png", 63, 21, 5, 1, 1, 5, 16);
        addPlatforms("turtles.png", 28, 14, 6, -1, 1, 4, 9, 15);
        addPlatforms("log.png", 63, 63, 4, 2, 2);
        addPlatforms("turtles.png", 28, 28, 3, -2, 3, 9, 15);
        addPlatforms("log.png", 63, 35, 2, 2, 1, 8, 14);
    }

    private void resetHoles() {
        isHoleFull.clear();
        for (int i = 0; i < HOLES.size(); i++) {
            isHoleFull.add(false);
        }
    }

    private void addMinecart(int tileX, int tileY, int speed) {
        minecarts.add(new Sprite(
                getPos(new Vec2f(tileX, tileY)),
                new Vec2f(7, 7),
                Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/minecart.png")
        ).addVelocity(new Vec2f(speed, 0)));
    }

    private void addPlatforms(String texture, int textureWidth, int width, int tileY, int speed, int... tileXs) {
        Identifier id = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/" + texture);
        for (int tileX : tileXs) {
            logs.add(new Sprite(
                    getPos(new Vec2f(tileX, tileY)),
                    new Vec2f(width, 7),
                    new Image(id, textureWidth, 7, 0, 0, width, 7)
            ).addVelocity(new Vec2f(speed, 0)));
        }
    }

    @Override
    public synchronized void respawn() {
        super.respawn();

        frog.setPos(new Vec2f((float) WIDTH / 2 - (float) TILE_SIZE / 2, 13 * TILE_SIZE));
        frog.setImage(new DirectionalImage(
                Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/frog.png"),
                7,
                28
        ));
        lastLine = (int)getTile(frog.getPos()).y;
    }

    @Override
    public synchronized void start() {

        // Calls start of super
        super.start();

        // Make everything on start
        // here
    }
    @Override
    public synchronized void tick() {
        // Calls tick of super
        super.tick();

        // Make animation tick or anything that ticks even if the game is in DIED state
        // here
    }
    @Override
    public synchronized void gameTick() {
        // Calls game tick of super
        super.gameTick();

        // Ticks all sprites and everything that ticks only when the game is running
        int i = 0;
        while (i < minecarts.size()) {
            Sprite minecart = minecarts.get(i);
            int oldX = (int)minecart.getX();
            minecart.tick();
            int newX = (int)minecart.getX();
            if (oldX > 0 && newX <= 0) {
                minecarts.add(
                        new Sprite(new Vec2f(WIDTH, minecart.getY()), minecart.getSize(), minecart.getImage()).addVelocity(minecart.getVelocity())
                );
            }
            if (newX + minecart.getWidth() < 0) {
                minecarts.remove(i);
                i--;
            }
            if (oldX + minecart.getWidth() < WIDTH && newX + minecart.getWidth() >= WIDTH) {
                minecarts.add(
                        new Sprite(new Vec2f(0 - minecart.getWidth(), minecart.getY()), minecart.getSize(), minecart.getImage()).addVelocity(minecart.getVelocity())
                );
            }
            if (newX > WIDTH) {
                minecarts.remove(i);
                i--;
            }

            if (minecart.isTouching(frog)) {
                lostLife();
            }

            i++;
        }

        Sprite logOn = null;

        i = 0;
        while (i < logs.size()) {
            Sprite log = logs.get(i);
            int oldX = (int)log.getX();
            log.tick();
            int newX = (int)log.getX();
            if (oldX > 0 && newX <= 0) {
                logs.add(
                        new Sprite(new Vec2f(WIDTH, log.getY()), log.getSize(), log.getImage()).addVelocity(log.getVelocity())
                );
            }
            if (newX + log.getWidth() < 0) {
                logs.remove(i);
                i--;
            }
            if (oldX + log.getWidth() < WIDTH && newX + log.getWidth() >= WIDTH) {
                logs.add(
                        new Sprite(new Vec2f(0 - log.getWidth(), log.getY()), log.getSize(), log.getImage()).addVelocity(log.getVelocity())
                );
            }
            if (newX > WIDTH) {
                logs.remove(i);
                i--;
            }

            Vec2f frogPos = frog.getCenterPos();
            if (frogPos.x > log.getX() && frogPos.y > log.getY() && frogPos.x < log.getX() + log.getWidth() && frogPos.y < log.getY() + log.getHeight()) {
                logOn = log;
            }

            i++;
        }

        int height = (int)getTile(frog.getPos()).y;
        if ((height > 1 && height < 7) && logOn == null) {
            lostLife();
        }
        else {
            if (logOn != null) {
                frog.moveBy(logOn.getVelocity());
            }
        }

        Vec2f frogPos = frog.getCenterPos();
        if (frogPos.x > WIDTH || frogPos.x < 0 || frogPos.y > HEIGHT || frogPos.y < 0) {
            lostLife();
        }

        frogPos = getTile(frog.getPos());
        if (frogPos.y == 1) {
            for (int j = 0; j < HOLES.size(); j++) {
                int hole = HOLES.get(j);
                if (frogPos.x > hole - 2 && frogPos.x < hole + 1) {
                    if (isHoleFull.get(j)) {
                        lostLife();
                    }
                    else {
                        isHoleFull.set(j, true);
                        score += 5;
                        respawn();
                        boolean flag = true;
                        for (boolean checkHole : isHoleFull) {
                            if (!checkHole) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            score += 100;
                            win();
                        }
                    }
                }
            }
        }

        if (moveCooldown < 2) {
            moveCooldown++;
        }
    }

    @Override
    public int gameTickDuration() {
        return 2;
    }

    @Override
    public synchronized void die() {
        // Calling die of super
        super.die();

        // Execute everything that happens when player dies
        // here
        // Example: yourSprite.hide();
    }

    @Override
    public int maxLives() {
        return 5;
    }

    @Override
    public synchronized void render(GameGraphics graphics, int posX, int posY) {
        // Calls render of super
        super.render(graphics, posX, posY);

        // Renders all sprites and everything
        for (Sprite minecart : minecarts) {
            minecart.render(graphics, posX, posY);
        }
        for (Sprite log : logs) {
            log.render(graphics, posX, posY);
        }

        frog.render(graphics, posX, posY);
        Vec2f frogPos = frog.getPos();
        int image = 0;
        if (frog.getImage() instanceof DirectionalImage direcional) {
            image = direcional.current();
        }

        for (int i = 0; i < HOLES.size(); i++) {
            if (isHoleFull.get(i)) {
                frog.setPos(getPos(new Vec2f(HOLES.get(i) - 0.5f, 1)));
                if (frog.getImage() instanceof DirectionalImage direcional) {
                    direcional.setImage(2);
                }
                frog.render(graphics, posX, posY);
            }
        }

        frog.setPos(frogPos);
        if (frog.getImage() instanceof DirectionalImage direcional) {
            direcional.setImage(image);
        }

        // Renders particles
        renderParticles(graphics, posX, posY);

        Font font = Minecraft.getInstance().font;
        graphics.drawText(font, Component.literal(String.valueOf(score)),  posX + 2, posY + HEIGHT - font.lineHeight, 0xFFFFFF, true);

        // Renders overlay
    }
    @Override
    public synchronized void buttonDown(Button button) {
        // Calls buttonDown of super
        super.buttonDown(button);

        // Execute code when a specific button is pressed
        if (stage == GameStage.PLAYING && ticks > 5) {
            if (moveCooldown >= 2) {
                if (button == Button.UP) {
                    frog.moveBy(VecUtil.VEC_UP.multiply(TILE_SIZE));
                    if (frog.getImage() instanceof DirectionalImage image) {
                        image.setImage(0);
                    }
                    if (getTile(frog.getPos()).y < lastLine) {
                        lastLine = (int)getTile(frog.getPos()).y;
                        score++;
                    }
                    moveCooldown = 0;
                    soundPlayer.playJump();
                }
                if (button == Button.RIGHT) {
                    frog.moveBy(VecUtil.VEC_RIGHT.multiply(TILE_SIZE));
                    if (frog.getImage() instanceof DirectionalImage image) {
                        image.setImage(1);
                    }
                    moveCooldown = 0;
                    soundPlayer.playJump();
                }
                if (button == Button.DOWN) {
                    frog.moveBy(VecUtil.VEC_DOWN.multiply(TILE_SIZE));
                    if (frog.getImage() instanceof DirectionalImage image) {
                        image.setImage(2);
                    }
                    moveCooldown = 0;
                    soundPlayer.playJump();
                }
                if (button == Button.LEFT) {
                    frog.moveBy(VecUtil.VEC_LEFT.multiply(TILE_SIZE));
                    if (frog.getImage() instanceof DirectionalImage image) {
                        image.setImage(3);
                    }
                    moveCooldown = 0;
                    soundPlayer.playJump();
                }
            }
        }
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.GREEN;
    }

    @Override
    public Identifier getBackground() {
        // Change here:
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/background/froggie_background.png");
    }

    @Override
    public boolean showScore() {
        return false;
    }

    @Override
    public Component getName() {
        // Change to name of your game
        return Component.translatable("gamediscs.froggie");
    }

    @Override
    public Identifier getIcon() {
        // Change icon here:
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/item/game_disc_froggie.png");
    }
}
