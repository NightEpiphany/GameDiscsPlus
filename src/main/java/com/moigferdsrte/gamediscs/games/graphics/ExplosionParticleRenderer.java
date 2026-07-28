package com.moigferdsrte.gamediscs.games.graphics;

import com.moigferdsrte.gamediscs.client.render.GameGraphics;
import net.minecraft.resources.Identifier;
import com.moigferdsrte.gamediscs.GameDiscs;
import com.moigferdsrte.gamediscs.games.util.Particle;

public class ExplosionParticleRenderer extends Renderer {
    private final MultiImage image;
    private final Particle particle;

    public ExplosionParticleRenderer(Particle particle) {
        image = new MultiImage(Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/games/sprite/explosion.png"), 2, 16, 8);
        this.particle = particle;
    }

    @Override
    public void render(GameGraphics graphics, int posX, int posY) {
        image.setImage((int)((Math.sqrt(particle.getVelocity().lengthSquared())) * 4)).render(graphics, posX, posY);
    }
}
