package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.mixin.mixins.render.*;
import org.lwjgl.opengl.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import java.awt.*;
import net.minecraft.util.math.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.api.util.render.*;

@ModuleManifest(label = "Tracers", category = Category.VISUAL, listen = false, color = 16445103)
public class Tracers extends Module
{
    private final Setting<Integer> yRange;
    private final AccessorRenderManager renderManager;
    
    public Tracers() {
        this.yRange = (Setting<Integer>)this.register(new Setting("Y Range", (T)50, (T)0, (T)255));
        this.renderManager = (AccessorRenderManager)this.mc.getRenderManager();
    }
    
    @Override
    public void onRender3D() {
        final boolean iHateBob = this.mc.gameSettings.viewBobbing;
        this.mc.gameSettings.viewBobbing = false;
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glBegin(1);
        for (int size = this.mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            final EntityPlayer entity = this.mc.world.playerEntities.get(i);
            if (entity != this.mc.player) {
                if (this.mc.getRenderViewEntity().getDistance(this.mc.getRenderViewEntity().posX, entity.posY, this.mc.getRenderViewEntity().posZ) <= this.yRange.getValue()) {
                    this.drawTraces((Entity)entity, this.getColorByDistance((Entity)entity));
                }
            }
        }
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.gameSettings.viewBobbing = iHateBob;
    }
    
    private void drawTraces(final Entity entity, final Color color) {
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * this.mc.getRenderPartialTicks() - this.renderManager.getRenderPosX();
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * this.mc.getRenderPartialTicks() - this.renderManager.getRenderPosY();
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * this.mc.getRenderPartialTicks() - this.renderManager.getRenderPosZ();
        final Vec3d eyes = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-(float)Math.toRadians(this.mc.getRenderViewEntity().rotationPitch)).rotateYaw(-(float)Math.toRadians(this.mc.getRenderViewEntity().rotationYaw));
        if (TrollGod.INSTANCE.getFriendManager().isFriend(entity.getName())) {
            RenderUtil.glColor(-11157267);
        }
        else {
            RenderUtil.glColor(color.getRGB());
        }
        GL11.glVertex3d(eyes.x, eyes.y + this.mc.getRenderViewEntity().getEyeHeight(), eyes.z);
        GL11.glVertex3d(x, y + entity.getEyeHeight(), z);
    }
    
    public Color getColorByDistance(final Entity entity) {
        if (entity instanceof EntityPlayer && TrollGod.INSTANCE.getFriendManager().isFriend(entity.getName())) {
            return new Color(-11157267);
        }
        return new Color(Color.HSBtoRGB((float)(Math.max(0.0, Math.min(this.mc.player.getDistanceSq(entity), 2500.0) / 2500.0) / 3.0), 1.0f, 0.8f) | 0xFF000000);
    }
}
