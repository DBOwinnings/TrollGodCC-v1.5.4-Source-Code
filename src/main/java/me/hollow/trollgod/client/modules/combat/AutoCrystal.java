/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import me.hollow.trollgod.api.mixin.mixins.network.AccessorCPacketUseEntity;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.BlockUtil;
import me.hollow.trollgod.api.util.CombatUtil;
import me.hollow.trollgod.api.util.EntityUtil;
import me.hollow.trollgod.api.util.ItemUtil;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.events.PacketEvent;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import me.hollow.trollgod.client.modules.client.Colours;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import tcb.bces.listener.Subscribe;

@ModuleManifest(label="AutoCrystal", category=Module.Category.COMBAT, color=0x880000)
public final class AutoCrystal
extends Module {
    private final Setting<Page> page = this.register(new Setting<Page>("Page", Page.BREAK));
    private final Setting<Float> breakRange = this.register(new Setting<Float>("Break Range", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.BREAK));
    private final Setting<Float> breakWallRange = this.register(new Setting<Float>("Wall Range", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.BREAK));
    private final Setting<Boolean> instant = this.register(new Setting<Boolean>("Instant", true, v -> this.page.getValue() == Page.BREAK));
    private final Setting<Integer> breakDelay = this.register(new Setting<Integer>("Break Delay", 1, 0, 10, v -> this.page.getValue() == Page.BREAK));
    private final Setting<Boolean> antiWeakness = this.register(new Setting<Boolean>("Anti Weakness", true, v -> this.page.getValue() == Page.BREAK));
    private final Setting<Boolean> mainhand = this.register(new Setting<Boolean>("Mainhand", true, v -> this.page.getValue() == Page.BREAK));
    private final Setting<Boolean> place = this.register(new Setting<Boolean>("Place", true, v -> this.page.getValue() == Page.PLACE));
    private final Setting<Float> placeRange = this.register(new Setting<Float>("Place Range", Float.valueOf(5.0f), Float.valueOf(1.0f), Float.valueOf(6.0f), v -> this.page.getValue() == Page.PLACE && this.place.getValue() != false));
    private final Setting<Integer> facePlaceHp = this.register(new Setting<Integer>("Faceplace HP", 8, 0, 36, v -> this.page.getValue() == Page.PLACE && this.place.getValue() != false));
    private final Setting<Double> minDamage = this.register(new Setting<Double>("MinDamage", 4.0, 1.0, 36.0, v -> this.page.getValue() == Page.PLACE && this.place.getValue() != false));
    private final Setting<Integer> maxSelfDamage = this.register(new Setting<Integer>("Max SelfDamage", 8, 1, 36, v -> this.page.getValue() == Page.PLACE && this.place.getValue() != false));
    private final Setting<Integer> armorScale = this.register(new Setting<Integer>("Armor Scale", 10, 0, 100, v -> this.page.getValue() == Page.PLACE));
    private final Setting<Float> range = this.register(new Setting<Float>("Range", Float.valueOf(9.0f), Float.valueOf(1.0f), Float.valueOf(15.0f), v -> this.page.getValue() == Page.PLACE));
    private final Setting<Boolean> secondCheck = this.register(new Setting<Boolean>("Second Check", true, v -> this.page.getValue() == Page.PLACE));
    private final Setting<Boolean> autoSwitch = this.register(new Setting<Boolean>("Auto Switch", true, v -> this.page.getValue() == Page.PLACE));
    private final Setting<Boolean> updateController = this.register(new Setting<Boolean>("Update Controller", true));
    private final Setting<Boolean> sync = this.register(new Setting<Boolean>("Sync", true, v -> this.page.getValue() == Page.RENDER));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255, v -> this.sync.getValue() == false && this.page.getValue() == Page.RENDER));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255, v -> this.sync.getValue() == false && this.page.getValue() == Page.RENDER));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255, v -> this.sync.getValue() == false && this.page.getValue() == Page.RENDER));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 40, 0, 255, v -> this.page.getValue() == Page.RENDER));
    private final Setting<Boolean> renderDamage = this.register(new Setting<Boolean>("Render Damage", true, v -> this.page.getValue() == Page.RENDER));
    private final Set<BlockPos> placeSet = new HashSet<BlockPos>();
    private EntityPlayer currentTarget;
    private boolean lowArmor;
    private BlockPos renderPos;
    private boolean offhand;
    private double currentDamage;
    private int ticks;
    private int breakTicks;
    public static AutoCrystal INSTANCE;

    public AutoCrystal() {
        INSTANCE = this;
    }

    @Override
    public void onToggle() {
        this.placeSet.clear();
        this.renderPos = null;
    }

    public void onTick() {
        if (this.isNull()) {
            return;
        }
        if (this.ticks++ > 20) {
            this.ticks = 0;
            this.placeSet.clear();
            this.renderPos = null;
            this.clearSuffix();
        }
        this.offhand = this.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.currentTarget = CombatUtil.getTarget(this.range.getValue().floatValue());
        if (this.currentTarget == null) {
            return;
        }
        this.lowArmor = ItemUtil.isArmorLow(this.currentTarget, this.armorScale.getValue());
        this.doPlace();
        this.doBreak();
    }

    private void doBreak() {
        Entity maxCrystal = null;
        double maxDamage = 0.5;
        for (Entity crystal : this.mc.world.loadedEntityList) {
            double selfDamage;
            double targetDamage;
            if (!(crystal instanceof EntityEnderCrystal)) continue;
            Float f = this.mc.player.canEntityBeSeen(crystal) ? this.breakRange.getValue() : this.breakWallRange.getValue();
            if (!(f.floatValue() > this.mc.player.getDistance(crystal)) || (targetDamage = (double)EntityUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, (EntityLivingBase)this.currentTarget)) < this.minDamage.getValue() && EntityUtil.getHealth((EntityLivingBase)this.currentTarget) > (float)this.facePlaceHp.getValue().intValue() && !this.lowArmor || (selfDamage = (double)EntityUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, (EntityLivingBase)this.mc.player)) + 2.0 >= (double)EntityUtil.getHealth((EntityLivingBase)this.mc.player) || selfDamage >= targetDamage || maxDamage > targetDamage) continue;
            maxCrystal = crystal;
            maxDamage = targetDamage;
        }
        if (this.breakTicks++ < this.breakDelay.getValue()) {
            return;
        }
        if (maxCrystal != null) {
            this.breakTicks = 0;
            this.mc.getConnection().sendPacket((Packet)new CPacketUseEntity(maxCrystal));
            if (this.updateController.getValue().booleanValue()) {
                this.mc.playerController.updateController();
            }
            this.mc.player.swingArm(this.mainhand.getValue() == false ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        }
    }

    private void doPlace() {
        BlockPos placePos = null;
        double maxDamage = 0.5;
        for (BlockPos pos : BlockUtil.getSphere(this.placeRange.getValue().floatValue(), true)) {
            double selfDamage;
            double targetDamage;
            if (!BlockUtil.canPlaceCrystal(pos, this.secondCheck.getValue()) || (targetDamage = (double)EntityUtil.calculate((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, (EntityLivingBase)this.currentTarget)) < this.minDamage.getValue() && EntityUtil.getHealth((EntityLivingBase)this.currentTarget) > (float)this.facePlaceHp.getValue().intValue() && !this.lowArmor || (selfDamage = (double)EntityUtil.calculate((double)pos.getX() + 0.5, (double)pos.getY() + 1.0, (double)pos.getZ() + 0.5, (EntityLivingBase)this.mc.player)) + 2.0 >= (double)EntityUtil.getHealth((EntityLivingBase)this.mc.player) || selfDamage >= targetDamage || maxDamage > targetDamage) continue;
            placePos = pos;
            maxDamage = targetDamage;
        }
        if (!this.offhand && this.mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && !this.autoSwitch.getValue().booleanValue()) {
            return;
        }
        if (maxDamage != 0.5) {
            if (!(this.offhand || !this.autoSwitch.getValue().booleanValue() || this.mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE && this.mc.player.isHandActive())) {
                int crystalSlot = ItemUtil.getItemFromHotbar(Items.END_CRYSTAL);
                if (crystalSlot == -1) {
                    return;
                }
                this.mc.player.inventory.currentItem = crystalSlot;
                this.mc.playerController.updateController();
            }
            if (this.updateController.getValue().booleanValue()) {
                this.mc.playerController.updateController();
            }
            this.mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 1.0f, 0.5f));
            this.placeSet.add(placePos);
            this.renderPos = placePos;
            this.currentDamage = maxDamage;
        } else {
            this.renderPos = null;
        }
    }

    @Override
    public void onRender3D() {
        if (this.renderPos != null) {
            RenderUtil.drawBoxESP(this.renderPos, this.sync.getValue() != false ? new Color(Colours.INSTANCE.getColor()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()), 1.0f, true, true, this.alpha.getValue(), 1.0f);
            if (this.renderDamage.getValue().booleanValue()) {
                GlStateManager.pushMatrix();
                RenderUtil.glBillboardDistanceScaled((float)this.renderPos.getX() + 0.5f, (float)this.renderPos.getY() + 0.5f, (float)this.renderPos.getZ() + 0.5f, (EntityPlayer)this.mc.player, 1.0f);
                if (this.currentDamage != -1.0) {
                    String damageText = String.format("%.1f", this.currentDamage);
                    GlStateManager.translate((float)(-(this.mc.fontRenderer.getStringWidth(damageText) >> 1)), (float)0.0f, (float)0.0f);
                    this.mc.fontRenderer.drawStringWithShadow(damageText, 0.0f, 0.0f, -5592406);
                }
                GlStateManager.popMatrix();
            }
        }
    }

    @Subscribe
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSoundEffect packet;
        if (event.getPacket() instanceof SPacketSpawnObject && instant.getValue()) {
            final SPacketSpawnObject packet2 = (SPacketSpawnObject) event.getPacket ( );
            if ( packet2.getType ( ) == 51 && placeSet.contains ( new BlockPos ( packet2.getX ( ) , packet2.getY ( ) , packet2.getZ ( ) ).down ( ) ) ) {
                AccessorCPacketUseEntity hitPacket = (AccessorCPacketUseEntity) new CPacketUseEntity ( );
                int entityId = packet2.getEntityID ( );
                hitPacket.setEntityId ( entityId );
                hitPacket.setAction ( CPacketUseEntity.Action.ATTACK );
                this.mc.getConnection ( ).sendPacket ( (Packet) hitPacket );
                this.mc.player.swingArm ( this.mainhand.getValue ( ) == false ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND );
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect && (packet = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            for (final Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36.0)) continue;
                entity.setDead();
                this.mc.world.removeEntity(entity);
            }
        }
    }

    public static enum Page {
        PLACE,
        BREAK,
        RENDER;

    }
}

