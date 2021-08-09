/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.client.events.MoveEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="SpeedTest", category=Module.Category.MOVEMENT)
public class SpeedTest
extends Module {
    private final double ACCELERATION = 1.65;
    private final double AIR_FRICTION = 0.66;
    private boolean previouslyOnGround;
    private double speed;
    private int stage;
    private double lastDistance;
    private static final double WALK_SPEED = 0.21585;
    private static final double RUN_SPEED = 0.2806;

    @Override
    public void onEnable() {
        this.speed = 0.0;
    }

    @Subscribe
    public void onPlayerMotionUpdateEvent(MoveEvent event) {
        if (!this.hasMovementInput()) {
            this.speed = this.getMovementSpeed();
            event.setMotionX(0.0);
            event.setMotionZ(0.0);
            return;
        }
        if (this.onGround()) {
            this.speed *= 1.57;
            this.mc.player.motionY = 0.412;
            event.setMotionY(0.412);
        } else {
            this.speed = this.previouslyOnGround ? (this.speed -= 0.66 * (this.speed - this.getMovementSpeed())) : (this.speed -= this.speed / 159.0);
        }
        this.previouslyOnGround = this.onGround();
        this.speed = Math.max(this.speed, this.getMovementSpeed());
        this.speed = Math.min(this.speed, 0.547);
        this.mc.player.motionX = -(Math.sin(this.getDirection()) * this.speed);
        event.setMotionX(this.mc.player.motionX);
        this.mc.player.motionZ = Math.cos(this.getDirection()) * this.speed;
        event.setMotionZ(this.mc.player.motionZ);
    }

    public boolean hasMovementInput() {
        return this.mc.player.movementInput.moveForward != 0.0f || this.mc.player.movementInput.moveStrafe != 0.0f;
    }

    private double getBaseMovementSpeed() {
        if (this.mc.player.isSneaking()) {
            return 0.064755;
        }
        return this.mc.player.isSprinting() ? 0.2806 : 0.21585;
    }

    public double getMovementSpeed() {
        if (!this.mc.player.isPotionActive(MobEffects.SPEED)) {
            return this.getBaseMovementSpeed();
        }
        return this.getBaseMovementSpeed() * 1.0 + 0.06 * (double)(this.mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1);
    }

    public boolean onGround() {
        return this.mc.player.onGround;
    }

    public double getSpeed() {
        return Math.hypot(this.mc.player.motionX, this.mc.player.motionZ);
    }

    public void setSpeed(double speed) {
        EntityPlayerSP player = this.mc.player;
        player.motionX = -(Math.sin(this.getDirection()) * speed);
        player.motionZ = Math.cos(this.getDirection()) * speed;
    }

    public double getDirection() {
        EntityPlayerSP player = this.mc.player;
        float direction = player.rotationYaw;
        if (player.moveForward < 0.0f) {
            direction += 180.0f;
        }
        float forward = 1.0f;
        forward = player.moveForward < 0.0f ? -0.5f : (player.moveForward > 0.0f ? 0.5f : 1.0f);
        if (player.moveStrafing > 0.0f) {
            direction -= 90.0f * forward;
        } else if (player.moveStrafing < 0.0f) {
            direction += 90.0f * forward;
        }
        return direction *= (float)Math.PI / 180;
    }
}

