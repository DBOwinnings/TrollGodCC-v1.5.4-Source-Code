package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.mixin.mixins.render.*;
import net.minecraft.util.math.*;
import java.awt.*;
import me.hollow.trollgod.api.util.render.*;
import net.minecraft.entity.*;
import tcb.bces.listener.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.renderer.*;
import me.hollow.trollgod.api.util.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.network.play.server.*;
import java.util.*;
import java.util.function.*;
import com.google.common.base.*;
import me.hollow.trollgod.*;

@ModuleManifest(label = "Logouts", category = Category.VISUAL, color = 10092339)
public class LogOutSpots extends Module
{
    private final Setting<Float> range;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Boolean> scaleing;
    private final Setting<Float> scaling;
    private final Setting<Float> factor;
    private final Setting<Boolean> smartScale;
    private final Setting<Boolean> rect;
    private final List<LogoutPos> spots;
    final AccessorRenderManager renderManager;
    
    public LogOutSpots() {
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)300.0f, (T)50.0f, (T)500.0f));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)0, (T)0, (T)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)0, (T)0, (T)255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)255, (T)0, (T)255));
        this.scaleing = (Setting<Boolean>)this.register(new Setting("Scale", (T)false));
        this.scaling = (Setting<Float>)this.register(new Setting("Size", (T)4.0f, (T)0.1f, (T)20.0f));
        this.factor = (Setting<Float>)this.register(new Setting("Factor", (T)0.3f, (T)0.1f, (T)1.0f, v -> this.scaleing.getValue()));
        this.smartScale = (Setting<Boolean>)this.register(new Setting("SmartScale", (T)false, v -> this.scaleing.getValue()));
        this.rect = (Setting<Boolean>)this.register(new Setting("Rectangle", (T)true));
        this.spots = new ArrayList<LogoutPos>();
        this.renderManager = (AccessorRenderManager)this.mc.getRenderManager();
    }
    
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
    
    public AxisAlignedBB interpolateAxis(final AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - this.mc.getRenderManager().viewerPosX, bb.minY - this.mc.getRenderManager().viewerPosY, bb.minZ - this.mc.getRenderManager().viewerPosZ, bb.maxX - this.mc.getRenderManager().viewerPosX, bb.maxY - this.mc.getRenderManager().viewerPosY, bb.maxZ - this.mc.getRenderManager().viewerPosZ);
    }
    
    @Override
    public void onRender3D() {
        if (this.spots.isEmpty()) {
            return;
        }
        final List<LogoutPos> syncSpots;
        synchronized (this.spots) {
            syncSpots = new ArrayList<LogoutPos>(this.spots);
        }
        for (final LogoutPos spot : syncSpots) {
            if (spot.getEntity() != null) {
                final AxisAlignedBB bb = this.interpolateAxis(spot.getEntity().getEntityBoundingBox());
                RenderUtil.drawBoundingBox(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                final double x = this.interpolate(spot.getEntity().lastTickPosX, spot.getEntity().posX, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosX();
                final double y = this.interpolate(spot.getEntity().lastTickPosY, spot.getEntity().posY, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosY();
                final double z = this.interpolate(spot.getEntity().lastTickPosZ, spot.getEntity().posZ, this.mc.getRenderPartialTicks()) - this.renderManager.getRenderPosZ();
                this.renderNameTag(spot.getName(), x, y, z, this.mc.getRenderPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
            }
        }
    }
    
    @Subscribe
    public void onUpdate(final UpdateEvent tickEvent) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        final float range = this.range.getValue() * this.range.getValue();
        this.spots.removeIf(spot -> this.mc.player.getDistanceSq((Entity)spot.getEntity()) >= range);
    }
    
    @Subscribe
    public void onConnection(final ConnectionEvent event) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (event.getStage() == 0) {
            this.spots.removeIf(pos -> pos.getName().equalsIgnoreCase(event.getName()));
        }
        else if (event.getStage() == 1) {
            final EntityPlayer entity = event.getEntity();
            final UUID uuid = event.getUuid();
            final String name = event.getName();
            if (name != null && entity != null && uuid != null) {
                this.spots.add(new LogoutPos(name, uuid, entity));
            }
        }
    }
    
    private void renderNameTag(final String name, final double x, final double yi, final double z, final float delta, final double xPos, final double yPos, final double zPos) {
        final double y = yi + 0.7;
        final Entity camera = this.mc.getRenderViewEntity();
        assert camera != null;
        final double originalPositionX = camera.posX;
        final double originalPositionY = camera.posY;
        final double originalPositionZ = camera.posZ;
        camera.posX = this.interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = this.interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = this.interpolate(camera.prevPosZ, camera.posZ, delta);
        final String displayTag = name + " XYZ: " + (int)xPos + ", " + (int)yPos + ", " + (int)zPos;
        final double distance = camera.getDistance(x + this.mc.getRenderManager().viewerPosX, y + this.mc.getRenderManager().viewerPosY, z + this.mc.getRenderManager().viewerPosZ);
        final int width = this.mc.fontRenderer.getStringWidth(displayTag) >> 1;
        double scale = (0.0018 + this.scaling.getValue() * (distance * this.factor.getValue())) / 1000.0;
        if (distance <= 8.0 && this.smartScale.getValue()) {
            scale = 0.0245;
        }
        if (!this.scaleing.getValue()) {
            scale = this.scaling.getValue() / 100.0;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x, (float)y + 1.4f, (float)z);
        GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(this.mc.getRenderManager().playerViewX, (this.mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if (this.rect.getValue()) {
            RenderUtil.drawRect((float)(-width - 2), (float)(-(this.mc.fontRenderer.FONT_HEIGHT + 1)), width + 2.0f, 1.5f, 1426063360);
        }
        GlStateManager.disableBlend();
        this.mc.fontRenderer.drawStringWithShadow(displayTag, (float)(-width), (float)(-(this.mc.fontRenderer.FONT_HEIGHT - 1)), ColorUtil.toRGBA(new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue())));
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }
    
    private double interpolate(final double previous, final double current, final float delta) {
        return previous + (current - previous) * delta;
    }
    
    @Subscribe
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            final SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals((Object)packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals((Object)packet.getAction())) {
                return;
            }
            final UUID id;
            final SPacketPlayerListItem sPacketPlayerListItem;
            final String name;
            final EntityPlayer entity;
            String logoutName;
            packet.getEntries().stream().filter(Objects::nonNull).filter(data -> !Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null).forEach(data -> {
                id = data.getProfile().getId();
                switch (sPacketPlayerListItem.getAction()) {
                    case ADD_PLAYER: {
                        name = data.getProfile().getName();
                        TrollGod.INSTANCE.getBus().post(new ConnectionEvent(0, id, name));
                        break;
                    }
                    case REMOVE_PLAYER: {
                        entity = this.mc.world.getPlayerEntityByUUID(id);
                        if (entity != null) {
                            logoutName = entity.getName();
                            TrollGod.INSTANCE.getBus().post(new ConnectionEvent(1, entity, id, logoutName));
                            break;
                        }
                        else {
                            TrollGod.INSTANCE.getBus().post(new ConnectionEvent(2, id, null));
                            break;
                        }
                        break;
                    }
                }
            });
        }
    }
    
    private static class LogoutPos
    {
        private final String name;
        private final UUID uuid;
        private final EntityPlayer entity;
        private final double x;
        private final double y;
        private final double z;
        
        public LogoutPos(final String name, final UUID uuid, final EntityPlayer entity) {
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
