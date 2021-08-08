package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import java.util.concurrent.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.entity.*;
import me.hollow.trollgod.*;
import net.minecraft.client.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.enchantment.*;
import java.util.*;
import net.minecraft.entity.item.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;

@ModuleManifest(label = "AutoArmor", listen = false, category = Category.COMBAT, color = -12538256)
public class AutoArmor extends Module
{
    private final Setting<Integer> delay;
    private final Setting<Boolean> mendingTakeOff;
    private final Setting<Integer> crystalRange;
    private final Setting<Integer> closestEnemy;
    private final Setting<Integer> helmetThreshold;
    private final Setting<Integer> chestThreshold;
    private final Setting<Integer> legThreshold;
    private final Setting<Integer> bootsThreshold;
    private final Setting<Boolean> curse;
    private final Setting<Integer> actions;
    private final Setting<Boolean> tps;
    private final Timer timer;
    private final Queue<Task> taskList;
    private final List<Integer> doneSlots;
    
    public AutoArmor() {
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", (T)50, (T)0, (T)500));
        this.mendingTakeOff = (Setting<Boolean>)this.register(new Setting("Auto Mend", (T)false));
        this.crystalRange = (Setting<Integer>)this.register(new Setting("Crystal Range", (T)10, (T)0, (T)20));
        this.closestEnemy = (Setting<Integer>)this.register(new Setting("Enemy Range", (T)8, (T)1, (T)20, v -> this.mendingTakeOff.getValue()));
        this.helmetThreshold = (Setting<Integer>)this.register(new Setting("Helmet %", (T)80, (T)1, (T)100, v -> this.mendingTakeOff.getValue()));
        this.chestThreshold = (Setting<Integer>)this.register(new Setting("Chest %", (T)80, (T)1, (T)100, v -> this.mendingTakeOff.getValue()));
        this.legThreshold = (Setting<Integer>)this.register(new Setting("Legs %", (T)80, (T)1, (T)100, v -> this.mendingTakeOff.getValue()));
        this.bootsThreshold = (Setting<Integer>)this.register(new Setting("Boots %", (T)80, (T)1, (T)100, v -> this.mendingTakeOff.getValue()));
        this.curse = (Setting<Boolean>)this.register(new Setting("Curse Of Binding", (T)false));
        this.actions = (Setting<Integer>)this.register(new Setting("Actions", (T)3, (T)1, (T)12));
        this.tps = (Setting<Boolean>)this.register(new Setting("Tps Sync", (T)true));
        this.timer = new Timer();
        this.taskList = new ConcurrentLinkedQueue<Task>();
        this.doneSlots = new ArrayList<Integer>();
    }
    
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
        if (this.isNull() || (this.mc.currentScreen instanceof GuiContainer && !(this.mc.currentScreen instanceof GuiInventory))) {
            return;
        }
        if (this.taskList.isEmpty()) {
            if (this.mendingTakeOff.getValue() && this.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE && this.mc.gameSettings.keyBindUseItem.isKeyDown() && (this.isSafe() || BlockUtil.isSafe((Entity)this.mc.player, 1))) {
                final ItemStack helm = this.mc.player.inventoryContainer.getSlot(5).getStack();
                if (!helm.isEmpty()) {
                    final int helmDamage = ItemUtil.getRoundedDamage(helm);
                    if (helmDamage >= this.helmetThreshold.getValue()) {
                        this.takeOffSlot(5);
                    }
                }
                final ItemStack chest = this.mc.player.inventoryContainer.getSlot(6).getStack();
                if (!chest.isEmpty()) {
                    final int chestDamage = ItemUtil.getRoundedDamage(chest);
                    if (chestDamage >= this.chestThreshold.getValue()) {
                        this.takeOffSlot(6);
                    }
                }
                final ItemStack legging = this.mc.player.inventoryContainer.getSlot(7).getStack();
                if (!legging.isEmpty()) {
                    final int leggingDamage = ItemUtil.getRoundedDamage(legging);
                    if (leggingDamage >= this.legThreshold.getValue()) {
                        this.takeOffSlot(7);
                    }
                }
                final ItemStack feet = this.mc.player.inventoryContainer.getSlot(8).getStack();
                if (!feet.isEmpty()) {
                    final int bootDamage = ItemUtil.getRoundedDamage(feet);
                    if (bootDamage >= this.bootsThreshold.getValue()) {
                        this.takeOffSlot(8);
                    }
                }
                return;
            }
            final ItemStack helm = this.mc.player.inventoryContainer.getSlot(5).getStack();
            if (helm.getItem() == Items.AIR) {
                final int slot = this.findArmorSlot(EntityEquipmentSlot.HEAD, this.curse.getValue(), false);
                if (slot != -1) {
                    this.getSlotOn(5, slot);
                }
            }
            final ItemStack chest = this.mc.player.inventoryContainer.getSlot(6).getStack();
            if (chest.getItem() == Items.AIR && this.taskList.isEmpty()) {
                final int slot2 = this.findArmorSlot(EntityEquipmentSlot.CHEST, this.curse.getValue(), true);
                if (slot2 != -1) {
                    this.getSlotOn(6, slot2);
                }
            }
            final ItemStack legging = this.mc.player.inventoryContainer.getSlot(7).getStack();
            if (legging.getItem() == Items.AIR) {
                final int slot3 = this.findArmorSlot(EntityEquipmentSlot.LEGS, this.curse.getValue(), true);
                if (slot3 != -1) {
                    this.getSlotOn(7, slot3);
                }
            }
            final ItemStack feet = this.mc.player.inventoryContainer.getSlot(8).getStack();
            if (feet.getItem() == Items.AIR) {
                final int slot4 = this.findArmorSlot(EntityEquipmentSlot.FEET, this.curse.getValue(), true);
                if (slot4 != -1) {
                    this.getSlotOn(8, slot4);
                }
            }
        }
        if (this.timer.hasReached((int)(this.delay.getValue() * (this.tps.getValue() ? TrollGod.INSTANCE.getTpsManager().getTpsFactor() : 1.0f)))) {
            if (!this.taskList.isEmpty()) {
                for (int i = 0; i < this.actions.getValue(); ++i) {
                    final Task task = this.taskList.poll();
                    if (task != null) {
                        task.run();
                    }
                }
            }
            this.timer.reset();
        }
    }
    
    private void takeOffSlot(final int slot) {
        if (this.taskList.isEmpty()) {
            int target = -1;
            for (final int i : this.findEmptySlots(true)) {
                if (!this.doneSlots.contains(target)) {
                    target = i;
                    this.doneSlots.add(i);
                }
            }
            if (target != -1) {
                this.taskList.add(new Task(slot, true));
                this.taskList.add(new Task());
            }
        }
    }
    
    public int findItemInventorySlot(final Item item, final boolean offHand) {
        int slot = -1;
        for (final Map.Entry<Integer, ItemStack> entry : this.getInventoryAndHotbarSlots().entrySet()) {
            if (entry.getValue().getItem() == item) {
                if (entry.getKey() == 45 && !offHand) {
                    continue;
                }
                slot = entry.getKey();
                return slot;
            }
        }
        return slot;
    }
    
    public List<Integer> findEmptySlots(final boolean withXCarry) {
        final List<Integer> outPut = new ArrayList<Integer>();
        for (final Map.Entry<Integer, ItemStack> entry : this.getInventoryAndHotbarSlots().entrySet()) {
            if (entry.getValue().isEmpty() || entry.getValue().getItem() == Items.AIR) {
                outPut.add(entry.getKey());
            }
        }
        if (withXCarry) {
            for (int i = 1; i < 5; ++i) {
                final Slot craftingSlot = this.mc.player.inventoryContainer.inventorySlots.get(i);
                final ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.isEmpty() || craftingStack.getItem() == Items.AIR) {
                    outPut.add(i);
                }
            }
        }
        return outPut;
    }
    
    public Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return this.getInventorySlots(9, 44);
    }
    
    public int findArmorSlot(final EntityEquipmentSlot type, final boolean binding) {
        int slot = -1;
        float damage = 0.0f;
        for (int i = 9; i < 45; ++i) {
            final ItemStack s = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if (s.getItem() != Items.AIR && s.getItem() instanceof ItemArmor) {
                final ItemArmor armor = (ItemArmor)s.getItem();
                if (armor.armorType == type) {
                    final float currentDamage = (float)(armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, s));
                    final boolean cursed = binding && EnchantmentHelper.hasBindingCurse(s);
                    if (currentDamage > damage && !cursed) {
                        damage = currentDamage;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }
    
    public int findArmorSlot(final EntityEquipmentSlot type, final boolean binding, final boolean withXCarry) {
        int slot = this.findArmorSlot(type, binding);
        if (slot == -1 && withXCarry) {
            float damage = 0.0f;
            for (int i = 1; i < 5; ++i) {
                final Slot craftingSlot = this.mc.player.inventoryContainer.inventorySlots.get(i);
                final ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() != Items.AIR && craftingStack.getItem() instanceof ItemArmor) {
                    final ItemArmor armor = (ItemArmor)craftingStack.getItem();
                    if (armor.armorType == type) {
                        final float currentDamage = (float)(armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, craftingStack));
                        final boolean cursed = binding && EnchantmentHelper.hasBindingCurse(craftingStack);
                        if (currentDamage > damage && !cursed) {
                            damage = currentDamage;
                            slot = i;
                        }
                    }
                }
            }
        }
        return slot;
    }
    
    private Map<Integer, ItemStack> getInventorySlots(final int currentI, final int last) {
        int current = currentI;
        final Map<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        while (current <= last) {
            fullInventorySlots.put(current, (ItemStack)this.mc.player.inventoryContainer.getInventory().get(current));
            ++current;
        }
        return fullInventorySlots;
    }
    
    private void getSlotOn(final int slot, final int target) {
        if (this.taskList.isEmpty()) {
            this.doneSlots.remove((Object)target);
            this.taskList.add(new Task(target, true));
            this.taskList.add(new Task());
        }
    }
    
    private boolean isSafe() {
        final Entity crystal = (Entity)this.mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityEnderCrystal).filter(e -> this.mc.player.getDistance(e) > this.crystalRange.getValue()).findFirst().orElse(null);
        if (crystal == null) {
            final EntityPlayer closest = CombatUtil.getTarget(this.closestEnemy.getValue());
            return closest == null || this.mc.player.getDistanceSq((Entity)closest) >= this.square(this.closestEnemy.getValue());
        }
        return false;
    }
    
    private double square(final double sq) {
        return sq * sq;
    }
    
    private static class Task
    {
        private final int slot;
        private boolean update;
        private boolean quickClick;
        private Minecraft mc;
        
        public Task(final int slot, final boolean quickClick) {
            this.update = false;
            this.quickClick = false;
            this.mc = Minecraft.getMinecraft();
            this.slot = slot;
            this.quickClick = quickClick;
        }
        
        public Task(final int slot) {
            this.update = false;
            this.quickClick = false;
            this.mc = Minecraft.getMinecraft();
            this.slot = slot;
            this.quickClick = false;
        }
        
        public Task() {
            this.update = false;
            this.quickClick = false;
            this.mc = Minecraft.getMinecraft();
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
