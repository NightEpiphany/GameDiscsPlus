package com.moigferdsrte.gamediscs.games.graphics;

import net.minecraft.resources.Identifier;
import com.moigferdsrte.gamediscs.GameDiscs;

public class BasicParticleRenderer extends ParticleRenderer {
    private static final Identifier IMAGE = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, "textures/particle/basic_particle.png");

    public BasicParticleRenderer(ParticleColor color) {
        super(IMAGE, 2, 32, 0, color.value() * 2, 2, 2);
    }
}
