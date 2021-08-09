/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import me.hollow.trollgod.client.modules.client.Colours;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

@ModuleManifest(label="Skeleton", listen=false, category=Module.Category.VISUAL, color=0x66FFCC)
public final class Skeleton
extends Module {
    private final Setting<Float> lineWidth = this.register(new Setting<Float>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f)));
    private final Map<EntityPlayer, float[][]> rotationList = new HashMap<EntityPlayer, float[][]>();
    public static Skeleton INSTANCE;
    private final AccessorRenderManager renderManager = (AccessorRenderManager)this.mc.getRenderManager();

    public Skeleton() {
        INSTANCE = this;
    }

    @Override
    public final void onRender3D() {
        int size = this.mc.world.playerEntities.size();
        for (int i = 0; i < size; ++i) {
            EntityPlayer player = (EntityPlayer)this.mc.world.playerEntities.get(i);
            if (player == null || player == this.mc.player || !player.isEntityAlive() || player.isPlayerSleeping() || this.rotationList.get(player) == null || !(this.mc.player.getDistanceSq((Entity)player) < 2500.0)) continue;
            this.renderSkeleton(player, this.rotationList.get(player), TrollGod.INSTANCE.getFriendManager().isFriend(player) ? new Color(-11157267) : new Color(Colours.INSTANCE.getColor()));
        }
    }

    public final void onRenderModel(ModelBase modelBase, Entity entity) {
        if (entity instanceof EntityPlayer && modelBase instanceof ModelBiped) {
            this.rotationList.put((EntityPlayer)entity, Skeleton.getBipedRotations((ModelBiped)modelBase));
        }
    }

    private void renderSkeleton(EntityPlayer player, float[][] rotations, Color color) {
        float sneak;
        RenderUtil.GLPre(this.lineWidth.getValue().floatValue());
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.color((float)((float)color.getRed() / 255.0f), (float)((float)color.getGreen() / 255.0f), (float)((float)color.getBlue() / 255.0f), (float)((float)color.getAlpha() / 255.0f));
        Vec3d interp = this.getInterpolatedRenderPos((Entity)player, this.mc.getRenderPartialTicks());
        GlStateManager.translate((double)interp.x, (double)interp.y, (double)interp.z);
        GlStateManager.rotate((float)(-player.renderYawOffset), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((double)0.0, (double)0.0, (double)(player.isSneaking() ? -0.235 : 0.0));
        float f = sneak = player.isSneaking() ? 0.6f : 0.75f;
        if (player.isElytraFlying()) {
            float f2 = (float)player.getTicksElytraFlying() + this.mc.getRenderPartialTicks();
            float f1 = MathHelper.clamp((float)(f2 * f2 / 100.0f), (float)0.0f, (float)1.0f);
            GlStateManager.rotate((float)(f1 * (90.0f - -player.rotationPitch)), (float)1.0f, (float)0.0f, (float)0.0f);
            Vec3d vec3d = player.getLook(this.mc.getRenderPartialTicks());
            double d0 = player.motionX * player.motionX + player.motionZ * player.motionZ;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            if (d0 > 0.0 && d1 > 0.0) {
                double d2 = (player.motionX * vec3d.x + player.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = player.motionX * vec3d.z - player.motionZ * vec3d.x;
                GlStateManager.rotate((float)((float)(Math.signum(d3) * Math.acos(d2)) * 180.0f / (float)Math.PI), (float)0.0f, (float)1.0f, (float)0.0f);
            }
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)-0.125, (double)sneak, (double)0.0);
        if (rotations[3][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[3][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (rotations[3][1] != 0.0f) {
            GlStateManager.rotate((float)(rotations[3][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (rotations[3][2] != 0.0f) {
            GlStateManager.rotate((float)(rotations[3][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex2d((double)0.0, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)(-sneak));
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.125, (double)sneak, (double)0.0);
        if (rotations[4][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[4][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (rotations[4][1] != 0.0f) {
            GlStateManager.rotate((float)(rotations[4][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (rotations[4][2] != 0.0f) {
            GlStateManager.rotate((float)(rotations[4][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex2d((double)0.0, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)(-sneak));
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.translate((double)0.0, (double)0.0, (double)(player.isSneaking() ? 0.25 : 0.0));
        GlStateManager.pushMatrix();
        double sneakOffset = 0.0;
        if (player.isSneaking()) {
            sneakOffset = -0.05;
        }
        GlStateManager.translate((double)0.0, (double)sneakOffset, (double)(player.isSneaking() ? -0.01725 : 0.0));
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)-0.375, (double)((double)sneak + 0.55), (double)0.0);
        if (rotations[1][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[1][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (rotations[1][1] != 0.0f) {
            GlStateManager.rotate((float)(rotations[1][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (rotations[1][2] != 0.0f) {
            GlStateManager.rotate((float)(-rotations[1][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex2d((double)0.0, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)-0.5);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.375, (double)((double)sneak + 0.55), (double)0.0);
        if (rotations[2][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[2][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        if (rotations[2][1] != 0.0f) {
            GlStateManager.rotate((float)(rotations[2][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
        }
        if (rotations[2][2] != 0.0f) {
            GlStateManager.rotate((float)(-rotations[2][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex2d((double)0.0, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)-0.5);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.0, (double)((double)sneak + 0.55), (double)0.0);
        if (rotations[0][0] != 0.0f) {
            GlStateManager.rotate((float)(rotations[0][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
        }
        GlStateManager.glBegin((int)3);
        GL11.glVertex2d((double)0.0, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)0.3);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        GlStateManager.rotate((float)(player.isSneaking() ? 25.0f : 0.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        if (player.isSneaking()) {
            sneakOffset = -0.16175;
        }
        GlStateManager.translate((double)0.0, (double)sneakOffset, (double)(player.isSneaking() ? -0.48025 : 0.0));
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.0, (double)sneak, (double)0.0);
        GlStateManager.glBegin((int)3);
        GL11.glVertex2d((double)-0.125, (double)0.0);
        GL11.glVertex2d((double)0.125, (double)0.0);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.0, (double)sneak, (double)0.0);
        GlStateManager.glBegin((int)3);
        GL11.glVertex2d((double)0.0, (double)0.0);
        GL11.glVertex2d((double)0.0, (double)0.55);
        GlStateManager.glEnd();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)0.0, (double)((double)sneak + 0.55), (double)0.0);
        this.drawLineVbo(-0.375, 0.0, 0.375, 0.0);
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        RenderUtil.GlPost();
    }

    private void drawLineVbo(double x, double y, double x1, double y2) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x, y, 0.0).endVertex();
        builder.pos(x1, y2, 0.0).endVertex();
        tessellator.draw();
    }

    public static float[][] getBipedRotations(ModelBiped biped) {
        float[][] rotations = new float[5][];
        float[] headRotation = new float[]{biped.bipedHead.rotateAngleX, biped.bipedHead.rotateAngleY, biped.bipedHead.rotateAngleZ};
        rotations[0] = headRotation;
        float[] rightArmRotation = new float[]{biped.bipedRightArm.rotateAngleX, biped.bipedRightArm.rotateAngleY, biped.bipedRightArm.rotateAngleZ};
        rotations[1] = rightArmRotation;
        float[] leftArmRotation = new float[]{biped.bipedLeftArm.rotateAngleX, biped.bipedLeftArm.rotateAngleY, biped.bipedLeftArm.rotateAngleZ};
        rotations[2] = leftArmRotation;
        float[] rightLegRotation = new float[]{biped.bipedRightLeg.rotateAngleX, biped.bipedRightLeg.rotateAngleY, biped.bipedRightLeg.rotateAngleZ};
        rotations[3] = rightLegRotation;
        float[] leftLegRotation = new float[]{biped.bipedLeftLeg.rotateAngleX, biped.bipedLeftLeg.rotateAngleY, biped.bipedLeftLeg.rotateAngleZ};
        rotations[4] = leftLegRotation;
        return rotations;
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(Skeleton.getInterpolatedAmount(entity, partialTicks));
    }

    public Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return Skeleton.getInterpolatedPos(entity, partialTicks).subtract(this.renderManager.getRenderPosX(), this.renderManager.getRenderPosY(), this.renderManager.getRenderPosZ());
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return Skeleton.getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
    }
}

