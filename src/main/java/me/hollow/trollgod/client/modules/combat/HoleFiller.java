package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.entity.player.*;
import com.mojang.realmsclient.gui.*;
import me.hollow.trollgod.api.util.*;
import java.util.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;

@ModuleManifest(label = "HoleFiller", listen = false, category = Category.COMBAT, color = -52230)
public class HoleFiller extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Integer> bpt;
    private final Setting<Float> range;
    private final Setting<Integer> retries;
    private final Setting<Boolean> autoDisable;
    private static final BlockPos[] surroundOffset;
    private final Map<BlockPos, Integer> retryMap;
    private final Timer retryTimer;
    private final Timer placeTimer;
    private int placeAmount;
    private int blockSlot;
    
    public HoleFiller() {
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)0, (T)0, (T)500));
        this.bpt = (Setting<Integer>)this.register(new Setting("Blocks/Tick", (T)10, (T)1, (T)20));
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)5.0f, (T)1.0f, (T)6.0f));
        this.retries = (Setting<Integer>)this.register(new Setting("Retries", (T)1, (T)0, (T)15));
        this.autoDisable = (Setting<Boolean>)this.register(new Setting("Auto Disable", (T)true));
        this.retryMap = new WeakHashMap<BlockPos, Integer>();
        this.retryTimer = new Timer();
        this.placeTimer = new Timer();
        this.blockSlot = -1;
    }
    
    @Override
    public void onUpdate() {
        if (this.check()) {
            final EntityPlayer currentTarget = CombatUtil.getTarget(10.0f);
            if (currentTarget == null) {
                this.disable();
                return;
            }
            final List<BlockPos> holes = this.calcHoles();
            if (holes.size() == 0) {
                this.disable();
                return;
            }
            final int lastSlot = this.mc.player.inventory.currentItem;
            this.blockSlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            if (this.blockSlot == -1) {
                this.disable();
                return;
            }
            this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.blockSlot));
            for (final BlockPos pos : holes) {
                final int placability = BlockUtil.isPositionPlaceable(pos, true);
                if (placability == 1 || this.retryMap.get(pos) == null || this.retryMap.get(pos) < this.retries.getValue()) {
                    this.placeBlock(pos);
                    this.retryMap.put(pos, (this.retryMap.get(pos) == null) ? 1 : (this.retryMap.get(pos) + 1));
                    this.retryTimer.reset();
                }
                else {
                    if (placability != 3) {
                        continue;
                    }
                    this.placeBlock(pos);
                }
            }
            this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(lastSlot));
            this.placeTimer.reset();
            if (this.autoDisable.getValue()) {
                this.disable();
            }
        }
    }
    
    private void placeBlock(final BlockPos pos) {
        if (this.bpt.getValue() > this.placeAmount && this.placeTimer.hasReached(this.delay.getValue())) {
            BlockUtil.placeBlock(pos);
            ++this.placeAmount;
        }
    }
    
    private boolean check() {
        if (this.isNull()) {
            return false;
        }
        this.placeAmount = 0;
        this.blockSlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        if (this.retryTimer.hasReached(2000L)) {
            this.retryMap.clear();
            this.retryTimer.reset();
        }
        if (this.blockSlot == -1) {
            MessageUtil.sendClientMessage(ChatFormatting.RED + "<HoleFiller> No obsidian, toggling!", -22221);
            this.disable();
        }
        return true;
    }
    
    public List<BlockPos> calcHoles() {
        final List<BlockPos> safeSpots = new ArrayList<BlockPos>();
        final List<BlockPos> sphere = BlockUtil.getSphere(this.range.getValue(), false);
        for (int size = sphere.size(), i = 0; i < size; ++i) {
            final BlockPos pos = sphere.get(i);
            if (this.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && this.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                if (this.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                    boolean isSafe = true;
                    for (final BlockPos offset : HoleFiller.surroundOffset) {
                        final Block block = this.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
                        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) {
                            if (block != Blocks.ENDER_CHEST) {
                                isSafe = false;
                            }
                        }
                    }
                    if (isSafe) {
                        safeSpots.add(pos);
                    }
                }
            }
        }
        return safeSpots;
    }
    
    static {
        surroundOffset = BlockUtil.toBlockPos(BlockUtil.holeOffsets);
    }
}
