package me.hollow.trollgod.client.managers;

import me.hollow.trollgod.api.interfaces.*;
import net.minecraft.entity.item.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.entity.*;

public class SafeManager implements Minecraftable
{
    private boolean safe;
    
    public void update() {
        this.safe = true;
        float maxDamage = 0.5f;
        for (int size = SafeManager.mc.world.loadedEntityList.size(), i = 0; i < size; ++i) {
            final Entity entity = SafeManager.mc.world.loadedEntityList.get(i);
            if (entity instanceof EntityEnderCrystal) {
                if (SafeManager.mc.player.getDistanceSq(entity) <= 100.0) {
                    final float damage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, (EntityLivingBase)SafeManager.mc.player);
                    if (damage >= maxDamage) {
                        maxDamage = damage;
                        if (damage + 2.0f >= EntityUtil.getHealth((EntityLivingBase)SafeManager.mc.player)) {
                            this.safe = false;
                        }
                    }
                }
            }
        }
    }
    
    public boolean isSafe() {
        return this.safe;
    }
}
