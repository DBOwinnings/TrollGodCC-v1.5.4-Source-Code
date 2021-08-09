/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.property.Bind;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.client.events.KeyEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="Offhand", category=Module.Category.COMBAT, color=2065886)
public class Offhand
extends Module {
    private final Setting<Boolean> noCrystal = this.register(new Setting<Boolean>("No Crystal", false));
    private final Setting<Float> health = this.register(new Setting<Float>("C-T-Health", Float.valueOf(15.0f), Float.valueOf(1.0f), Float.valueOf(37.0f)));
    private final Setting<Float> gappleHealth = this.register(new Setting<Float>("G-T-Health", Float.valueOf(8.0f), Float.valueOf(1.0f), Float.valueOf(37.0f)));
    private final Setting<Bind> gappleBind = this.register(new Setting<Bind>("Gapple Bind", new Bind(-1)));
    private final Setting<Boolean> swordGapple = this.register(new Setting<Boolean>("Sword Gapple", true));
    private boolean gappling;

    @Subscribe
    public void onKey(KeyEvent event) {
        if (event.getKey() == this.gappleBind.getValue().getKey()) {
            this.gappling = !this.gappling;
        }
    }

    @Override
    public void onUpdate() {
        Item item = new Item();
        if (this.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        Item item2 = this.shouldTotem() ? Items.TOTEM_OF_UNDYING : (this.gappling ? Items.GOLDEN_APPLE : (item = this.noCrystal.getValue() != false ? Items.TOTEM_OF_UNDYING : Items.END_CRYSTAL));
        if (this.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            item = Items.GOLDEN_APPLE;
        }
        int getSlot = ItemUtil.getItemSlot(item);
        if (this.mc.player.getHeldItemOffhand().getItem() != item && getSlot != -1) {
            this.switchItem(getSlot < 9 ? getSlot + 36 : getSlot);
            this.mc.playerController.updateController();
        }
        if (item == Items.END_CRYSTAL) {
            this.setSuffix("Crystal");
        } else if (item == Items.TOTEM_OF_UNDYING) {
            this.setSuffix("Totem");
        } else {
            this.setSuffix("Gapple");
        }
    }

    private void switchItem(int slot) {
        this.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
        this.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
        this.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
    }

    private boolean shouldTotem() {
        if (this.mc.player.fallDistance > 10.0f) {
            return true;
        }
        if (ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) == 0) {
            return false;
        }
        if (ItemUtil.getItemCount(this.gappling ? Items.GOLDEN_APPLE : Items.END_CRYSTAL) == 0) {
            return true;
        }
        return this.mc.player.getHealth() + this.mc.player.getAbsorptionAmount() <= this.getHealth() || !TrollGod.INSTANCE.getSafeManager().isSafe() || this.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA;
    }

    private float getHealth() {
        return this.gappling ? this.gappleHealth.getValue().floatValue() : this.health.getValue().floatValue();
    }
}

