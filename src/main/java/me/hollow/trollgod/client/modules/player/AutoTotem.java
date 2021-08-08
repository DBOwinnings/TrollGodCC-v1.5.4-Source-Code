package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.client.modules.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.init.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;

@ModuleManifest(label = "AutoTotem", category = Category.PLAYER, color = -12538256)
public class AutoTotem extends Module
{
    @Override
    public void onUpdate() {
        if (this.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        final int totemSlot = ItemUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        if (this.mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING) {
            final int slot = (totemSlot < 9) ? (totemSlot + 36) : totemSlot;
            if (totemSlot != -1) {
                this.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
                this.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
                if (this.mc.player.getHeldItemMainhand().isEmpty()) {
                    return;
                }
                this.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
            }
        }
        final int totems = ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING);
        if (totems == 0) {
            this.clearSuffix();
            return;
        }
        this.setSuffix(ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) + "");
    }
}
