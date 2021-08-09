/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.combat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.BlockUtil;
import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.api.util.Timer;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@ModuleManifest(label="AutoFeetPlace", listen=false, category=Module.Category.COMBAT, color=-5602)
public class AutoFeetPlace
extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay/Place", 50, 0, 250));
    private final Setting<Integer> blocksPerTick = this.register(new Setting<Integer>("Block/Place", 8, 1, 20));
    private final Setting<Boolean> raytrace = this.register(new Setting<Boolean>("Raytrace", false));
    private final Setting<Boolean> helpingBlocks = this.register(new Setting<Boolean>("HelpingBlocks", true));
    private final Setting<Boolean> intelligent = this.register(new Setting<Object>("Intelligent", false, v -> this.helpingBlocks.getValue()));
    private final Setting<Boolean> antiPedo = this.register(new Setting<Boolean>("Always Help", false));
    private final Setting<Boolean> echests = this.register(new Setting<Boolean>("E-Chests", false));
    private final Setting<Integer> retryer = this.register(new Setting<Integer>("Retries", 4, 0, 15));
    private final Setting<Integer> extensions = this.register(new Setting<Integer>("Extensions", 1, 0, 3));
    private final Timer timer = new Timer();
    private final Timer retryTimer = new Timer();
    private boolean didPlace = false;
    public static AutoFeetPlace INSTANCE;
    private int placements;
    public static boolean isPlacing;
    private int obbySlot;
    private int extenders;
    private final Map<BlockPos, Integer> retries;
    private final Set<Vec3d> extendingBlocks;
    private double enabledPos;

    public AutoFeetPlace() {
        INSTANCE = this;
        this.obbySlot = -1;
        this.retries = new HashMap<BlockPos, Integer>();
        this.extendingBlocks = new HashSet<Vec3d>();
    }

    @Override
    public void onEnable() {
        if (this.isNull()) {
            this.disable();
            return;
        }
        this.enabledPos = this.mc.player.posY;
        this.retries.clear();
        this.retryTimer.reset();
    }

    public void doPlace() {
        if (this.check()) {
            return;
        }
        Block playerBlock = this.mc.world.getBlockState(new BlockPos(this.mc.player.getPositionVector())).getBlock();
        boolean offset = playerBlock == Blocks.ENDER_CHEST && this.mc.player.posY - (double)((int)this.mc.player.posY) > 0.5;
        if (!BlockUtil.isSafe((Entity)this.mc.player, offset ? 1 : 0)) {
            this.placeBlocks(this.mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray((Entity)this.mc.player, offset ? 1 : 0), this.helpingBlocks.getValue(), false, false);
        } else if (!BlockUtil.isSafe((Entity)this.mc.player, offset ? 0 : -1) && this.antiPedo.getValue().booleanValue()) {
            this.placeBlocks(this.mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray((Entity)this.mc.player, offset ? 0 : -1), false, false, false);
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }

    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < this.extensions.getValue()) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            Iterator<Vec3d> iterator = this.extendingBlocks.iterator();
            while (iterator.hasNext()) {
                Vec3d vec3d;
                array[i] = vec3d = iterator.next();
                ++i;
            }
            int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), BlockUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0), this.helpingBlocks.getValue(), false, true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        } else if (this.extendingBlocks.size() > 2 || this.extenders >= this.extensions.getValue()) {
            this.extendingBlocks.clear();
        }
    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        for (Vec3d vec3d : vec3ds) {
            for (Vec3d pos : BlockUtil.getUnsafeBlockArray((Entity)this.mc.player, 0)) {
                if (!vec3d.equals((Object)pos)) continue;
                ++matches;
            }
        }
        if (matches == 2) {
            return this.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }

    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
        int helpings = 0;
        boolean gotHelp = true;
        if (this.obbySlot == -1) {
            return false;
        }
        int lastSlot = this.mc.player.inventory.currentItem;
        this.mc.player.inventory.currentItem = this.obbySlot;
        this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.obbySlot));
        block6: for (Vec3d vec3d : vec3ds) {
            gotHelp = true;
            if (isHelping && !this.intelligent.getValue().booleanValue() && ++helpings > 1) {
                return false;
            }
            BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            switch (BlockUtil.isPositionPlaceable(position, true)) {
                case -1: {
                    continue block6;
                }
                case 1: {
                    if (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue()) {
                        this.placeBlock(position);
                        this.retries.put(position, this.retries.get(position) == null ? 1 : this.retries.get(position) + 1);
                        this.retryTimer.reset();
                        continue block6;
                    }
                    if (isExtending || this.extenders >= this.extensions.getValue()) continue block6;
                    this.placeBlocks(this.mc.player.getPositionVector().add(vec3d), BlockUtil.getUnsafeBlockArrayFromVec3d(this.mc.player.getPositionVector().add(vec3d), 0), hasHelpingBlocks, false, true);
                    this.extendingBlocks.add(vec3d);
                    ++this.extenders;
                    continue block6;
                }
                case 2: {
                    if (!hasHelpingBlocks) continue block6;
                    gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                }
                case 3: {
                    if (gotHelp) {
                        this.placeBlock(position);
                    }
                    if (!isHelping) continue block6;
                    return true;
                }
            }
        }
        this.mc.player.inventory.currentItem = lastSlot;
        this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(lastSlot));
        return false;
    }

    private boolean check() {
        if (this.isNull()) {
            this.disable();
            return true;
        }
        if (this.mc.player.posY > this.enabledPos) {
            this.disable();
            return true;
        }
        isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        this.obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        int echestSlot = ItemUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
        if (!this.isEnabled()) {
            return true;
        }
        if (this.retryTimer.hasReached(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (!(this.obbySlot != -1 || this.echests.getValue().booleanValue() && echestSlot != -1)) {
            this.disable();
            return true;
        }
        return !this.timer.hasReached(this.delay.getValue().intValue());
    }

    private void placeBlock(BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValue()) {
            isPlacing = true;
            BlockUtil.placeBlock(pos);
            this.didPlace = true;
            ++this.placements;
        }
    }

    static {
        isPlacing = false;
    }

    public static enum MovementMode {
        NONE,
        STATIC,
        LIMIT,
        OFF;

    }
}

