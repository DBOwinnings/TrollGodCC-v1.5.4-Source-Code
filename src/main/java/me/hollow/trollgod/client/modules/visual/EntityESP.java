/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import java.awt.Color;
import me.hollow.trollgod.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import me.hollow.trollgod.client.modules.client.Colours;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

@ModuleManifest(label="EntityESP", listen=false, category=Module.Category.VISUAL, color=0xFF00FF)
public final class EntityESP
extends Module {
    private final Setting<Boolean> bottles = this.register(new Setting<Boolean>("XPBottles", true));
    private final Setting<Boolean> items = this.register(new Setting<Boolean>("Items", true));
    private final Setting<Boolean> players = this.register(new Setting<Boolean>("Players", true));
    private final Setting<Boolean> sync = this.register(new Setting<Boolean>("Sync", true));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255, v -> this.sync.getValue() == false));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255, v -> this.sync.getValue() == false));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255, v -> this.sync.getValue() == false));
    private final AccessorRenderManager renderManager = (AccessorRenderManager)this.mc.getRenderManager();
    public static EntityESP INSTANCE;

    public EntityESP() {
        INSTANCE = this;
    }

    @Override
    public final int getColor() {
        return this.sync.getValue() != false ? Colours.INSTANCE.getColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
    }

    @Override
    public final void onRender3D() {
        int size = this.mc.world.loadedEntityList.size();
        for (int i = 0; i < size; ++i) {
            Entity entity = (Entity)this.mc.world.loadedEntityList.get(i);
            if ((!(entity instanceof EntityExpBottle) || !this.bottles.getValue().booleanValue()) && (!(entity instanceof EntityItem) || !this.items.getValue().booleanValue()) && (!(entity instanceof EntityPlayer) || !this.players.getValue().booleanValue() || entity == this.mc.player) || entity instanceof EntityItem && this.mc.player.getDistanceSq(entity) > 2500.0) continue;
            Vec3d vec = this.interpolateEntity(entity, this.mc.getRenderPartialTicks());
            RenderUtil.drawBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, (double)entity.width, (double)entity.height, (double)entity.width).offset(vec.x - this.renderManager.getRenderPosX() - (double)(entity.width / 2.0f), vec.y - this.renderManager.getRenderPosY(), vec.z - this.renderManager.getRenderPosZ() - (double)(entity.width / 2.0f)).grow(entity instanceof EntityPlayer ? 0.0 : 0.125), this.sync.getValue() != false ? new Color(Colours.INSTANCE.getColor()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()), 1.0f);
        }
    }

    private Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time);
    }
}

