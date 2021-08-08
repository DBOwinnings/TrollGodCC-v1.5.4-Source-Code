package me.hollow.trollgod.client.managers;

import me.hollow.trollgod.api.interfaces.*;

public class RotationManager implements Minecraftable
{
    private float yaw;
    private float pitch;
    
    public void updateRotations() {
        this.yaw = RotationManager.mc.player.rotationYaw;
        this.pitch = RotationManager.mc.player.rotationPitch;
    }
    
    public void restoreRotations() {
        RotationManager.mc.player.rotationYaw = this.yaw;
        RotationManager.mc.player.rotationYawHead = this.yaw;
        RotationManager.mc.player.rotationPitch = this.pitch;
    }
    
    public void setPlayerRotations(final float yaw, final float pitch) {
        RotationManager.mc.player.rotationYaw = yaw;
        RotationManager.mc.player.rotationYawHead = yaw;
        RotationManager.mc.player.rotationPitch = pitch;
    }
    
    public void setPitch(final float pitch) {
        RotationManager.mc.player.rotationPitch = pitch;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public float getYaw() {
        return this.yaw;
    }
}
