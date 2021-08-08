package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.item.*;
import me.hollow.trollgod.client.managers.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import tcb.bces.listener.*;
import java.util.function.*;
import me.hollow.trollgod.api.util.*;
import java.util.*;
import net.minecraft.entity.passive.*;
import me.hollow.trollgod.*;

@ModuleManifest(label = "KillAura", color = 34, category = Category.COMBAT)
public class KillAura extends Module
{
    private final Setting<Boolean> rotate;
    private final Setting<Boolean> tpsSync;
    private final Setting<TargetSortingMode> sortMode;
    private final Setting<Float> range;
    private final Setting<SwordMode> sword;
    private final Setting<Boolean> players;
    private final Setting<Boolean> pigs;
    public static KillAura INSTANCE;
    
    public KillAura() {
        this.rotate = (Setting<Boolean>)this.register(new Setting("Rotate", (T)false));
        this.tpsSync = (Setting<Boolean>)this.register(new Setting("TPS-Sync", (T)true));
        this.sortMode = (Setting<TargetSortingMode>)this.register(new Setting("Sort by", (T)TargetSortingMode.DISTANCE));
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)5.0f, (T)1.0f, (T)6.0f));
        this.sword = (Setting<SwordMode>)this.register(new Setting("Sword", (T)SwordMode.REQUIRE));
        this.players = (Setting<Boolean>)this.register(new Setting("Players", (T)true));
        this.pigs = (Setting<Boolean>)this.register(new Setting("Pigs", (T)true));
        KillAura.INSTANCE = this;
    }
    
    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        final EntityLivingBase target = this.getTarget();
        if (target != null) {
            switch (this.sword.getValue()) {
                case SWITCH: {
                    final int swordSlot = ItemUtil.getItemFromHotbar(ItemSword.class);
                    if (swordSlot == -1) {
                        return;
                    }
                    this.mc.player.inventory.currentItem = swordSlot;
                    this.mc.playerController.updateController();
                    break;
                }
                case REQUIRE: {
                    if (!(this.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword)) {
                        return;
                    }
                    break;
                }
            }
            final float ticks = 20.0f - TPSManager.TPS;
            if (this.mc.player.getCooledAttackStrength(((boolean)this.tpsSync.getValue()) ? (-ticks) : 0.0f) >= 1.0f) {
                if (this.rotate.getValue()) {
                    final float[] rotations = this.getRotations((Entity)target);
                    this.mc.player.rotationYaw = rotations[0];
                    this.mc.player.rotationPitch = rotations[1];
                }
                this.mc.playerController.attackEntity((EntityPlayer)this.mc.player, (Entity)target);
                this.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }
    
    public EntityLivingBase getTarget() {
        return (EntityLivingBase)this.mc.world.loadedEntityList.stream().filter(this::isValid).min(Comparator.comparingDouble(e -> (this.sortMode.getValue() == TargetSortingMode.DISTANCE) ? this.mc.player.getDistanceSq((Entity)e) : EntityUtil.getHealth(e))).orElse(null);
    }
    
    private boolean isValid(final Entity entity) {
        return entity != this.mc.player && ((entity instanceof EntityPlayer && this.players.getValue()) || (entity instanceof EntityPig && this.pigs.getValue())) && this.mc.player.getDistance(entity) <= this.range.getValue() && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0.0f && !TrollGod.INSTANCE.getFriendManager().isFriend(entity.getName());
    }
    
    public float[] getRotations(final Entity ent) {
        return this.getRotationFromPosition(ent.posX, ent.getEntityBoundingBox().maxY - 4.0, ent.posZ);
    }
    
    public float[] getRotationFromPosition(final double x, final double y, final double z) {
        final double xDiff = x - this.mc.player.posX;
        final double zDiff = z - this.mc.player.posZ;
        final double yDiff = y - this.mc.player.posY;
        final double hypotenuse = Math.hypot(xDiff, zDiff);
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-Math.atan2(yDiff, hypotenuse) * 180.0 / 3.141592653589793);
        return new float[] { yaw, pitch };
    }
    
    public enum SwordMode
    {
        REQUIRE, 
        SWITCH, 
        NONE;
    }
    
    public enum TargetSortingMode
    {
        HEALTH, 
        DISTANCE;
    }
}
