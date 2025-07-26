package net.tejty.gamediscs.item.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tejty.gamediscs.GameDiscsMod;
import net.tejty.gamediscs.client.ClientUtils;

import java.util.Objects;

public class GamingConsoleItem extends Item {
    public GamingConsoleItem(Settings properties) {
        super(properties.maxCount(1));
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            ClientUtils.openConsoleScreen();
        }

        return super.use(world, user, hand);
    }

    public void setBestScore(ItemStack stack, String game, int score, PlayerEntity player) {
//        if (!stack.hasNbt()){
//            stack.setNbt(new NbtCompound());
//        }
//        NbtCompound nbtData = stack.getNbt();
//        nbtData.putInt(GameDiscsMod.MOD_ID + ":" + game + ";" + player.getDisplayName().getString(), score);
//        stack.setNbt(nbtData);
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {return;}
        if (!nbtComponent.isEmpty()) {
            NbtCompound nbtData = new NbtCompound();
            nbtData.putInt(GameDiscsMod.MOD_ID + ":" + game + ";" + Objects.requireNonNull(player.getDisplayName()).getString(), score);
            NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbtData);
        }else {
            NbtCompound nbtData = new NbtCompound();
            NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbtData);
        }

        // TODO bestScore
    }

    public static int getBestScore(ItemStack stack, String game, PlayerEntity player) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) {return 0;}
        if (nbtComponent.isEmpty()) {
            return 0;
        }
        else {
             return nbtComponent.getNbt().getInt(GameDiscsMod.MOD_ID + ":" + game + ";" + Objects.requireNonNull(player.getDisplayName()).getString());
            //return nbtComponent..getInt(GameDiscsMod.MOD_ID + ":" + game + ";" + player.getDisplayName().getString());
        }
    }
}
