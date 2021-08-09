/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.BlockUtil;
import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.api.util.MessageUtil;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;

@ModuleManifest(label="SelfFill", category=Module.Category.COMBAT, color=-16764673)
public class Burrow
extends Module {
    private final Setting<Boolean> lessPackets = this.register(new Setting<Boolean>("Conserve Packets", false));
    private final Setting<Float> height = this.register(new Setting<Float>("Height", Float.valueOf(5.0f), Float.valueOf(-5.0f), Float.valueOf(5.0f)));
    private final Setting<Boolean> preferEChests = this.register(new Setting<Boolean>("Prefer EChests", true));
    private static Burrow INSTANCE;
    public BlockPos startPos;
    private int obbySlot = -1;

    public Burrow() {
        INSTANCE = this;
    }

    public static Burrow getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        if (this.isNull()) {
            this.disable();
            return;
        }
        this.obbySlot = ItemUtil.getBlockFromHotbar(Blocks.OBSIDIAN);
        int eChestSlot = ItemUtil.getBlockFromHotbar(Blocks.ENDER_CHEST);
        if ((this.preferEChests.getValue().booleanValue() || this.obbySlot == -1) && eChestSlot != -1) {
            this.obbySlot = eChestSlot;
        } else {
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
        int startSlot = this.mc.player.inventory.currentItem;
        this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.obbySlot));
        this.startPos = new BlockPos(this.mc.player.getPositionVector());
        if (this.lessPackets.getValue().booleanValue()) {
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.45, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.79, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.1, this.mc.player.posZ, true));
        } else {
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.41, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 0.75, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.0, this.mc.player.posZ, true));
            this.mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + 1.16, this.mc.player.posZ, true));
        }
        boolean onEChest = this.mc.world.getBlockState(new BlockPos(this.mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        if (BlockUtil.placeBlock(onEChest ? this.startPos.up() : this.startPos) && !this.mc.player.capabilities.isCreativeMode) {
            this.mc.getConnection().sendPacket((Packet)new CPacketPlayer.Position(this.mc.player.posX, this.mc.player.posY + (double)this.height.getValue().floatValue(), this.mc.player.posZ, false));
        }
        if (startSlot != -1) {
            this.mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(startSlot));
        }
        this.disable();
    }
}

