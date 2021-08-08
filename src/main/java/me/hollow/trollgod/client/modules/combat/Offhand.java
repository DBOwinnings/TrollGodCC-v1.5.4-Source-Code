package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.client.events.*;
import tcb.bces.listener.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.init.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.player.*;
import me.hollow.trollgod.*;
import net.minecraft.inventory.*;

@ModuleManifest(label = "Offhand", category = Category.COMBAT, color = 2065886)
public class Offhand extends Module
{
    private final Setting<Boolean> noCrystal;
    private final Setting<Float> health;
    private final Setting<Float> gappleHealth;
    private final Setting<Bind> gappleBind;
    private final Setting<Boolean> swordGapple;
    private boolean gappling;
    
    public Offhand() {
        this.noCrystal = (Setting<Boolean>)this.register(new Setting("No Crystal", (T)false));
        this.health = (Setting<Float>)this.register(new Setting("C-T-Health", (T)15.0f, (T)1.0f, (T)37.0f));
        this.gappleHealth = (Setting<Float>)this.register(new Setting("G-T-Health", (T)8.0f, (T)1.0f, (T)37.0f));
        this.gappleBind = (Setting<Bind>)this.register(new Setting("Gapple Bind", (T)new Bind(-1)));
        this.swordGapple = (Setting<Boolean>)this.register(new Setting("Sword Gapple", (T)true));
    }
    
    @Subscribe
    public void onKey(final KeyEvent event) {
        if (event.getKey() == this.gappleBind.getValue().getKey()) {
            this.gappling = !this.gappling;
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        Item item = this.shouldTotem() ? Items.TOTEM_OF_UNDYING : (this.gappling ? Items.GOLDEN_APPLE : (this.noCrystal.getValue() ? Items.TOTEM_OF_UNDYING : Items.END_CRYSTAL));
        if (this.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && this.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            item = Items.GOLDEN_APPLE;
        }
        final int getSlot = ItemUtil.getItemSlot(item);
        if (this.mc.player.getHeldItemOffhand().getItem() != item && getSlot != -1) {
            this.switchItem((getSlot < 9) ? (getSlot + 36) : getSlot);
            this.mc.playerController.updateController();
        }
        if (item == Items.END_CRYSTAL) {
            this.setSuffix("Crystal");
        }
        else if (item == Items.TOTEM_OF_UNDYING) {
            this.setSuffix("Totem");
        }
        else {
            this.setSuffix("Gapple");
        }
    }
    
    private void switchItem(final int slot) {
        this.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
        this.mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
        this.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
    }
    
    private boolean shouldTotem() {
        return this.mc.player.fallDistance > 10.0f || (ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) != 0 && (ItemUtil.getItemCount(this.gappling ? Items.GOLDEN_APPLE : Items.END_CRYSTAL) == 0 || this.mc.player.getHealth() + this.mc.player.getAbsorptionAmount() <= this.getHealth() || !TrollGod.INSTANCE.getSafeManager().isSafe() || this.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA));
    }
    
    private float getHealth() {
        return this.gappling ? this.gappleHealth.getValue() : ((float)this.health.getValue());
    }
}
