package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.util.math.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import java.util.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.client.*;
import net.minecraft.item.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "AntiRegear", listen = false, category = Category.COMBAT)
public class AntiRegear extends Module
{
    private final Setting<Float> targetRange;
    private final Set<BlockPos> shulkerBlackList;
    
    public AntiRegear() {
        this.targetRange = (Setting<Float>)this.register(new Setting("Target Range", (T)10.0f, (T)0.0f, (T)20.0f));
        this.shulkerBlackList = new HashSet<BlockPos>();
    }
    
    @Override
    public void onUpdate() {
        if (CombatUtil.getTarget(this.targetRange.getValue()) == null) {
            return;
        }
        if (!this.mc.player.onGround) {
            return;
        }
        for (final BlockPos pos : BlockUtil.getSphere(5.0f, true)) {
            if (this.mc.world.getBlockState(pos).getBlock() instanceof BlockShulkerBox) {
                if (this.shulkerBlackList.contains(pos)) {
                    continue;
                }
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
                int lastSlot = -1;
                if (this.mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_PICKAXE) {
                    lastSlot = this.mc.player.inventory.currentItem;
                    final int pickSlot = ItemUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                    if (pickSlot != -1) {
                        this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(ItemUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE)));
                    }
                }
                this.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                this.mc.getConnection().sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                if (lastSlot == -1) {
                    continue;
                }
                this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(lastSlot));
            }
        }
    }
    
    @Subscribe
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
            if (this.mc.player.getHeldItem(packet.getHand()).getItem() instanceof ItemShulkerBox) {
                this.shulkerBlackList.add(packet.getPos().offset(packet.getDirection()));
            }
        }
    }
}
