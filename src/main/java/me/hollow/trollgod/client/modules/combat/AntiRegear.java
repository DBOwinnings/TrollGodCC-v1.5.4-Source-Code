/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.combat;

import java.util.HashSet;
import java.util.Set;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.BlockUtil;
import me.hollow.trollgod.api.util.CombatUtil;
import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.init.Items;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="AntiRegear", listen=false, category=Module.Category.COMBAT)
public class AntiRegear
extends Module {
    private final Setting<Float> targetRange = this.register(new Setting<Float>("Target Range", Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(20.0f)));
    private final Set<BlockPos> shulkerBlackList = new HashSet<BlockPos>();

    @Override
    public void onUpdate() {
        if (CombatUtil.getTarget(this.targetRange.getValue().floatValue()) == null) {
            return;
        }
        if (!this.mc.player.onGround) {
            return;
        }
        for (BlockPos pos : BlockUtil.getSphere(5.0f, true)) {
            if (!(this.mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox) || this.shulkerBlackList.contains(pos)) continue;
            this.mc.player.swingArm(EnumHand.MAIN_HAND);
            int lastSlot = -1;
            if (this.mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE) {
                lastSlot = this.mc.player.inventory.currentItem;
                int pickSlot = ItemUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                if (pickSlot != -1) {
                    this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(ItemUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE)));
                }
            }
            this.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
            this.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
            if (lastSlot == -1) continue;
            this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(lastSlot));
        }
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        CPacketPlayerTryUseItemOnBlock packet;
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && this.mc.player.getHeldItem((packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket()).getHand()).getItem() instanceof ItemShulkerBox) {
            this.shulkerBlackList.add(packet.getPos().offset(packet.getDirection()));
        }
    }
}

