package com.moigferdsrte.gamediscs.util.networking.payload;

import com.moigferdsrte.gamediscs.GameDiscs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record SetBestScoreC2SPayload(String game, int score) implements CustomPacketPayload {

    public static final Type<SetBestScoreC2SPayload> TYPE = new Type<>(GameDiscs.id("set_best_score"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetBestScoreC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SetBestScoreC2SPayload::game,
            ByteBufCodecs.VAR_INT,
            SetBestScoreC2SPayload::score,
            SetBestScoreC2SPayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
