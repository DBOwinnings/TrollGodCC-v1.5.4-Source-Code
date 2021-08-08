package me.hollow.trollgod.client.managers;

import me.hollow.trollgod.api.interfaces.*;
import net.minecraft.util.math.*;

public class SpeedManager implements Minecraftable
{
    public double speedometerCurrentSpeed;
    
    public SpeedManager() {
        this.speedometerCurrentSpeed = 0.0;
    }
    
    public void update() {
        final double distTraveledLastTickX = SpeedManager.mc.player.posX - SpeedManager.mc.player.prevPosX;
        final double distTraveledLastTickZ = SpeedManager.mc.player.posZ - SpeedManager.mc.player.prevPosZ;
        this.speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
    }
    
    public double turnIntoKpH(final double input) {
        return MathHelper.sqrt(input) * 71.2729367892;
    }
    
    public double getKpH() {
        double speedometerkphdouble = this.turnIntoKpH(this.speedometerCurrentSpeed);
        speedometerkphdouble = Math.round(10.0 * speedometerkphdouble) / 10.0;
        return speedometerkphdouble;
    }
}
