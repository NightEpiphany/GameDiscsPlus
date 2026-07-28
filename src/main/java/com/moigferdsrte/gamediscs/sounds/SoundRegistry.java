package com.moigferdsrte.gamediscs.sounds;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import com.moigferdsrte.gamediscs.GameDiscs;

public class SoundRegistry {
    public static final SoundEvent JUMP = registerSoundEvent("jump");
    public static final SoundEvent CLICK = registerSoundEvent("click");
    public static final SoundEvent POINT = registerSoundEvent("point");
    public static final SoundEvent NEW_BEST = registerSoundEvent("new_best");
    public static final SoundEvent GAME_OVER = registerSoundEvent("game_over");
    public static final SoundEvent SELECT = registerSoundEvent("select");
    public static final SoundEvent CONFIRM = registerSoundEvent("confirm");
    public static final SoundEvent EXPLOSION = registerSoundEvent("explosion");
    public static final SoundEvent SHOOT = registerSoundEvent("shoot");
    public static final SoundEvent SWING = registerSoundEvent("swing");
    public static final SoundEvent SWITCH = registerSoundEvent("switch");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(GameDiscs.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerSounds() {
        GameDiscs.LOGGER.info("Registering Sounds for " + GameDiscs.MOD_ID);
    }
}
