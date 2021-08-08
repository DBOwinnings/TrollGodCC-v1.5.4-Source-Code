package me.hollow.trollgod.api.util;

import me.hollow.trollgod.api.interfaces.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import me.hollow.trollgod.client.modules.client.*;
import net.minecraft.network.play.client.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import java.util.*;
import net.minecraft.block.*;
import java.math.*;
import net.minecraft.entity.item.*;

public final class BlockUtil implements Minecraftable
{
    public static final Vec3d[] platformOffsetList;
    public static final Vec3d[] legOffsetList;
    public static final Vec3d[] offsetList;
    public static Vec3d[] holeOffsets;
    
    public static boolean placeBlock(final BlockPos pos) {
        if (!BlockUtil.mc.world.getBlockState(pos).getBlock().isReplaceable((IBlockAccess)BlockUtil.mc.world, pos)) {
            return false;
        }
        final boolean alreadySneaking = BlockUtil.mc.player.isSneaking();
        for (int i = 0; i < 6; ++i) {
            final EnumFacing side = EnumFacing.values()[i];
            final IBlockState offsetState = BlockUtil.mc.world.getBlockState(pos.offset(side));
            if (offsetState.getBlock().canCollideCheck(offsetState, false)) {
                if (!offsetState.getMaterial().isReplaceable()) {
                    final boolean activated = offsetState.getBlock().onBlockActivated((World)BlockUtil.mc.world, pos, BlockUtil.mc.world.getBlockState(pos), (EntityPlayer)BlockUtil.mc.player, EnumHand.MAIN_HAND, side, 0.5f, 0.5f, 0.5f);
                    if (activated && !alreadySneaking) {
                        BlockUtil.mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)BlockUtil.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    }
                    BlockUtil.mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos.offset(side), side.getOpposite(), EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                    if (Manage.INSTANCE.placeSwing.getValue()) {
                        BlockUtil.mc.getConnection().sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
                    }
                    if (activated && !alreadySneaking) {
                        BlockUtil.mc.getConnection().sendPacket((Packet)new CPacketEntityAction((Entity)BlockUtil.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean check) {
        if (BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN) {
            return false;
        }
        final BlockPos boost = blockPos.add(0, 1, 0);
        return BlockUtil.mc.world.getBlockState(boost).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(0, 2, 0)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB((double)boost.getX(), (double)boost.getY(), (double)boost.getZ(), (double)(boost.getX() + 1), (double)(boost.getY() + (check ? 2 : 1)), (double)(boost.getZ() + 1)), e -> !(e instanceof EntityEnderCrystal)).size() == 0;
    }
    
    public static Vec3d[] convertVec3ds(final Vec3d vec3d, final Vec3d[] input) {
        final Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }
    
    public static List<Vec3d> targets(final Vec3d vec3d, final boolean feet) {
        final List<Vec3d> placeTargets = new ArrayList<Vec3d>();
        Collections.addAll(placeTargets, convertVec3ds(vec3d, BlockUtil.platformOffsetList));
        if (feet) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, BlockUtil.legOffsetList));
        }
        Collections.addAll(placeTargets, convertVec3ds(vec3d, BlockUtil.offsetList));
        final List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2);
        if (vec3ds.size() == 4) {
            for (final Vec3d vector : vec3ds) {
                final BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                switch (isPositionPlaceable(position, true)) {
                    case -1:
                    case 1:
                    case 2: {
                        continue;
                    }
                    case 3: {
                        placeTargets.add(vec3d.add(vector));
                        break;
                    }
                }
                break;
            }
        }
        return placeTargets;
    }
    
    public static List<BlockPos> getSphere(final float radius, final boolean ignoreAir) {
        final List<BlockPos> sphere = new ArrayList<BlockPos>();
        final BlockPos pos = new BlockPos(BlockUtil.mc.player.getPositionVector());
        final int posX = pos.getX();
        final int posY = pos.getY();
        final int posZ = pos.getZ();
        final int radiuss = (int)radius;
        for (int x = posX - radiuss; x <= posX + radius; ++x) {
            for (int z = posZ - radiuss; z <= posZ + radius; ++z) {
                for (int y = posY - radiuss; y < posY + radius; ++y) {
                    if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                        final BlockPos position = new BlockPos(x, y, z);
                        if (!ignoreAir || BlockUtil.mc.world.getBlockState(position).getBlock() != Blocks.AIR) {
                            sphere.add(position);
                        }
                    }
                }
            }
        }
        return sphere;
    }
    
    public static List<Vec3d> getUnsafeBlocks(final Entity entity, final int height) {
        return getUnsafeBlocksFromVec3d(entity.getPositionVector(), height);
    }
    
    public static List<EnumFacing> getPossibleSides(final BlockPos pos) {
        final List<EnumFacing> facings = new ArrayList<EnumFacing>(6);
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.offset(side);
            if (BlockUtil.mc.world.getBlockState(neighbour).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(neighbour), false)) {
                final IBlockState blockState = BlockUtil.mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    facings.add(side);
                }
            }
        }
        return facings;
    }
    
    public static Vec3d[] getHelpingBlocks(final Vec3d vec3d) {
        return new Vec3d[] { new Vec3d(vec3d.x, vec3d.y - 1.0, vec3d.z), new Vec3d((vec3d.x != 0.0) ? (vec3d.x * 2.0) : vec3d.x, vec3d.y, (vec3d.x != 0.0) ? vec3d.z : (vec3d.z * 2.0)), new Vec3d((vec3d.x == 0.0) ? (vec3d.x + 1.0) : vec3d.x, vec3d.y, (vec3d.x == 0.0) ? vec3d.z : (vec3d.z + 1.0)), new Vec3d((vec3d.x == 0.0) ? (vec3d.x - 1.0) : vec3d.x, vec3d.y, (vec3d.x == 0.0) ? vec3d.z : (vec3d.z - 1.0)), new Vec3d(vec3d.x, vec3d.y + 1.0, vec3d.z) };
    }
    
    public static BlockPos[] toBlockPos(final Vec3d[] vec3ds) {
        final BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }
    
    public static boolean isSafe(final Entity entity, final int height) {
        return getUnsafeBlocks(entity, height).size() == 0;
    }
    
    public static int isPositionPlaceable(final BlockPos pos, final boolean entityCheck) {
        final Block block = BlockUtil.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
        }
        if (entityCheck) {
            for (final Entity entity : BlockUtil.mc.world.getEntitiesWithinAABB((Class)Entity.class, new AxisAlignedBB(pos))) {
                if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                    return 1;
                }
            }
        }
        for (final EnumFacing side : getPossibleSides(pos)) {
            if (canBeClicked(pos.offset(side))) {
                return 3;
            }
        }
        return 2;
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock().canCollideCheck(BlockUtil.mc.world.getBlockState(pos), false);
    }
    
    public static BlockPos getRoundedBlockPos(final Entity entity) {
        return new BlockPos(roundVec(entity.getPositionVector(), 0));
    }
    
    public static Vec3d roundVec(final Vec3d vec3d, final int places) {
        return new Vec3d(round(vec3d.x, places), round(vec3d.y, places), round(vec3d.z, places));
    }
    
    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }
    
    public static List<Vec3d> getUnsafeBlocksFromVec3d(final Vec3d pos, final int height) {
        final List<Vec3d> vec3ds = new ArrayList<Vec3d>(4);
        for (final Vec3d vector : getOffsets(height)) {
            final BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            final Block block = BlockUtil.mc.world.getBlockState(targetPos).getBlock();
            if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }
    
    public static Vec3d[] getUnsafeBlockArray(final Entity entity, final int height) {
        final List<Vec3d> list = getUnsafeBlocks(entity, height);
        final Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }
    
    public static Vec3d[] getUnsafeBlockArrayFromVec3d(final Vec3d pos, final int height) {
        final List<Vec3d> list = getUnsafeBlocksFromVec3d(pos, height);
        final Vec3d[] array = new Vec3d[list.size()];
        return list.toArray(array);
    }
    
    public static List<Vec3d> getOffsetList(final int y) {
        final List<Vec3d> offsets = new ArrayList<Vec3d>(4);
        offsets.add(new Vec3d(-1.0, (double)y, 0.0));
        offsets.add(new Vec3d(1.0, (double)y, 0.0));
        offsets.add(new Vec3d(0.0, (double)y, -1.0));
        offsets.add(new Vec3d(0.0, (double)y, 1.0));
        return offsets;
    }
    
    public static Vec3d[] getOffsets(final int y) {
        final List<Vec3d> offsets = getOffsetList(y);
        final Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    
    static {
        platformOffsetList = new Vec3d[] { new Vec3d(0.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(1.0, -1.0, 0.0) };
        legOffsetList = new Vec3d[] { new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0) };
        offsetList = new Vec3d[] { new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0), new Vec3d(0.0, 2.0, 0.0) };
        BlockUtil.holeOffsets = new Vec3d[] { new Vec3d(-1.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(0.0, -1.0, 0.0) };
    }
}
