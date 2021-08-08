package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.client.events.*;
import tcb.bces.listener.*;
import net.minecraft.init.*;
import net.minecraft.entity.*;
import net.minecraft.client.entity.*;

@ModuleManifest(label = "SpeedTest", category = Category.MOVEMENT)
public class SpeedTest extends Module
{
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
    public void onPlayerMotionUpdateEvent(final MoveEvent event) {
        if (!this.hasMovementInput()) {
            this.speed = this.getMovementSpeed();
            event.setMotionX(0.0);
            event.setMotionZ(0.0);
            return;
        }
        if (this.onGround()) {
            this.speed *= 1.57;
            event.setMotionY(this.mc.player.motionY = 0.412);
        }
        else if (this.previouslyOnGround) {
            this.speed -= 0.66 * (this.speed - this.getMovementSpeed());
        }
        else {
            this.speed -= this.speed / 159.0;
        }
        this.previouslyOnGround = this.onGround();
        this.speed = Math.max(this.speed, this.getMovementSpeed());
        this.speed = Math.min(this.speed, 0.547);
        event.setMotionX(this.mc.player.motionX = -(Math.sin(this.getDirection()) * this.speed));
        event.setMotionZ(this.mc.player.motionZ = Math.cos(this.getDirection()) * this.speed);
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
        return this.getBaseMovementSpeed() * 1.0 + 0.06 * (this.mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier() + 1);
    }
    
    public boolean onGround() {
        return this.mc.player.onGround;
    }
    
    public double getSpeed() {
        return Math.hypot(this.mc.player.motionX, this.mc.player.motionZ);
    }
    
    public void setSpeed(final double speed) {
        final Entity player = (Entity)this.mc.player;
        player.motionX = -(Math.sin(this.getDirection()) * speed);
        player.motionZ = Math.cos(this.getDirection()) * speed;
    }
    
    public double getDirection() {
        final EntityPlayerSP player = this.mc.player;
        float direction = player.rotationYaw;
        if (player.moveForward < 0.0f) {
            direction += 180.0f;
        }
        float forward = 1.0f;
        if (player.moveForward < 0.0f) {
            forward = -0.5f;
        }
        else if (player.moveForward > 0.0f) {
            forward = 0.5f;
        }
        else {
            forward = 1.0f;
        }
        if (player.moveStrafing > 0.0f) {
            direction -= 90.0f * forward;
        }
        else if (player.moveStrafing < 0.0f) {
            direction += 90.0f * forward;
        }
        direction *= 0.017453292f;
        return direction;
    }
}
