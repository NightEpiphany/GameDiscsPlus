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
import com.moigferdsrte.gamediscs.games.util.Game;
import com.moigferdsrte.gamediscs.games.util.GameStage;
import com.moigferdsrte.gamediscs.games.util.Sprite;

import java.util.ArrayList;
import java.util.List;

public class PlaneWarGame extends Game {


    private static final float PLAYER_SPEED = 3.0f;
    private static final float BULLET_SPEED = -6.0f;
    private static final int ROCK_SPAWN_INTERVAL = 30;
    private static final int ROCK_MIN_SPEED = 1;
    private static final int ROCK_MAX_SPEED = 4;


    private Sprite player;
    private final List<Sprite> rocks = new ArrayList<>();
    private final List<Sprite> bullets = new ArrayList<>();
    private int rockSpawnTimer = 0;


    private static final Identifier PLAYER_IMG = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/plane.png");
    private static final Identifier BULLET_IMG = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/bullet.png");
    private static final Identifier[] ROCK_IMAGES = {
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/rock.png"),
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/rock0.png"),
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/rock1.png"),
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/rock2.png"),
            Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/rock3.png")
    };

    public PlaneWarGame() {
        super();
    }

    @Override
    public synchronized void prepare() {
        super.prepare();


        player = new Sprite(
                new Vec2f(WIDTH / 2f - 8, HEIGHT - 30),
                new Vec2f(16, 16),
                new Image(PLAYER_IMG, 16, 16)
        );

        rocks.clear();
        bullets.clear();
        rockSpawnTimer = ROCK_SPAWN_INTERVAL;
    }

    @Override
    public synchronized void gameTick() {
        super.gameTick();

        if (stage != GameStage.PLAYING) return;

        if (controls.isButtonDown(Button.LEFT)) {
            player.moveBy(new Vec2f(-PLAYER_SPEED, 0));
        }
        if (controls.isButtonDown(Button.RIGHT)) {
            player.moveBy(new Vec2f(PLAYER_SPEED, 0));
        }


        if (player.getX() < 0) player.setX(0);
        if (player.getX() > WIDTH - player.getWidth()) {
            player.setX(WIDTH - player.getWidth());
        }


        for (int i = 0; i < bullets.size(); i++) {
            Sprite bullet = bullets.get(i);
            bullet.moveBy(new Vec2f(0, BULLET_SPEED));


            if (bullet.getY() < -bullet.getHeight()) {
                bullets.remove(i);
                i--;
            }
        }

        for (int i = 0; i < rocks.size(); i++) {
            Sprite rock = rocks.get(i);
            rock.tick();

            if (player.isTouching(rock)) {
                die();
                return;
            }

            for (int j = 0; j < bullets.size(); j++) {
                Sprite bullet = bullets.get(j);
                if (bullet.isTouching(rock)) {
                    rocks.remove(i);
                    soundPlayer.play(SoundEvents.SHIELD_BREAK.value());
                    bullets.remove(j);
                    score++; // 加分
                    soundPlayer.playPoint();
                    i--;
                    break;
                }
            }


            if (rock.getY() > HEIGHT) {
                rocks.remove(i);
                i--;
            }
        }


        if (rockSpawnTimer-- <= 0) {
            spawnRock();
            spawnRock();
            spawnRock();
            spawnRock();
            spawnRock();
            rockSpawnTimer = ROCK_SPAWN_INTERVAL;
        }
    }

    @Override
    public synchronized void buttonDown(Button button) {
        super.buttonDown(button);

        if (button == Button.BUTTON1 && stage == GameStage.PLAYING) {

            Sprite bullet = new Sprite(
                    new Vec2f(
                            player.getX() + player.getWidth() / 2f - 4,
                            player.getY() - 8
                    ),
                    new Vec2f(8, 8),
                    new Image(BULLET_IMG, 8, 8)
            );

            bullet.setVelocity(new Vec2f(0, BULLET_SPEED));
            bullets.add(bullet);
            soundPlayer.play(SoundEvents.ARROW_SHOOT);
            soundPlayer.playClick(true);
        }
    }


    private void spawnRock() {


        Identifier rockImg = ROCK_IMAGES[random.nextInt(ROCK_IMAGES.length)];


        Sprite rock = new Sprite(
                new Vec2f(random.nextInt(WIDTH - 20),
                         -20),
                        new Vec2f(16, 16),
                        new Image(rockImg, 16, 16)
                );


        rock.setVelocity(new Vec2f(
                random.nextFloat() * 2 - 1,
                random.nextInt(ROCK_MAX_SPEED - ROCK_MIN_SPEED) + ROCK_MIN_SPEED
        ));

        rocks.add(rock);
    }

    @Override
    public synchronized void render(GameGraphics graphics, int posX, int posY) {
        super.render(graphics, posX, posY);


        if (player != null) {
            player.render(graphics, posX, posY);
        }


        for (Sprite bullet : bullets) {
            bullet.render(graphics, posX, posY);
        }


        for (Sprite rock : rocks) {
            rock.render(graphics, posX, posY);
        }
    }

    @Override
    public ChatFormatting getColor() {
        return ChatFormatting.DARK_AQUA;
    }

    @Override
    public Identifier getIcon() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/item/game_disc_plane_war.png");
    }

    @Override
    public Component getName() {
        return Component.translatable("gamediscs.plane_war");
    }

    @Override
    public Identifier getBackground() {
        return Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/background/universe_background.png");
    }

    @Override
    public int scoreColor() {
        return 0xFFFF00;
    }

    @Override
    public boolean showScoreBox() {
        return false;
    }
}
