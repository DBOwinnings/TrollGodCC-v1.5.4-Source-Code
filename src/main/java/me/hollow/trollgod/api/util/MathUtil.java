package me.hollow.trollgod.api.util;

import me.hollow.trollgod.api.interfaces.*;
import java.text.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.util.*;

public class MathUtil implements Minecraftable
{
    private static final DecimalFormat formatter;
    
    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }
    
    public static double[] directionSpeed(final double speed) {
        float forward = MathUtil.mc.player.movementInput.moveForward;
        float side = MathUtil.mc.player.movementInput.moveStrafe;
        float yaw = MathUtil.mc.player.prevRotationYaw + (MathUtil.mc.player.rotationYaw - MathUtil.mc.player.prevRotationYaw) * MathUtil.mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }
    
    public static float getSpeedInKMH() {
        final double deltaX = MathUtil.mc.player.posX - MathUtil.mc.player.prevPosX;
        final double deltaZ = MathUtil.mc.player.posZ - MathUtil.mc.player.prevPosZ;
        final float distance = MathHelper.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        final double kmh = Math.floor(distance / 1000.0f / 1.3888889E-5f);
        String format = MathUtil.formatter.format(kmh);
        if (!format.contains(".")) {
            format += ".0";
        }
        return Float.parseFloat(format);
    }
    
    public static int clamp(final int num, final int min, final int max) {
        return (num < min) ? min : Math.min(num, max);
    }
    
    public static double[] dirSpeedNew(final double speed) {
        float moveForward = MathUtil.mc.player.movementInput.moveForward;
        float moveStrafe = MathUtil.mc.player.movementInput.moveStrafe;
        float rotationYaw = MathUtil.mc.player.prevRotationYaw + (MathUtil.mc.player.rotationYaw - MathUtil.mc.player.prevRotationYaw) * MathUtil.mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
            }
            else if (moveStrafe < 0.0f) {
                rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            }
            else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        final double posX = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        final double posZ = moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[] { posX, posZ };
    }
    
    public static double[] futureCalc1(final double speed) {
        final MovementInput movementInput = MathUtil.mc.player.movementInput;
        double forward = movementInput.moveForward;
        double strafe = movementInput.moveStrafe;
        float yaw = MathUtil.mc.player.rotationYaw;
        double x;
        double z;
        if (forward == 0.0 && strafe == 0.0) {
            z = (x = 0.0);
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            z = forward * speed * cos + strafe * speed * sin;
            x = forward * speed * sin - strafe * speed * cos;
        }
        return new double[] { z, x };
    }
    
    static {
        formatter = new DecimalFormat("#.#");
    }
}
