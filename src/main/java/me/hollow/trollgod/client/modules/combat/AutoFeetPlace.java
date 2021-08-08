package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;
import net.minecraft.block.*;
import java.util.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import me.hollow.trollgod.api.util.*;

@ModuleManifest(label = "AutoFeetPlace", listen = false, category = Category.COMBAT, color = -5602)
public class AutoFeetPlace extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> blocksPerTick;
    private final Setting<Boolean> raytrace;
    private final Setting<Boolean> helpingBlocks;
    private final Setting<Boolean> intelligent;
    private final Setting<Boolean> antiPedo;
    private final Setting<Boolean> echests;
    private final Setting<Integer> retryer;
    private final Setting<Integer> extensions;
    private final Timer timer;
    private final Timer retryTimer;
    private boolean didPlace;
    public static AutoFeetPlace INSTANCE;
    private int placements;
    public static boolean isPlacing;
    private int obbySlot;
    private int extenders;
    private final Map<BlockPos, Integer> retries;
    private final Set<Vec3d> extendingBlocks;
    private double enabledPos;
    
    public AutoFeetPlace() {
        this.delay = (Setting<Integer>)this.register(new Setting("Delay/Place", (T)50, (T)0, (T)250));
        this.blocksPerTick = (Setting<Integer>)this.register(new Setting("Block/Place", (T)8, (T)1, (T)20));
        this.raytrace = (Setting<Boolean>)this.register(new Setting("Raytrace", (T)false));
        this.helpingBlocks = (Setting<Boolean>)this.register(new Setting("HelpingBlocks", (T)true));
        this.intelligent = (Setting<Boolean>)this.register(new Setting("Intelligent", (T)false, v -> this.helpingBlocks.getValue()));
        this.antiPedo = (Setting<Boolean>)this.register(new Setting("Always Help", (T)false));
        this.echests = (Setting<Boolean>)this.register(new Setting("E-Chests", (T)false));
        this.retryer = (Setting<Integer>)this.register(new Setting("Retries", (T)4, (T)0, (T)15));
        this.extensions = (Setting<Integer>)this.register(new Setting("Extensions", (T)1, (T)0, (T)3));
        this.timer = new Timer();
        this.retryTimer = new Timer();
        this.didPlace = false;
        AutoFeetPlace.INSTANCE = this;
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
        final Block playerBlock = this.mc.world.getBlockState(new BlockPos(this.mc.player.getPositionVector())).getBlock();
        final boolean offset = playerBlock == Blocks.ENDER_CHEST && this.mc.player.posY - (int)this.mc.player.posY > 0.5;
        if (!BlockUtil.isSafe((Entity)this.mc.player, offset ? 1 : 0)) {
            this.placeBlocks(this.mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray((Entity)this.mc.player, (int)(offset ? 1 : 0)), this.helpingBlocks.getValue(), false, false);
        }
        else if (!BlockUtil.isSafe((Entity)this.mc.player, offset ? 0 : -1) && this.antiPedo.getValue()) {
            this.placeBlocks(this.mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray((Entity)this.mc.player, offset ? 0 : -1), false, false, false);
        }
        this.processExtendingBlocks();
        if (this.didPlace) {
            this.timer.reset();
        }
    }
    
    private void processExtendingBlocks() {
        if (this.extendingBlocks.size() == 2 && this.extenders < this.extensions.getValue()) {
            final Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (final Vec3d vec3d : this.extendingBlocks) {
                array[i] = vec3d;
                ++i;
            }
            final int placementsBefore = this.placements;
            if (this.areClose(array) != null) {
                this.placeBlocks(this.areClose(array), BlockUtil.getUnsafeBlockArrayFromVec3d(this.areClose(array), 0), this.helpingBlocks.getValue(), false, true);
            }
            if (placementsBefore < this.placements) {
                this.extendingBlocks.clear();
            }
        }
        else if (this.extendingBlocks.size() > 2 || this.extenders >= this.extensions.getValue()) {
            this.extendingBlocks.clear();
        }
    }
    
    private Vec3d areClose(final Vec3d[] vec3ds) {
        int matches = 0;
        for (final Vec3d vec3d : vec3ds) {
            for (final Vec3d pos : BlockUtil.getUnsafeBlockArray((Entity)this.mc.player, 0)) {
                if (vec3d.equals((Object)pos)) {
                    ++matches;
                }
            }
        }
        if (matches == 2) {
            return this.mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1]));
        }
        return null;
    }
    
    private boolean placeBlocks(final Vec3d pos, final Vec3d[] vec3ds, final boolean hasHelpingBlocks, final boolean isHelping, final boolean isExtending) {
        int helpings = 0;
        boolean gotHelp = true;
        if (this.obbySlot == -1) {
            return false;
        }
        final int lastSlot = this.mc.player.inventory.currentItem;
        this.mc.player.inventory.currentItem = this.obbySlot;
        this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.obbySlot));
        for (final Vec3d vec3d : vec3ds) {
            gotHelp = true;
            ++helpings;
            if (isHelping && !this.intelligent.getValue() && helpings > 1) {
                return false;
            }
            final BlockPos position = new BlockPos(pos).add(vec3d.x, vec3d.y, vec3d.z);
            Label_0425: {
                switch (BlockUtil.isPositionPlaceable(position, true)) {
                    case 1: {
                        if (this.retries.get(position) == null || this.retries.get(position) < this.retryer.getValue()) {
                            this.placeBlock(position);
                            this.retries.put(position, (this.retries.get(position) == null) ? 1 : (this.retries.get(position) + 1));
                            this.retryTimer.reset();
                            break;
                        }
                        if (!isExtending && this.extenders < this.extensions.getValue()) {
                            this.placeBlocks(this.mc.player.getPositionVector().add(vec3d), BlockUtil.getUnsafeBlockArrayFromVec3d(this.mc.player.getPositionVector().add(vec3d), 0), hasHelpingBlocks, false, true);
                            this.extendingBlocks.add(vec3d);
                            ++this.extenders;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (hasHelpingBlocks) {
                            gotHelp = this.placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                            break Label_0425;
                        }
                        break;
                    }
                    case 3: {
                        if (gotHelp) {
                            this.placeBlock(position);
                        }
                        if (isHelping) {
                            return true;
                        }
                        break;
                    }
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
        AutoFeetPlace.isPlacing = false;
        this.didPlace = false;
        this.placements = 0;
        this.obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        final int echestSlot = ItemUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
        if (!this.isEnabled()) {
            return true;
        }
        if (this.retryTimer.hasReached(2500L)) {
            this.retries.clear();
            this.retryTimer.reset();
        }
        if (this.obbySlot == -1 && (!this.echests.getValue() || echestSlot == -1)) {
            this.disable();
            return true;
        }
        return !this.timer.hasReached(this.delay.getValue());
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.placements < this.blocksPerTick.getValue()) {
            AutoFeetPlace.isPlacing = true;
            BlockUtil.placeBlock(pos);
            this.didPlace = true;
            ++this.placements;
        }
    }
    
    static {
        AutoFeetPlace.isPlacing = false;
    }
    
    public enum MovementMode
    {
        NONE, 
        STATIC, 
        LIMIT, 
        OFF;
    }
}
