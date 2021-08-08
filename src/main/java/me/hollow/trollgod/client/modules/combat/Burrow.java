package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.network.*;
import net.minecraft.network.play.client.*;
import me.hollow.trollgod.api.util.*;

@ModuleManifest(label = "SelfFill", category = Category.COMBAT, color = -16764673)
public class Burrow extends Module
{
    private final Setting<Boolean> lessPackets;
    private final Setting<Float> height;
    private final Setting<Boolean> preferEChests;
    private static Burrow INSTANCE;
    public BlockPos startPos;
    private int obbySlot;
    
    public Burrow() {
        this.lessPackets = (Setting<Boolean>)this.register(new Setting("Conserve Packets", (T)false));
        this.height = (Setting<Float>)this.register(new Setting("Height", (T)5.0f, (T)(-5.0f), (T)5.0f));
        this.preferEChests = (Setting<Boolean>)this.register(new Setting("Prefer EChests", (T)true));
        this.obbySlot = -1;
        Burrow.INSTANCE = this;
    }
    
    public static Burrow getInstance() {
        return Burrow.INSTANCE;
    }
    
    @Override
    public void onEnable() {
        if (this.isNull()) {
            this.disable();
            return;
        }
        this.obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        final int eChestSlot = ItemUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
        if ((this.preferEChests.getValue() || this.obbySlot == -1) && eChestSlot != -1) {
            this.obbySlot = eChestSlot;
        }
        else {
            this.obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
            if (this.obbySlot == -1) {
                MessageUtil.sendClientMessage(ChatFormatting.RED + "<Burrow> Toggling, No obsidian.", -551);
                this.disable();
            }
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.isNull()) {
            return;
        }
        final int startSlot = this.mc.player.inventory.currentItem;
        this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.obbySlot));
        this.startPos = new BlockPos(this.mc.player.getPositionVector());
        if (this.lessPackets.getValue()) {
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.45, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.79, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.1, this.mc.player.posZ, true));
        }
        else {
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.41, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.75, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.0, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.16, this.mc.player.posZ, true));
        }
        final boolean onEChest = this.mc.world.getBlockState(new BlockPos(this.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (BlockUtil.placeBlock(onEChest ? this.startPos.up() : this.startPos) && !this.mc.player.capabilities.isCreativeMode) {
            this.mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + this.height.getValue(), this.mc.player.posZ, false));
        }
        if (startSlot != -1) {
            this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(startSlot));
        }
        this.disable();
    }
}
