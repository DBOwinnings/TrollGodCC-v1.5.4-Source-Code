/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.hollow.trollgod.api.interfaces.Minecraftable;
import me.hollow.trollgod.client.modules.client.Manage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockUtil
        implements Minecraftable {
    public static final Vec3d[] platformOffsetList = new Vec3d[]{new Vec3d(0.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(1.0, -1.0, 0.0)};
    public static final Vec3d[] legOffsetList = new Vec3d[]{new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0)};
    public static final Vec3d[] offsetList = new Vec3d[]{new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, 0.0)};
    public static Vec3d[] holeOffsets = new Vec3d[]{new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, -1.0, 0.0)};

    public static boolean placeBlock(BlockPos pos) {
        if (!BlockUtil.mc.world.getBlockState(pos).getBlock().isReplaceable((IBlockAccess)BlockUtil.mc.world, pos)) {
            return false;
        }
        boolean alreadySneaking = BlockUtil.mc.player.isSneaking();
        for (int i = 0; i < 6; ++i) {
            EnumFacing side = EnumFacing.values()[i];
            IBlockState offsetState = BlockUtil.mc.world.getBlockState(pos.offset(side));
            if (!offsetState.getBlock().canCollideCheck(offsetState, false) || offsetState.getMaterial().isReplaceable()) continue;
            boolean activated = offsetState.getBlock().onBlockActivated((World)BlockUtil.mc.world, pos, BlockUtil.mc.world.getBlockState(pos), (EntityPlayer)BlockUtil.mc.player, EnumHand.MAIN_HAND, side, 0.5f, 0.5f, 0.5f);
            if (activated && !alreadySneaking) {
                mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }
            mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos.offset(side), side.getOpposite(), EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if (Manage.INSTANCE.placeSwing.getValue().booleanValue()) {
                mc.getConnection().sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
            }
            if (!activated || alreadySneaking) continue;
            mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
        return true;
    }

    public static boolean canPlaceCrystal(BlockPos blockPos, boolean check) {
        if (BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            return false;
        }
        BlockPos boost = blockPos.add(0, 1, 0);
        if (BlockUtil.mc.world.getBlockState(boost).getBlock() != Blocks.AIR || BlockUtil.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() != Blocks.AIR) {
            return false;
        }
        return BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB((double)boost.getX(), (double)boost.getY(), (double)boost.getZ(), (double)(boost.getX() + 1), (double)(boost.getY() + (check ? 2 : 1)), (double)(boost.getZ() + 1)), e -> !(e instanceof EntityEnderCrystal)).size() == 0;
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static List<Vec3d> targets(Vec3d vec3d, boolean feet) {
        ArrayList<Vec3d> placeTargets = new ArrayList<Vec3d>();
        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, platformOffsetList));
        if (feet) {
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, legOffsetList));
        }
        Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, offsetList));
        List<Vec3d> vec3ds = BlockUtil.getUnsafeBlocksFromVec3d(vec3d, 2);
        if (vec3ds.size() == 4) {
            block5: for (Vec3d vector : vec3ds) {
                BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                switch (BlockUtil.isPositionPlaceable(position, true)) {
                    case 0: {
                        break;
                    }
                    case -1:
                    case 1:
                    case 2: {
                        continue block5;
                    }
                    case 3: {
                        placeTargets.add(vec3d.add(vector));
                    }
                }
                break;
            }
        }
        return placeTargets;
    }

    public static List<BlockPos> getSphere(float radius, boolean ignoreAir) {
        ArrayList<BlockPos> sphere = new ArrayList<BlockPos>();
        BlockPos pos = new BlockPos(BlockUtil.mc.player.getPositionVector());
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int radiuss = (int)radius;
        int x = posX - radiuss;
        while ((float)x <= (float)posX + radius) {
            int z = posZ - radiuss;
            while ((float)z <= (float)posZ + radius) {
                int y = posY - radiuss;
                while ((float)y < (float)posY + radius) {
                    if ((float)((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y)) < radius * radius) {
                        BlockPos position = new BlockPos(x, y, z);
                        if (!ignoreAir || BlockUtil.mc.world.getBlockState(position).getBlock() != Blocks.AIR) {
                            sphere.add(position);
                        }
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return sphere;
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height) {
        return BlockUtil.getUnsafeBlocksFromVec3d(entity.getPositionVector(), height);
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>(6);
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false) || (blockState = BlockUtil.mc.world.getBlockState(neighbour)).getMaterial().isReplaceable()) continue;
            facings.add(side);
        }
        return facings;
    }

    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[]{new Vec3d(vec3d.x, vec3d.y - 1.0, vec3d.z), new Vec3d(vec3d.x != 0.0 ? vec3d.x * 2.0 : vec3d.x, vec3d.y, vec3d.x != 0.0 ? vec3d.z : vec3d.z * 2.0), new Vec3d(vec3d.x == 0.0 ? vec3d.x + 1.0 : vec3d.x, vec3d.y, vec3d.x == 0.0 ? vec3d.z : vec3d.z + 1.0), new Vec3d(vec3d.x == 0.0 ? vec3d.x - 1.0 : vec3d.x, vec3d.y, vec3d.x == 0.0 ? vec3d.z : vec3d.z - 1.0), new Vec3d(vec3d.x, vec3d.y + 1.0, vec3d.z)};
    }

    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }

    public static boolean isSafe(Entity entity, int height) {
        return BlockUtil.getUnsafeBlocks(entity, height).size() == 0;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean entityCheck) {
        Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        if (entityCheck) {
            for (Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return 1;
            }
        }
        for (EnumFacing side : BlockUtil.getPossibleSides(pos)) {
            if (!BlockUtil.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static boolean canBeClicked(BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(pos), false);
    }

    public static BlockPos getRoundedBlockPos(Entity entity) {
        return new BlockPos(BlockUtil.roundVec(entity.getPositionVector(), 0));
    }

    public static Vec3d roundVec(Vec3d vec3d, int places) {
        return new Vec3d(BlockUtil.round(vec3d.x, places), BlockUtil.round(vec3d.y, places), BlockUtil.round(vec3d.z, places));
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>(4);
        for (Vec3d vector : BlockUtil.getOffsets(height)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = BlockUtil.mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }

    public static Vec3d[] getUnsafeBlockArray(Entity entity, int height) {
        List<Vec3d> list = BlockUtil.getUnsafeBlocks(entity, height);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static Vec3d[] getUnsafeBlockArrayFromVec3d(Vec3d pos, int height) {
        List<Vec3d> list = BlockUtil.getUnsafeBlocksFromVec3d(pos, height);
        Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }

    public static List<Vec3d> getOffsetList(int y) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>(4);
        offsets.add(new Vec3d(-1.0, (double)y, 0.0));
        offsets.add(new Vec3d(1.0, (double)y, 0.0));
        offsets.add(new Vec3d(0.0, (double)y, -1.0));
        offsets.add(new Vec3d(0.0, (double)y, 1.0));
        return offsets;
    }

    public static Vec3d[] getOffsets(int y) {
        List<Vec3d> offsets = BlockUtil.getOffsetList(y);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
}

