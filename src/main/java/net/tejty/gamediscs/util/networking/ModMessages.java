package net.tejty.gamediscs.util.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.tejty.gamediscs.GameDiscsMod;
import net.tejty.gamediscs.item.custom.GamingConsoleItem;
import net.tejty.gamediscs.util.networking.packet.SetBestScoreC2SPacket;
import net.tejty.gamediscs.util.networking.payload.SetBestScoreC2SPayload;

public class ModMessages {
    public static final Identifier SET_BEST_SCORE_ID = Identifier.of(GameDiscsMod.MOD_ID, "set_best_score");

    public static void registerC2SPackets() {

        PayloadTypeRegistry.playC2S().register(SetBestScoreC2SPayload.ID, SetBestScoreC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetBestScoreC2SPayload.ID,
                (client, ctx) -> {
                    String game = client.game();
                    int score = client.score();
                    ItemStack stack = ctx.player().getMainHandStack();
                    if (stack.getItem() instanceof GamingConsoleItem console) {
                        console.setBestScore(stack, game, score, ctx.player());
                    }
        });
    }

    public static void registerS2CPackets() {

    }
}
