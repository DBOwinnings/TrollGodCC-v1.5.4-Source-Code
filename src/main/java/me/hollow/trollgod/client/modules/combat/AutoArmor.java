/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.BlockUtil;
import me.hollow.trollgod.api.util.CombatUtil;
import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.api.util.Timer;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@ModuleManifest(label="AutoArmor", listen=false, category=Module.Category.COMBAT, color=-12538256)
public class AutoArmor
extends Module {
    private final Setting<Integer> delay = this.register(new Setting<Integer>("Delay", 50, 0, 500));
    private final Setting<Boolean> mendingTakeOff = this.register(new Setting<Boolean>("Auto Mend", false));
    private final Setting<Integer> crystalRange = this.register(new Setting<Integer>("Crystal Range", 10, 0, 20));
    private final Setting<Integer> closestEnemy = this.register(new Setting<Object>("Enemy Range", 8, 1, 20, v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> helmetThreshold = this.register(new Setting<Object>("Helmet %", 80, 1, 100, v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> chestThreshold = this.register(new Setting<Object>("Chest %", 80, 1, 100, v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> legThreshold = this.register(new Setting<Object>("Legs %", 80, 1, 100, v -> this.mendingTakeOff.getValue()));
    private final Setting<Integer> bootsThreshold = this.register(new Setting<Object>("Boots %", 80, 1, 100, v -> this.mendingTakeOff.getValue()));
    private final Setting<Boolean> curse = this.register(new Setting<Boolean>("Curse Of Binding", false));
    private final Setting<Integer> actions = this.register(new Setting<Integer>("Actions", 3, 1, 12));
    private final Setting<Boolean> tps = this.register(new Setting<Boolean>("Tps Sync", true));
    private final Timer timer = new Timer();
    private final Queue<Task> taskList = new ConcurrentLinkedQueue<Task>();
    private final List<Integer> doneSlots = new ArrayList<Integer>();

    @Override
    public void onDisable() {
        this.taskList.clear();
        this.doneSlots.clear();
    }

    @Override
    public void onDisconnect() {
        this.taskList.clear();
        this.doneSlots.clear();
    }

    @Override
    public void onUpdate() {
        if (this.isNull() || this.mc.currentScreen instanceof GuiContainer && !(this.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (this.taskList.isEmpty()) {
            int slot;
            ItemStack feet;
            int slot2;
            ItemStack legging;
            int slot3;
            ItemStack chest;
            int slot4;
            if (this.mendingTakeOff.getValue().booleanValue() && this.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE && this.mc.gameSettings.keyBindUseItem.isKeyDown() && (this.isSafe() || BlockUtil.isSafe((Entity)this.mc.player, 1))) {
                int bootDamage;
                ItemStack feet2;
                int leggingDamage;
                ItemStack legging2;
                int chestDamage;
                ItemStack chest2;
                int helmDamage;
                ItemStack helm = this.mc.player.inventoryContainer.getSlot(5).getStack();
                if (!helm.isEmpty() && (helmDamage = ItemUtil.getRoundedDamage(helm)) >= this.helmetThreshold.getValue()) {
                    this.takeOffSlot(5);
                }
                if (!(chest2 = this.mc.player.inventoryContainer.getSlot(6).getStack()).isEmpty() && (chestDamage = ItemUtil.getRoundedDamage(chest2)) >= this.chestThreshold.getValue()) {
                    this.takeOffSlot(6);
                }
                if (!(legging2 = this.mc.player.inventoryContainer.getSlot(7).getStack()).isEmpty() && (leggingDamage = ItemUtil.getRoundedDamage(legging2)) >= this.legThreshold.getValue()) {
                    this.takeOffSlot(7);
                }
                if (!(feet2 = this.mc.player.inventoryContainer.getSlot(8).getStack()).isEmpty() && (bootDamage = ItemUtil.getRoundedDamage(feet2)) >= this.bootsThreshold.getValue()) {
                    this.takeOffSlot(8);
                }
                return;
            }
            ItemStack helm = this.mc.player.inventoryContainer.getSlot(5).getStack();
            if (helm.getItem() == Items.AIR && (slot4 = this.findArmorSlot(EntityEquipmentSlot.HEAD, this.curse.getValue(), false)) != -1) {
                this.getSlotOn(5, slot4);
            }
            if ((chest = this.mc.player.inventoryContainer.getSlot(6).getStack()).getItem() == Items.AIR && this.taskList.isEmpty() && (slot3 = this.findArmorSlot(EntityEquipmentSlot.CHEST, this.curse.getValue(), true)) != -1) {
                this.getSlotOn(6, slot3);
            }
            if ((legging = this.mc.player.inventoryContainer.getSlot(7).getStack()).getItem() == Items.AIR && (slot2 = this.findArmorSlot(EntityEquipmentSlot.LEGS, this.curse.getValue(), true)) != -1) {
                this.getSlotOn(7, slot2);
            }
            if ((feet = this.mc.player.inventoryContainer.getSlot(8).getStack()).getItem() == Items.AIR && (slot = this.findArmorSlot(EntityEquipmentSlot.FEET, this.curse.getValue(), true)) != -1) {
                this.getSlotOn(8, slot);
            }
        }
        if (this.timer.hasReached((int)((float)this.delay.getValue().intValue() * (this.tps.getValue() != false ? TrollGod.INSTANCE.getTpsManager().getTpsFactor() : 1.0f)))) {
            if (!this.taskList.isEmpty()) {
                for (int i = 0; i < this.actions.getValue(); ++i) {
                    Task task = this.taskList.poll();
                    if (task == null) continue;
                    task.run();
                }
            }
            this.timer.reset();
        }
    }

    private void takeOffSlot(int slot) {
        if (this.taskList.isEmpty()) {
            int target = -1;
            for (int i : this.findEmptySlots(true)) {
                if (this.doneSlots.contains(target)) continue;
                target = i;
                this.doneSlots.add(i);
            }
            if (target != -1) {
                this.taskList.add(new Task(slot, true));
                this.taskList.add(new Task());
            }
        }
    }

    public int findItemInventorySlot(Item item, boolean offHand) {
        int slot = -1;
        for (Map.Entry<Integer, ItemStack> entry : this.getInventoryAndHotbarSlots().entrySet()) {
            if (entry.getValue().getItem() != item || entry.getKey() == 45 && !offHand) continue;
            slot = entry.getKey();
            return slot;
        }
        return slot;
    }

    public List<Integer> findEmptySlots(boolean withXCarry) {
        ArrayList<Integer> outPut = new ArrayList<Integer>();
        for (Map.Entry<Integer, ItemStack> entry : this.getInventoryAndHotbarSlots().entrySet()) {
            if (!entry.getValue().isEmpty() && entry.getValue().getItem() != Items.AIR) continue;
            outPut.add(entry.getKey());
        }
        if (withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Slot craftingSlot = (Slot)this.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (!craftingStack.isEmpty() && craftingStack.getItem() != Items.AIR) continue;
                outPut.add(i);
            }
        }
        return outPut;
    }

    public Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return this.getInventorySlots(9, 44);
    }

    public int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
        int slot = -1;
        float damage = 0.0f;
        for (int i = 9; i < 45; ++i) {
            boolean cursed;
            ItemStack s = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if (s.getItem() == Items.AIR || !(s.getItem() instanceof ItemArmor)) continue;
            ItemArmor armor = (ItemArmor)s.getItem();
            if (armor.armorType != type) continue;
            float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.PROTECTION, (ItemStack)s);
            boolean bl = cursed = binding && EnchantmentHelper.hasBindingCurse((ItemStack)s);
            if (!(currentDamage > damage) || cursed) continue;
            damage = currentDamage;
            slot = i;
        }
        return slot;
    }

    public int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
        int slot = this.findArmorSlot(type, binding);
        if (slot == -1 && withXCarry) {
            float damage = 0.0f;
            for (int i = 1; i < 5; ++i) {
                boolean cursed;
                Slot craftingSlot = (Slot)this.mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() == Items.AIR || !(craftingStack.getItem() instanceof ItemArmor)) continue;
                ItemArmor armor = (ItemArmor)craftingStack.getItem();
                if (armor.armorType != type) continue;
                float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.PROTECTION, (ItemStack)craftingStack);
                boolean bl = cursed = binding && EnchantmentHelper.hasBindingCurse((ItemStack)craftingStack);
                if (!(currentDamage > damage) || cursed) continue;
                damage = currentDamage;
                slot = i;
            }
        }
        return slot;
    }

    private Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        for (int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(current, (ItemStack)this.mc.player.inventoryContainer.getInventory().get(current));
        }
        return fullInventorySlots;
    }

    private void getSlotOn(int slot, int target) {
        if (this.taskList.isEmpty()) {
            this.doneSlots.remove((Object)target);
            this.taskList.add(new Task(target, true));
            this.taskList.add(new Task());
        }
    }

    private boolean isSafe() {
        Entity crystal = this.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal).filter(e -> this.mc.player.getDistance(e) > (float)this.crystalRange.getValue().intValue()).findFirst().orElse(null);
        if (crystal == null) {
            EntityPlayer closest = CombatUtil.getTarget(this.closestEnemy.getValue().intValue());
            if (closest == null) {
                return true;
            }
            return this.mc.player.getDistanceSq((Entity)closest) >= this.square(this.closestEnemy.getValue().intValue());
        }
        return false;
    }

    private double square(double sq) {
        return sq * sq;
    }

    private static class Task {
        private final int slot;
        private boolean update = false;
        private boolean quickClick = false;
        private Minecraft mc = Minecraft.getMinecraft();

        public Task(int slot, boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
        }

        public Task(int slot) {
            this.slot = slot;
            this.quickClick = false;
        }

        public Task() {
            this.update = true;
            this.slot = -1;
        }

        public void run() {
            if (this.slot != -1) {
                this.mc.playerController.windowClick(0, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, (EntityPlayer)this.mc.player);
            }
            if (this.update) {
                this.mc.playerController.updateController();
            }
        }

        public boolean isSwitching() {
            return !this.update;
        }
    }
}

