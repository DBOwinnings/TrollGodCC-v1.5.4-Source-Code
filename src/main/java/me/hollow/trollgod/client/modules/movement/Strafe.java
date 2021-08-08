package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.util.*;
import tcb.bces.listener.*;
import java.util.*;
import net.minecraft.potion.*;

@ModuleManifest(label = "Strafe", category = Category.MOVEMENT, color = -5602)
public class Strafe extends Module
{
    @Subscribe
    public void onMotion(final MoveEvent event) {
        if (!this.mc.player.isSneaking() && !this.mc.player.isInWater() && !this.mc.player.isInLava() && (this.mc.player.movementInput.moveForward != 0.0f || this.mc.player.movementInput.moveStrafe != 0.0f)) {
            final MovementInput movementInput = this.mc.player.movementInput;
            float moveForward = movementInput.moveForward;
            float moveStrafe = movementInput.moveStrafe;
            float rotationYaw = this.mc.player.rotationYaw;
            if (moveForward == 0.0 && moveStrafe == 0.0) {
                event.setMotionX(0.0);
                event.setMotionZ(0.0);
            }
            else {
                if (moveForward != 0.0) {
                    if (moveStrafe > 0.0) {
                        rotationYaw += ((moveForward > 0.0) ? -45 : 45);
                    }
                    else if (moveStrafe < 0.0) {
                        rotationYaw += ((moveForward > 0.0) ? 45 : -45);
                    }
                    moveStrafe = 0.0f;
                    moveForward = ((moveForward == 0.0f) ? moveForward : ((moveForward > 0.0) ? 1.0f : -1.0f));
                }
                moveStrafe = ((moveStrafe == 0.0f) ? moveStrafe : ((moveStrafe > 0.0) ? 1.0f : -1.0f));
                final double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
                final double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
                event.setMotionX(moveForward * this.getMaxSpeed() * cos + moveStrafe * this.getMaxSpeed() * sin);
                event.setMotionZ(moveForward * this.getMaxSpeed() * sin - moveStrafe * this.getMaxSpeed() * cos);
            }
        }
    }
    
    public double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (this.mc.player.isPotionActive((Potion)Objects.requireNonNull(Potion.getPotionById(1)))) {
            maxModifier *= 1.0 + 0.2 * (Objects.requireNonNull(this.mc.player.getActivePotionEffect((Potion)Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier() + 1);
        }
        return maxModifier;
    }
}
