/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.BlockUtil;
import me.hollow.trollgod.api.util.CombatUtil;
import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.api.util.MessageUtil;
import me.hollow.trollgod.api.util.Timer;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

@ModuleManifest(label="HoleFiller", listen=false, category=Module.Category.COMBAT, color=-52230)
public class HoleFiller
extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 0, 0, 500));
    private final Setting<Integer> bpt = this.register(new Setting<Integer>("Blocks/Tick", 10, 1, 20));
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(6.0f)));
    private final Setting<Integer> retries = this.register(new Setting<Integer>("Retries", 1, 0, 15));
    private final Setting<Boolean> autoDisable = this.register(new Setting<Boolean>("Auto Disable", true));
    private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(BlockUtil.holeOffsets);
    private final Map<BlockPos, Integer> retryMap = new WeakHashMap<BlockPos, Integer>();
    private final Timer retryTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private int placeAmount;
    private int blockSlot = -1;

    @Override
    public void onUpdate() {
        if (this.check()) {
            EntityPlayer currentTarget = CombatUtil.getTarget(10.0f);
            if (currentTarget == null) {
                this.disable();
                return;
            }
            List<BlockPos> holes = this.calcHoles();
            if (holes.size() == 0) {
                this.disable();
                return;
            }
            int lastSlot = this.mc.player.inventory.currentItem;
            this.blockSlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            if (this.blockSlot == -1) {
                this.disable();
                return;
            }
            this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.blockSlot));
            for (BlockPos pos : holes) {
                int placability = BlockUtil.isPositionPlaceable(pos, true);
                if (placability == 1 || this.retryMap.get(pos) == null || this.retryMap.get(pos) < this.retries.getValue()) {
                    this.placeBlock(pos);
                    this.retryMap.put(pos, this.retryMap.get(pos) == null ? 1 : this.retryMap.get(pos) + 1);
                    this.retryTimer.reset();
                    continue;
                }
                if (placability != 3) continue;
                this.placeBlock(pos);
            }
            this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(lastSlot));
            this.placeTimer.reset();
            if (this.autoDisable.getValue().booleanValue()) {
                this.disable();
            }
        }
    }

    private void placeBlock(BlockPos pos) {
        if (this.bpt.getValue() > this.placeAmount && this.placeTimer.hasReached(this.delay.getValue().intValue())) {
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
        ArrayList<BlockPos> safeSpots = new ArrayList<BlockPos>();
        List<BlockPos> sphere = BlockUtil.getSphere(this.range.getValue().floatValue(), false);
        int size = sphere.size();
        for (int i = 0; i < size; ++i) {
            BlockPos pos = sphere.get(i);
            if (!this.mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || !this.mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR) || !this.mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) continue;
            boolean isSafe = true;
            for (BlockPos offset : surroundOffset) {
                Block block = this.mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
                if (block == Blocks.BEDROCK || block == Blocks.OBSIDIAN || block == Blocks.ENDER_CHEST) continue;
                isSafe = false;
            }
            if (!isSafe) continue;
            safeSpots.add(pos);
        }
        return safeSpots;
    }
}

