/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.movement;

import java.util.Objects;
import me.hollow.trollgod.client.events.MoveEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="Strafe", category=Module.Category.MOVEMENT, color=-5602)
public class Strafe
extends Module {
    @Subscribe
    public void onMotion(MoveEvent event) {
        if (!(this.mc.player.isSneaking() || this.mc.player.isInWater() || this.mc.player.isInLava() || this.mc.player.movementInput.moveForward == 0.0f && this.mc.player.movementInput.moveStrafe == 0.0f)) {
            MovementInput movementInput = this.mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = this.mc.player.rotationYaw;
            if ((double)moveForward == 0.0 && (double)moveStrafe == 0.0) {
                event.setMotionX(0.0);
                event.setMotionZ(0.0);
            } else {
                if ((double)moveForward != 0.0) {
                    if ((double)moveStrafe > 0.0) {
                        rotationYaw += (float)((double)moveForward > 0.0 ? -45 : 45);
                    } else if ((double)moveStrafe < 0.0) {
                        rotationYaw += (float)((double)moveForward > 0.0 ? 45 : -45);
                    }
                    moveStrafe = 0.0f;
                    float f = moveForward == 0.0f ? moveForward : (moveForward = (double)moveForward > 0.0 ? 1.0f : -1.0f);
                }
                moveStrafe = moveStrafe == 0.0f ? moveStrafe : ((double)moveStrafe > 0.0 ? 1.0f : -1.0f);
                double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
                double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
                event.setMotionX((double)moveForward * this.getMaxSpeed() * cos + (double)moveStrafe * this.getMaxSpeed() * sin);
                event.setMotionZ((double)moveForward * this.getMaxSpeed() * sin - (double)moveStrafe * this.getMaxSpeed() * cos);
            }
        }
    }

    public double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (this.mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById((int)1)))) {
            maxModifier *= 1.0 + 0.2 * (double)(Objects.requireNonNull(this.mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById((int)1)))).getAmplifier() + 1);
        }
        return maxModifier;
    }
}

