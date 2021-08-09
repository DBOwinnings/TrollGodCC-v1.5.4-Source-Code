/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.visual;

import com.google.common.base.Strings;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.mixin.mixins.render.AccessorRenderManager;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.ColorUtil;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.events.ConnectionEvent;
import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.events.UpdateEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.util.math.AxisAlignedBB;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="Logouts", category=Module.Category.VISUAL, color=0x99FF33)
public class LogOutSpots
extends Module {
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(300.0f), Float.valueOf(50.0f), Float.valueOf(500.0f)));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 0, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 0, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));
    private final Setting<Boolean> scaleing = this.register(new Setting<Boolean>("Scale", false));
    private final Setting<Float> scaling = this.register(new Setting<Float>("Size", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(20.0f)));
    private final Setting<Float> factor = this.register(new Setting<Object>("Factor", Float.valueOf(0.3f), Float.valueOf(0.1f), Float.valueOf(1.0f), v -> this.scaleing.getValue()));
    private final Setting<Boolean> smartScale = this.register(new Setting<Object>("SmartScale", false, v -> this.scaleing.getValue()));
    private final Setting<Boolean> rect = this.register(new Setting<Boolean>("Rectangle", true));
    private final List<LogoutPos> spots = new ArrayList<LogoutPos>();
    final AccessorRenderManager renderManager = (AccessorRenderManager)this.mc.getRenderManager();

    @Override
    public void onDisconnect() {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        this.spots.clear();
    }

    @Override
    public void onDisable() {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        this.spots.clear();
    }

    public AxisAlignedBB interpolateAxis(AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - this.mc.getRenderManager().viewerPosX, bb.minY - this.mc.getRenderManager().viewerPosY, bb.minZ - this.mc.getRenderManager().viewerPosZ, bb.maxX - this.mc.getRenderManager().viewerPosX, bb.maxY - this.mc.getRenderManager().viewerPosY, bb.maxZ - this.mc.getRenderManager().viewerPosZ);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onRender3D() {
        ArrayList<LogoutPos> syncSpots;
        if (this.spots.isEmpty()) {
            return;
        }
        List<LogoutPos> list = this.spots;
        synchronized (list) {
            syncSpots = new ArrayList<LogoutPos>(this.spots);
        }
        for (LogoutPos spot : syncSpots) {
            if (spot.getEntity() == null) continue;
            AxisAlignedBB bb = this.interpolateAxis(spot.getEntity().getEntityBoundingBox());
            RenderUtil.drawBoundingBox(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
            double x = this.interpolate(spot.getEntity().lastTickPosX, spot.getEntity().posX, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosX();
            double y = this.interpolate(spot.getEntity().lastTickPosY, spot.getEntity().posY, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosY();
            double z = this.interpolate(spot.getEntity().lastTickPosZ, spot.getEntity().posZ, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosZ();
            this.renderNameTag(spot.getName(), x, y, z, this.mc.getRenderPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent tickEvent) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        float range = this.range.getValue().floatValue() * this.range.getValue().floatValue();
        this.spots.removeIf(spot -> this.mc.player.getDistanceSq((Entity)spot.getEntity()) >= (double)range);
    }

    @Subscribe
    public void onConnection(ConnectionEvent event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (event.getStage() == 0) {
            this.spots.removeIf(pos -> pos.getName().equalsIgnoreCase(event.getName()));
        } else if (event.getStage() == 1) {
            EntityPlayer entity = event.getEntity();
            UUID uuid = event.getUuid();
            String name = event.getName();
            if (name != null && entity != null && uuid != null) {
                this.spots.add(new LogoutPos(name, uuid, entity));
            }
        }
    }

    private void renderNameTag(String name, double x, double yi, double z, float delta, double xPos, double yPos, double zPos) {
        double y = yi + 0.7;
        Entity camera = this.mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        String displayTag = name + " XYZ: " + (int)xPos + ", " + (int)yPos + ", " + (int)zPos;
        double distance = camera.getDistance(x + this.mc.getRenderManager().viewerPosX, y + this.mc.getRenderManager().viewerPosY, z + this.mc.getRenderManager().viewerPosZ);
        int width = this.mc.fontRenderer.getStringWidth(displayTag) >> 1;
        double scale = (0.0018 + (double)this.scaling.getValue().floatValue() * (distance * (double)this.factor.getValue().floatValue())) / 1000.0;
        if (distance <= 8.0 && this.smartScale.getValue().booleanValue()) {
            scale = 0.0245;
        }
        if (!this.scaleing.getValue().booleanValue()) {
            scale = (double)this.scaling.getValue().floatValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)-1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)((float)x), (float)((float)y + 1.4f), (float)((float)z));
        GlStateManager.rotate((float)(-this.mc.getRenderManager().playerViewY), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)this.mc.getRenderManager().playerViewX, (float)(this.mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f), (float)0.0f, (float)0.0f);
        GlStateManager.scale((double)(-scale), (double)(-scale), (double)scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (this.rect.getValue().booleanValue()) {
            RenderUtil.drawRect(-width - 2, -(this.mc.fontRenderer.FONT_HEIGHT + 1), (float)width + 2.0f, 1.5f, 0x55000000);
        }
        GlStateManager.disableBlend();
        this.mc.fontRenderer.drawStringWithShadow(displayTag, (float)(-width), (float)(-(this.mc.fontRenderer.FONT_HEIGHT - 1)), ColorUtil.toRGBA(new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue())));
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset((float)1.0f, (float)1500000.0f);
        GlStateManager.popMatrix();
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double)delta;
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals((Object)packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals((Object)packet.getAction())) {
                return;
            }
            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> !Strings.isNullOrEmpty((String)data.getProfile().getName()) || data.getProfile().getId() != null).forEach(data -> {
                UUID id = data.getProfile().getId();
                switch (packet.getAction()) {
                    case ADD_PLAYER: {
                        String name = data.getProfile().getName();
                        TrollGod.INSTANCE.getBus().post(new ConnectionEvent(0, id, name));
                        break;
                    }
                    case REMOVE_PLAYER: {
                        EntityPlayer entity = this.mc.world.getPlayerEntityByUUID(id);
                        if (entity != null) {
                            String logoutName = entity.getName();
                            TrollGod.INSTANCE.getBus().post(new ConnectionEvent(1, entity, id, logoutName));
                            break;
                        }
                        TrollGod.INSTANCE.getBus().post(new ConnectionEvent(2, id, null));
                    }
                }
            });
        }
    }

    private static class LogoutPos {
        private final String name;
        private final UUID uuid;
        private final EntityPlayer entity;
        private final double x;
        private final double y;
        private final double z;

        public LogoutPos(String name, UUID uuid, EntityPlayer entity) {
            this.name = name;
            this.uuid = uuid;
            this.entity = entity;
            this.x = entity.posX;
            this.y = entity.posY;
            this.z = entity.posZ;
        }

        public String getName() {
            return this.name;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public EntityPlayer getEntity() {
            return this.entity;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }
}

