package net.tejty.gamediscs.util.networking.payload;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.tejty.gamediscs.util.networking.ModMessages;

public record SetBestScoreC2SPayload(String game, int score) implements CustomPayload {

    public static final CustomPayload.Id<SetBestScoreC2SPayload> ID = new CustomPayload.Id<>(ModMessages.SET_BEST_SCORE_ID);

    public static final PacketCodec<PacketByteBuf, SetBestScoreC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING,
            SetBestScoreC2SPayload::game,
            PacketCodecs.INTEGER,
            SetBestScoreC2SPayload::score,
            SetBestScoreC2SPayload::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
