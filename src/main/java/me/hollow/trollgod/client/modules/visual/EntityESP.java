package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.mixin.mixins.render.*;
import me.hollow.trollgod.client.modules.client.*;
import java.awt.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import me.hollow.trollgod.api.util.render.*;
import net.minecraft.util.math.*;

@ModuleManifest(label = "EntityESP", listen = false, category = Category.VISUAL, color = 16711935)
public final class EntityESP extends Module
{
    private final Setting<Boolean> bottles;
    private final Setting<Boolean> items;
    private final Setting<Boolean> players;
    private final Setting<Boolean> sync;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final AccessorRenderManager renderManager;
    public static EntityESP INSTANCE;
    
    public EntityESP() {
        this.bottles = (Setting<Boolean>)this.register(new Setting("XPBottles", (T)true));
        this.items = (Setting<Boolean>)this.register(new Setting("Items", (T)true));
        this.players = (Setting<Boolean>)this.register(new Setting("Players", (T)true));
        this.sync = (Setting<Boolean>)this.register(new Setting("Sync", (T)true));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255, v -> !this.sync.getValue()));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> !this.sync.getValue()));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255, v -> !this.sync.getValue()));
        this.renderManager = (AccessorRenderManager)this.mc.getRenderManager();
        EntityESP.INSTANCE = this;
    }
    
    @Override
    public final int getColor() {
        return this.sync.getValue() ? Colours.INSTANCE.getColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
    }
    
    @Override
    public final void onRender3D() {
        for (int size = this.mc.world.loadedEntityList.size(), i = 0; i < size; ++i) {
            final Entity entity = this.mc.world.loadedEntityList.get(i);
            if ((entity instanceof EntityExpBottle && this.bottles.getValue()) || (entity instanceof EntityItem && this.items.getValue()) || (entity instanceof EntityPlayer && this.players.getValue() && entity != this.mc.player)) {
                if (!(entity instanceof EntityItem) || this.mc.player.getDistanceSq(entity) <= 2500.0) {
                    final Vec3d vec = this.interpolateEntity(entity, this.mc.getRenderPartialTicks());
                    RenderUtil.drawBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, (double)entity.width, (double)entity.height, (double)entity.width).offset(vec.x - this.renderManager.getRenderPosX() - entity.width / 2.0f, vec.y - this.renderManager.getRenderPosY(), vec.z - this.renderManager.getRenderPosZ() - entity.width / 2.0f).grow((entity instanceof EntityPlayer) ? 0.0 : 0.125), ((boolean)this.sync.getValue()) ? new Color(Colours.INSTANCE.getColor()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()), 1.0f);
                }
            }
        }
    }
    
    private Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }
}
