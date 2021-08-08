package me.hollow.trollgod.api.util;

import me.hollow.trollgod.api.interfaces.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;

public class CombatUtil implements Minecraftable
{
    public static EntityPlayer getTarget(final float range) {
        EntityPlayer currentTarget = null;
        for (int size = CombatUtil.mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = CombatUtil.mc.world.playerEntities.get(i);
            if (!EntityUtil.isntValid(player, range)) {
                if (currentTarget == null) {
                    currentTarget = player;
                }
                else if (CombatUtil.mc.player.getDistanceSq((Entity)player) < CombatUtil.mc.player.getDistanceSq((Entity)currentTarget)) {
                    currentTarget = player;
                }
            }
        }
        return currentTarget;
    }
    
    public static boolean isInHole(final EntityPlayer entity) {
        return isBlockValid(new BlockPos(entity.posX, entity.posY, entity.posZ));
    }
    
    public static boolean isBlockValid(final BlockPos blockPos) {
        return isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos);
    }
    
    public static int isInHoleInt(final EntityPlayer entity) {
        final BlockPos playerPos = new BlockPos(entity.getPositionVector());
        if (isBedrockHole(playerPos)) {
            return 1;
        }
        if (isObbyHole(playerPos) || isBothHole(playerPos)) {
            return 2;
        }
        return 0;
    }
    
    public static boolean isObbyHole(final BlockPos blockPos) {
        final BlockPos[] array;
        final BlockPos[] touchingBlocks = array = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
        for (final BlockPos pos : array) {
            final IBlockState touchingState = CombatUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isBedrockHole(final BlockPos blockPos) {
        final BlockPos[] array;
        final BlockPos[] touchingBlocks = array = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
        for (final BlockPos pos : array) {
            final IBlockState touchingState = CombatUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.BEDROCK) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isAir(final BlockPos pos) {
        return CombatUtil.mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }
    
    public static boolean is2x1(final BlockPos pos) {
        if (!isAir(pos) || !isAir(pos.up()) || !isAir(pos.up(2)) || isAir(pos.down())) {
            return false;
        }
        int airBlocks = 0;
        for (final EnumFacing facing : EnumFacing.HORIZONTALS) {
            final BlockPos offset = pos.offset(facing);
            if (isAir(offset) && isAir(offset.up())) {
                if (isAir(offset.down())) {
                    return false;
                }
                for (final EnumFacing offsetFacing : EnumFacing.HORIZONTALS) {
                    if (offsetFacing != facing.getOpposite() && isAir(offset.offset(offsetFacing))) {
                        return false;
                    }
                }
                ++airBlocks;
            }
            if (airBlocks > 1) {
                return false;
            }
        }
        return airBlocks == 1;
    }
    
    public static boolean isBothHole(final BlockPos blockPos) {
        final BlockPos[] array;
        final BlockPos[] touchingBlocks = array = new BlockPos[] { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
        for (final BlockPos pos : array) {
            final IBlockState touchingState = CombatUtil.mc.world.getBlockState(pos);
            if (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
        }
        return true;
    }
}
