package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.entity.player.*;
import me.hollow.trollgod.api.mixin.mixins.render.*;
import java.util.*;
import net.minecraft.entity.*;
import me.hollow.trollgod.*;
import java.awt.*;
import me.hollow.trollgod.client.modules.client.*;
import net.minecraft.client.model.*;
import me.hollow.trollgod.api.util.render.*;
import org.lwjgl.opengl.*;
import net.minecraft.util.math.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;

@ModuleManifest(label = "Skeleton", listen = false, category = Category.VISUAL, color = 6750156)
public final class Skeleton extends Module
{
    private final Setting<Float> lineWidth;
    private final Map<EntityPlayer, float[][]> rotationList;
    public static Skeleton INSTANCE;
    private final AccessorRenderManager renderManager;
    
    public Skeleton() {
        this.lineWidth = (Setting<Float>)this.register(new Setting("LineWidth", (T)1.5f, (T)0.1f, (T)5.0f));
        this.rotationList = new HashMap<EntityPlayer, float[][]>();
        this.renderManager = (AccessorRenderManager)this.mc.getRenderManager();
        Skeleton.INSTANCE = this;
    }
    
    @Override
    public final void onRender3D() {
        for (int size = this.mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer player = this.mc.world.playerEntities.get(i);
            if (player != null && player != this.mc.player && player.isEntityAlive() && !player.isPlayerSleeping() && this.rotationList.get(player) != null && this.mc.player.getDistanceSq((Entity)player) < 2500.0) {
                this.renderSkeleton(player, this.rotationList.get(player), TrollGod.INSTANCE.getFriendManager().isFriend(player) ? new Color(-11157267) : new Color(Colours.INSTANCE.getColor()));
            }
        }
    }
    
    public final void onRenderModel(final ModelBase modelBase, final Entity entity) {
        if (entity instanceof EntityPlayer && modelBase instanceof ModelBiped) {
            this.rotationList.put((EntityPlayer)entity, getBipedRotations((ModelBiped)modelBase));
        }
    }
    
    private void renderSkeleton(final EntityPlayer player, final float[][] rotations, final Color color) {
        RenderUtil.GLPre(this.lineWidth.getValue());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
        final Vec3d interp = this.getInterpolatedRenderPos((Entity)player, this.mc.getRenderPartialTicks());
        GlStateManager.translate(interp.x, interp.y, interp.z);
        GlStateManager.rotate(-player.renderYawOffset, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(0.0, 0.0, player.isSneaking() ? -0.235 : 0.0);
        final float sneak = player.isSneaking() ? 0.6f : 0.75f;
        if (player.isElytraFlying()) {
            final float f = player.getTicksElytraFlying() + this.mc.getRenderPartialTicks();
            final float f2 = MathHelper.clamp(f * f / 100.0f, 0.0f, 1.0f);
            GlStateManager.rotate(f2 * (90.0f - -player.rotationPitch), 1.0f, 0.0f, 0.0f);
            final Vec3d vec3d = player.getLook(this.mc.getRenderPartialTicks());
            final double d0 = player.motionX * player.motionX + player.motionZ * player.motionZ;
            final double d2 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            if (d0 > 0.0 && d2 > 0.0) {
                final double d3 = (player.motionX * vec3d.x + player.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d2));
                final double d4 = player.motionX * vec3d.z - player.motionZ * vec3d.x;
                GlStateManager.rotate((float)(Math.signum(d4) * Math.acos(d3)) * 180.0f / 3.1415927f, 0.0f, 1.0f, 0.0f);
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.125, (double)sneak, 0.0);
        if (rotations[3][0] != 0.0f) {
            GlStateManager.rotate(rotations[3][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        if (rotations[3][1] != 0.0f) {
            GlStateManager.rotate(rotations[3][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (rotations[3][2] != 0.0f) {
            GlStateManager.rotate(rotations[3][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.glBegin(3);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d(0.0, (double)(-sneak));
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.125, (double)sneak, 0.0);
        if (rotations[4][0] != 0.0f) {
            GlStateManager.rotate(rotations[4][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        if (rotations[4][1] != 0.0f) {
            GlStateManager.rotate(rotations[4][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (rotations[4][2] != 0.0f) {
            GlStateManager.rotate(rotations[4][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.glBegin(3);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d(0.0, (double)(-sneak));
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.translate(0.0, 0.0, player.isSneaking() ? 0.25 : 0.0);
        GlStateManager.pushMatrix();
        double sneakOffset = 0.0;
        if (player.isSneaking()) {
            sneakOffset = -0.05;
        }
        GlStateManager.translate(0.0, sneakOffset, player.isSneaking() ? -0.01725 : 0.0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.375, sneak + 0.55, 0.0);
        if (rotations[1][0] != 0.0f) {
            GlStateManager.rotate(rotations[1][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        if (rotations[1][1] != 0.0f) {
            GlStateManager.rotate(rotations[1][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (rotations[1][2] != 0.0f) {
            GlStateManager.rotate(-rotations[1][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.glBegin(3);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d(0.0, -0.5);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.375, sneak + 0.55, 0.0);
        if (rotations[2][0] != 0.0f) {
            GlStateManager.rotate(rotations[2][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        if (rotations[2][1] != 0.0f) {
            GlStateManager.rotate(rotations[2][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (rotations[2][2] != 0.0f) {
            GlStateManager.rotate(-rotations[2][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        GlStateManager.glBegin(3);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d(0.0, -0.5);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, sneak + 0.55, 0.0);
        if (rotations[0][0] != 0.0f) {
            GlStateManager.rotate(rotations[0][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
        GlStateManager.glBegin(3);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d(0.0, 0.3);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.rotate(player.isSneaking() ? 25.0f : 0.0f, 1.0f, 0.0f, 0.0f);
        if (player.isSneaking()) {
            sneakOffset = -0.16175;
        }
        GlStateManager.translate(0.0, sneakOffset, player.isSneaking() ? -0.48025 : 0.0);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, (double)sneak, 0.0);
        GlStateManager.glBegin(3);
        GL11.glVertex2d(-0.125, 0.0);
        GL11.glVertex2d(0.125, 0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, (double)sneak, 0.0);
        GlStateManager.glBegin(3);
        GL11.glVertex2d(0.0, 0.0);
        GL11.glVertex2d(0.0, 0.55);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, sneak + 0.55, 0.0);
        this.drawLineVbo(-0.375, 0.0, 0.375, 0.0);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        RenderUtil.GlPost();
    }
    
    private void drawLineVbo(final double x, final double y, final double x1, final double y2) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder builder = tessellator.getBuffer();
        builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y, 0.0).endVertex();
        builder.pos(x1, y2, 0.0).endVertex();
        tessellator.draw();
    }
    
    public static float[][] getBipedRotations(final ModelBiped biped) {
        final float[][] rotations = new float[5][];
        final float[] headRotation = { biped.bipedHead.rotateAngleX, biped.bipedHead.rotateAngleY, biped.bipedHead.rotateAngleZ };
        rotations[0] = headRotation;
        final float[] rightArmRotation = { biped.bipedRightArm.rotateAngleX, biped.bipedRightArm.rotateAngleY, biped.bipedRightArm.rotateAngleZ };
        rotations[1] = rightArmRotation;
        final float[] leftArmRotation = { biped.bipedLeftArm.rotateAngleX, biped.bipedLeftArm.rotateAngleY, biped.bipedLeftArm.rotateAngleZ };
        rotations[2] = leftArmRotation;
        final float[] rightLegRotation = { biped.bipedRightLeg.rotateAngleX, biped.bipedRightLeg.rotateAngleY, biped.bipedRightLeg.rotateAngleZ };
        rotations[3] = rightLegRotation;
        final float[] leftLegRotation = { biped.bipedLeftLeg.rotateAngleX, biped.bipedLeftLeg.rotateAngleY, biped.bipedLeftLeg.rotateAngleZ };
        rotations[4] = leftLegRotation;
        return rotations;
    }
    
    public static Vec3d getInterpolatedPos(final Entity entity, final float partialTicks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, partialTicks));
    }
    
    public Vec3d getInterpolatedRenderPos(final Entity entity, final float partialTicks) {
        return getInterpolatedPos(entity, partialTicks).subtract(this.renderManager.getRenderPosX(), this.renderManager.getRenderPosY(), this.renderManager.getRenderPosZ());
    }
    
    public static Vec3d getInterpolatedAmount(final Entity entity, final double x, final double y, final double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }
    
    public static Vec3d getInterpolatedAmount(final Entity entity, final float partialTicks) {
        return getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
    }
}
