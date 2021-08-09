/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;

@ModuleManifest(label="AutoTotem", category=Module.Category.PLAYER, color=-12538256)
public class AutoTotem
extends Module {
    @Override
    public void onUpdate() {
        int totems;
        if (this.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        int totemSlot = ItemUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        if (this.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
            int slot;
            int n = slot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
            if (totemSlot != -1) {
                this.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
                this.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
                if (this.mc.player.getHeldItemMainhand().isEmpty()) {
                    return;
                }
                this.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
            }
        }
        if ((totems = ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING)) == 0) {
            this.clearSuffix();
            return;
        }
        this.setSuffix(ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) + "");
    }
}

