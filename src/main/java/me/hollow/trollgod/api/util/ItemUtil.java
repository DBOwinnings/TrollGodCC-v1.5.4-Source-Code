package me.hollow.trollgod.api.util;

import me.hollow.trollgod.api.interfaces.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;

public class ItemUtil implements Minecraftable
{
    public static int getItemFromHotbar(final Item item) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem() == item) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static int getItemFromHotbar(final Class clazz) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem().getClass() == clazz) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return getInventorySlots(9, 44);
    }
    
    private static Map<Integer, ItemStack> getInventorySlots(final int currentI, final int last) {
        int current = currentI;
        final Map<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        while (current <= last) {
            fullInventorySlots.put(current, (ItemStack)ItemUtil.mc.player.inventoryContainer.getInventory().get(current));
            ++current;
        }
        return fullInventorySlots;
    }
    
    public static int getBlockFromHotbar(final Block block) {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem() == Item.getItemFromBlock(block)) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static int getItemSlot(final Class clss) {
        int itemSlot = -1;
        for (int i = 45; i > 0; --i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem().getClass() == clss) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }
    
    public static int getItemSlot(final Item item) {
        int itemSlot = -1;
        for (int i = 45; i > 0; --i) {
            if (ItemUtil.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }
    
    public static int getItemCount(final Item item) {
        int count = 0;
        for (int size = ItemUtil.mc.player.inventory.mainInventory.size(), i = 0; i < size; ++i) {
            final ItemStack itemStack = (ItemStack)ItemUtil.mc.player.inventory.mainInventory.get(i);
            if (itemStack.getItem() == item) {
                count += itemStack.getCount();
            }
        }
        final ItemStack offhandStack = ItemUtil.mc.player.getHeldItemOffhand();
        if (offhandStack.getItem() == item) {
            count += offhandStack.getCount();
        }
        return count;
    }
    
    public static boolean isArmorLow(final EntityPlayer player, final int durability) {
        for (int i = 0; i < 4; ++i) {
            if (getDamageInPercent((ItemStack)player.inventory.armorInventory.get(i)) < durability) {
                return true;
            }
        }
        return false;
    }
    
    public static int getItemDamage(final ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage();
    }
    
    public static float getDamageInPercent(final ItemStack stack) {
        final float green = (stack.getMaxDamage() - (float)stack.getItemDamage()) / stack.getMaxDamage();
        final float red = 1.0f - green;
        return (float)(100 - (int)(red * 100.0f));
    }
    
    public static int getRoundedDamage(final ItemStack stack) {
        return (int)getDamageInPercent(stack);
    }
    
    public static boolean hasDurability(final ItemStack stack) {
        final Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }
}
