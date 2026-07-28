package com.moigferdsrte.gamediscs.games.util;

import com.moigferdsrte.gamediscs.games.math.Vec2f;
import com.moigferdsrte.gamediscs.games.graphics.ParticleColor;

import java.util.Random;

public class ConfettiParticle extends Particle {
    private final Random random = new Random();
    public ConfettiParticle(Vec2f pos, ParticleColor color, int lifetime, ParticleLevel level) {
        super(pos, color, lifetime, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.setVelocity(getVelocity().multiply(0.8f));
        this.addVelocity(new Vec2f(random.nextFloat(-0.2f, 0.2f), 0.3f));
    }
}
