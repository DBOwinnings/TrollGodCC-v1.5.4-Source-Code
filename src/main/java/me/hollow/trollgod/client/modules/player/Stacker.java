package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.inventory.*;
import net.minecraft.entity.player.*;
import tcb.bces.listener.*;
import net.minecraft.init.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

@ModuleManifest(label = "Stacker", category = Category.PLAYER, color = -5616)
public class Stacker extends Module
{
    private final Setting<Integer> tickDelay;
    private final Setting<Integer> threshold;
    private int delayStep;
    
    public Stacker() {
        this.tickDelay = (Setting<Integer>)this.register(new Setting("Delay", (T)1, (T)0, (T)5));
        this.threshold = (Setting<Integer>)this.register(new Setting("Threshold", (T)30, (T)1, (T)63));
        this.delayStep = 0;
    }
    
    private Map<Integer, ItemStack> getInventory() {
        return this.getInventorySlots(9, 35);
    }
    
    private Map<Integer, ItemStack> getHotbar() {
        return this.getInventorySlots(36, 44);
    }
    
    private Map<Integer, ItemStack> getInventorySlots(int current, final int last) {
        final Map<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        while (current <= last) {
            fullInventorySlots.put(current, (ItemStack)this.mc.player.inventoryContainer.getInventory().get(current));
            ++current;
        }
        return fullInventorySlots;
    }
    
    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        if (this.isNull()) {
            return;
        }
        if (this.mc.currentScreen instanceof GuiContainer) {
            return;
        }
        if (this.delayStep < this.tickDelay.getValue()) {
            ++this.delayStep;
            return;
        }
        this.delayStep = 0;
        final Pair<Integer, Integer> slots = this.findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        final int inventorySlot = slots.getKey();
        final int hotbarSlot = slots.getValue();
        this.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
        this.mc.playerController.windowClick(0, hotbarSlot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
        this.mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)this.mc.player);
    }
    
    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        Pair<Integer, Integer> returnPair = null;
        for (final Map.Entry<Integer, ItemStack> hotbarSlot : this.getHotbar().entrySet()) {
            final ItemStack stack = hotbarSlot.getValue();
            if (!stack.isEmpty()) {
                if (stack.getItem() == Items.AIR) {
                    continue;
                }
                if (!stack.isStackable()) {
                    continue;
                }
                if (stack.getCount() >= stack.getMaxStackSize()) {
                    continue;
                }
                if (stack.getCount() > this.threshold.getValue()) {
                    continue;
                }
                final int inventorySlot = this.findCompatibleInventorySlot(stack);
                if (inventorySlot == -1) {
                    continue;
                }
                returnPair = new Pair<Integer, Integer>(inventorySlot, hotbarSlot.getKey());
            }
        }
        return returnPair;
    }
    
    private int findCompatibleInventorySlot(final ItemStack hotbarStack) {
        int inventorySlot = -1;
        int smallestStackSize = 999;
        for (final Map.Entry<Integer, ItemStack> entry : this.getInventory().entrySet()) {
            final ItemStack inventoryStack = entry.getValue();
            if (!inventoryStack.isEmpty()) {
                if (inventoryStack.getItem() == Items.AIR) {
                    continue;
                }
                if (!this.isCompatibleStacks(hotbarStack, inventoryStack)) {
                    continue;
                }
                final int currentStackSize = ((ItemStack)this.mc.player.inventoryContainer.getInventory().get((int)entry.getKey())).getCount();
                if (smallestStackSize <= currentStackSize) {
                    continue;
                }
                smallestStackSize = currentStackSize;
                inventorySlot = entry.getKey();
            }
        }
        return inventorySlot;
    }
    
    private boolean isCompatibleStacks(final ItemStack stack1, final ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }
        if (stack1.getItem() instanceof ItemBlock && stack2.getItem() instanceof ItemBlock) {
            final Block block1 = ((ItemBlock)stack1.getItem()).getBlock();
            final Block block2 = ((ItemBlock)stack2.getItem()).getBlock();
            if (!block1.getMaterial(block1.getBlockState().getBaseState()).equals(block2.getMaterial(block2.getBlockState().getBaseState()))) {
                return false;
            }
        }
        return this.stackEqualExact(stack1, stack2) && stack1.getItemDamage() == stack2.getItemDamage();
    }
    
    private boolean stackEqualExact(final ItemStack stack1, final ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }
    
    private static class Pair<K, V>
    {
        final K key;
        final V value;
        
        public Pair(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        public V getValue() {
            return this.value;
        }
        
        public K getKey() {
            return this.key;
        }
    }
}
