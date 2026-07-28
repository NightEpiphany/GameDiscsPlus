package com.moigferdsrte.gamediscs.games.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import com.moigferdsrte.gamediscs.games.math.Vec2f;
import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.games.audio.SoundPlayer;
import com.moigferdsrte.gamediscs.games.controls.Button;
import com.moigferdsrte.gamediscs.games.controls.Controls;
import com.moigferdsrte.gamediscs.games.graphics.ParticleColor;
import com.moigferdsrte.gamediscs.games.graphics.Renderer;
import com.moigferdsrte.gamediscs.sounds.SoundRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public class Game {
    // Current stage of the game
    public GameStage stage = GameStage.START;
    // Controls of the game
    public Controls controls = new Controls(this);
    // Sound player
    public SoundPlayer soundPlayer = new SoundPlayer();
    // Dimensions of the game area
    public static final int WIDTH = 140;
    public static final int HEIGHT = 100;
    // Random
    public final Random random = new Random();
    // Tick counter
    public int ticks = 0;
    // Score counter
    public int score = 0;
    // Lives
    public int lives = maxLives();

    public int maxLives() {
        return 1;
    }

    // Particles
    private final List<Particle> particles = new ArrayList<>();
    private final GameScoreService scoreService = new GameScoreService();
    public Game() {

    }

    /**
     * Resets all variables and prepares for start
     */
    public synchronized void prepare() {
        score = 0;
        lives = maxLives();
        respawn();
    }

    /**
     * Starts the game
     */
    public synchronized void start() {
        stage = GameStage.PLAYING;
        ticks = 1;
    }

    /**
     * Stops the game, shows die screen, and sets the best score
     */
    public synchronized void die() {
        if (scoreService.submitIfNewBest(getClass(), score)) {
            soundPlayer.playNewBest();
            spawnConfetti();
        } else {
            soundPlayer.playGameOver();
        }

        // Sets game stage to DIED and resets tick counter
        stage = GameStage.DIED;
        ticks = 1;
    }

    public synchronized void lostLife() {
        lives--;
        soundPlayer.play(SoundRegistry.EXPLOSION);
        respawn();
        if (lives <= 0) {
            die();
        }
    }

    public synchronized void respawn() {
        stage = GameStage.START;
        ticks = 1;
        particles.clear();
    }

    /**
     * Stops the game, shows win screen, and sets the best score
     */
    public synchronized void win() {
        soundPlayer.playNewBest();
        spawnConfetti();
        scoreService.submitIfNewBest(getClass(), score);

        // Sets game stage to WON and resets tick counter
        stage = GameStage.WON;
        ticks = 1;
    }

    /**
     * Updates everything by one tick
     */
    public synchronized void tick() {
        // Calls gameTick depending on game stage and game tick duration
        if (stage == GameStage.PLAYING && ticks % gameTickDuration() == 0) {
            gameTick();
        }
        // Move particles
        int i = 0;
        while (i < particles.size()) {
            Particle particle = particles.get(i);
            particle.tick();
            if (particle.isDead()) {
                particles.remove(i);
                i--;
            }
            i++;
        }
        // Counts ticks
        ticks++;
    }

    /**
     * Updates everything in game by one tick
     */
    public synchronized void gameTick() {
    }

    /**
     * Renders the base game layer. The screen renders overlays in a later stratum.
     * @param graphics GameGraphics used for rendering
     * @param posX X position of the game area
     * @param posY Y position of the game area
     */
    public synchronized void render(GameGraphics graphics, int posX, int posY) {
        // Renders background
        if (getBackground() != null) {
            graphics.drawTexture(getBackground(), posX, posY, 0, 0, 0, WIDTH, HEIGHT, WIDTH, HEIGHT);
        }
    }

    /**
     * Renders all overlay things including score, die screen, and "press any key" text
     * @param graphics GameGraphics used for rendering
     * @param posX X position
     * @param posY Y position
     */
    public synchronized void renderOverlay(GameGraphics graphics, int posX, int posY) {
        // saving font
        Font font = Minecraft.getInstance().font;

        // If outside the game
        if (stage != GameStage.PLAYING) {
            // Render "press any key" text
            if (showPressAnyKey()) {
                graphics.drawText(
                        font,
                        Component.translatable("gui.gamingconsole.press_any_key"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.press_any_key").getVisualOrderText())) / 2 + 1,
                        posY + HEIGHT - 1 - font.lineHeight - 1 + (ticks % 40 <= 20 ? 0 : 1),
                        0x373737,
                        false
                );
                graphics.drawText(
                        font,
                        Component.translatable("gui.gamingconsole.press_any_key"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.press_any_key").getVisualOrderText())) / 2,
                        posY + HEIGHT - 1 - font.lineHeight - 2 + (ticks % 40 <= 20 ? 0 : 1),
                        0xFFFFFF,
                        false
                );
            }

            if (showMutiPlayerInfo()) {
                graphics.drawText(
                        font,
                        Component.translatable("gui.gamingconsole.mutiplayer"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.mutiplayer").getVisualOrderText())) / 2 + 1,
                        posY + HEIGHT - 1 - font.lineHeight - 1 + (ticks % 40 <= 20 ? 0 : 1),
                        0x373737,
                        false
                );
                graphics.drawText(
                        font,
                        Component.translatable("gui.gamingconsole.mutiplayer"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.mutiplayer").getVisualOrderText())) / 2,
                        posY + HEIGHT - 1 - font.lineHeight - 2 + (ticks % 40 <= 20 ? 0 : 1),
                        0xFFFFFF,
                        false
                );
            }

            if (showAiReactInfo()) {
                graphics.drawText(
                        font,
                        Component.translatable("gui.gamingconsole.ai_react"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.ai_react").getVisualOrderText())) / 2 + 1,
                        posY + HEIGHT - 1 - font.lineHeight - 1 + (ticks % 40 <= 20 ? 0 : 1),
                        0x373737,
                        false
                );
                graphics.drawText(
                        font,
                        Component.translatable("gui.gamingconsole.ai_react"),
                        posX + (WIDTH - font.width(Component.translatable("gui.gamingconsole.ai_react").getVisualOrderText())) / 2,
                        posY + HEIGHT - 1 - font.lineHeight - 2 + (ticks % 40 <= 20 ? 0 : 1),
                        0xFFFFFF,
                        false
                );
            }
            // Renders died / won screen
            if (stage == GameStage.DIED || stage == GameStage.WON) {

                if (this.renderScoreBoard()) {
                    // Renders score board
                    graphics.drawTexture(Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/gui/score_board.png"), posX, posY, 0, 0, 0, 140, 100, 140, 100);

                    // Component based on won or died
                    Component component = stage == GameStage.DIED ? Component.translatable("gui.gamingconsole.died").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_RED) : Component.translatable("gui.gamingconsole.won").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_GREEN);

                    // Renders the outline of the text (four times renders the same text pushed by 1px to all directions)
                    graphics.drawText(
                            font,
                            component,
                            posX + (WIDTH - font.width(component.getVisualOrderText())) / 2,
                            posY + 29,
                            Objects.requireNonNull(component.getStyle().getColor()).getValue(),
                            false
                    );
                    graphics.drawText(
                            font,
                            component,
                            posX + (WIDTH - font.width(component.getVisualOrderText())) / 2,
                            posY + 31,
                            component.getStyle().getColor().getValue(),
                            false
                    );
                    graphics.drawText(
                            font,
                            component,
                            posX + (WIDTH - font.width(component.getVisualOrderText())) / 2 + 1,
                            posY + 30,
                            component.getStyle().getColor().getValue(),
                            false
                    );
                    graphics.drawText(
                            font,
                            component,
                            posX + (WIDTH - font.width(component.getVisualOrderText())) / 2 - 1,
                            posY + 30,
                            component.getStyle().getColor().getValue(),
                            false
                    );

                    // Sets the text to be with lighter color
                    component = stage == GameStage.DIED ? Component.translatable("gui.gamingconsole.died").withStyle(ChatFormatting.BOLD, ChatFormatting.RED) : Component.translatable("gui.gamingconsole.won").withStyle(ChatFormatting.BOLD, ChatFormatting.GREEN);

                    // Renders the text
                    graphics.drawText(
                            font,
                            component,
                            posX + (WIDTH - font.width(component.getVisualOrderText())) / 2,
                            posY + 30,
                            Objects.requireNonNull(component.getStyle().getColor()).getValue(),
                            false
                    );

                    // Renders score text
                    component = Component.translatable("gui.gamingconsole.score").append(": ").append(String.valueOf(score)).withStyle(ChatFormatting.YELLOW);
                    graphics.drawText(
                            font,
                            component,
                            posX + (WIDTH - font.width(component.getVisualOrderText())) / 2,
                            posY + 35 + font.lineHeight,
                            Objects.requireNonNull(component.getStyle().getColor()).getValue(),
                            false
                    );

                    // Renders best score text
                    int bestScore = scoreService.getBestScore(getClass());
                    component = Component.translatable(score >= bestScore ? "gui.gamingconsole.new_best_score" : "gui.gamingconsole.best_score").append(": ").append(String.valueOf(bestScore)).withStyle(score >= bestScore ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
                    graphics.drawText(
                            font,
                            component,
                            posX + (WIDTH - font.width(component.getVisualOrderText())) / 2,
                            posY + 50 + font.lineHeight,
                            Objects.requireNonNull(component.getStyle().getColor()).getValue(),
                            false
                    );
                }
            }
        }
        else {
            // If current game has score box, it renders it
            if (showScoreBox() && showScore()) {
                if (useLongScoreBox()) graphics.drawTexture(Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/gui/long_score_box.png"), posX, posY, 0, 0, 0, 140, 100, 140, 100);
                else graphics.drawTexture(Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/gui/score_box.png"), posX, posY, 0, 0, 0, 140, 100, 140, 100);
            }

            if (showScore()) {
                // Renders score
                graphics.drawText(
                        font,
                        (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(score)),
                        posX + 2,
                        posY + 2,
                        0x373737,
                        false
                );
                graphics.drawText(
                        font,
                        (scoreText() ? Component.translatable("gui.gamingconsole.score").append(": ") : Component.empty()).append(String.valueOf(score)),
                        posX + 1,
                        posY + 1,
                        scoreColor(),
                        false
                );
            }
        }

        for (Particle particle : particles) {
            if (particle.isForOverlay()) {
                particle.render(graphics, posX, posY, stage);
            }
        }
    }

    public synchronized void renderParticles(GameGraphics graphics, int posX, int posY) {
        for (Particle particle : particles) {
            particle.render(graphics, posX, posY, stage);
        }
    }

    /**
     * On button down
     * @param button The button that was pressed
     */
    public synchronized void buttonDown(Button button) {
        soundPlayer.playClick(true);
        // Starts the game
        if ((stage == GameStage.START || stage == GameStage.RETRY) && ticks > 8) {
            start();
        }
        // Prepares the game
        else if ((stage == GameStage.WON || stage == GameStage.DIED) && ticks > 8) {
            prepare();
        }
    }

    public Particle addParticle(Particle particle) {
        particles.add(particle);
        return particle;
    }
    public void spawnParticleExplosion(Supplier<Renderer> renderer, Vec2f pos, int count, int speed, int lifetime, ParticleLevel level) {
        for (int i = 0; i < count; i++) {
            Particle particle = new Particle(pos, renderer.get(), random.nextInt(lifetime / 2, lifetime), level);
            particle.setVelocity(new Vec2f(random.nextFloat(-speed, speed), random.nextFloat(-speed, speed)));
            particles.add(particle);
        }
    }
    public void spawnParticleExplosion(Vec2f pos, int count, int speed, int lifetime, ParticleLevel level) {
        soundPlayer.play(SoundEvents.GENERIC_EXPLODE.value(), 1.5f, 0.1f);
        for (int i = 0; i < count; i++) {
            Particle particle = new ExplosionParticle(pos, random.nextInt(lifetime / 2, lifetime), level);
            particle.setVelocity(new Vec2f(random.nextFloat(-speed, speed), random.nextFloat(-speed, speed)));
            particles.add(particle);
        }
    }

    public void spawnConfetti() {
        for (int i = 0; i < 30; i++) {
            Particle particle = new ConfettiParticle(new Vec2f(0, HEIGHT), ParticleColor.random(random), random.nextInt(50, 70), ParticleLevel.OVERLAY);
            particle.setVelocity(new Vec2f(random.nextFloat(1, 10), random.nextFloat(-25, -10)));
            particles.add(particle);
        }
        for (int i = 0; i < 30; i++) {
            Particle particle = new ConfettiParticle(new Vec2f(WIDTH, HEIGHT), ParticleColor.random(random), random.nextInt(50, 70), ParticleLevel.OVERLAY);
            particle.setVelocity(new Vec2f(random.nextFloat(-10, -1), random.nextFloat(-25, -10)));
            particles.add(particle);
        }
    }

    /**
     * On button up
     * @param button The button that was released
     */
    public synchronized void buttonUp(Button button) {
        soundPlayer.playClick(false);
    }

    /**
     * @return Game tickt duration
     */
    public int gameTickDuration() {
        return 1; // Determines how often the game ticks (1 game tick duration = 20 game ticks per second, 2 game tick duration = 10 game ticks per second)
    }

    /**
     * @return Resource location of background image
     */
    public Identifier getBackground() {
        return null;
    }

    /**
     * @return Determines if this game shows score box
     */
    public boolean showScoreBox() {
        return true;
    }

    public boolean useLongScoreBox() {
        return false;
    }

    public boolean showScore() {
        return true;
    }

    public boolean showPressAnyKey() {
        return true;
    }

    public boolean showAiReactInfo() {
        return false;
    }

    public boolean showMutiPlayerInfo() {
        return false;
    }

    public boolean renderScoreBoard() {
        return true;
    }

    /**
     * @return Color of score
     */
    public int scoreColor() {
        return 0xFFFFFF; // Default is white
    }

    /**
     * @return Determines if score will be rendered with "Score:" before the number
     */
    public boolean scoreText() {
        return true; // Default is true
    }

    /**
     * @return Display name of the game
     */
    public Component getName() {return Component.empty();}

    /**
     * @return Resource location to icon of the game
     */
    public Identifier getIcon() {return null;}

    /**
     * @return Display color of the game
     */
    public ChatFormatting getColor() {return ChatFormatting.YELLOW;}

    /**
     * @return True if the game is an empty game (default Game, not its child), false otherwise
     */
    public boolean isEmpty() {
        return this.getClass().equals(Game.class);
    }
}
