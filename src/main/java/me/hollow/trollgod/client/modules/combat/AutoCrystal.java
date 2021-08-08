package me.hollow.trollgod.client.modules.combat;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import me.hollow.trollgod.api.util.*;
import net.minecraft.network.play.client.*;
import me.hollow.trollgod.client.modules.client.*;
import java.awt.*;
import me.hollow.trollgod.api.util.render.*;
import net.minecraft.client.renderer.*;
import me.hollow.trollgod.client.events.*;
import me.hollow.trollgod.api.mixin.mixins.network.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import net.minecraft.init.*;
import java.util.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "AutoCrystal", category = Category.COMBAT, color = 8912896)
public final class AutoCrystal extends Module
{
    private final Setting<Page> page;
    private final Setting<Float> breakRange;
    private final Setting<Float> breakWallRange;
    private final Setting<Boolean> instant;
    private final Setting<Integer> breakDelay;
    private final Setting<Boolean> antiWeakness;
    private final Setting<Boolean> mainhand;
    private final Setting<Boolean> place;
    private final Setting<Float> placeRange;
    private final Setting<Integer> facePlaceHp;
    private final Setting<Double> minDamage;
    private final Setting<Integer> maxSelfDamage;
    private final Setting<Integer> armorScale;
    private final Setting<Float> range;
    private final Setting<Boolean> secondCheck;
    private final Setting<Boolean> autoSwitch;
    private final Setting<Boolean> updateController;
    private final Setting<Boolean> sync;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Boolean> renderDamage;
    private final Set<BlockPos> placeSet;
    private EntityPlayer currentTarget;
    private boolean lowArmor;
    private BlockPos renderPos;
    private boolean offhand;
    private double currentDamage;
    private int ticks;
    private int breakTicks;
    public static AutoCrystal INSTANCE;
    
    public AutoCrystal() {
        this.page = (Setting<Page>)this.register(new Setting("Page", (T)Page.BREAK));
        this.breakRange = (Setting<Float>)this.register(new Setting("Break Range", (T)5.0f, (T)1.0f, (T)6.0f, v -> this.page.getValue() == Page.BREAK));
        this.breakWallRange = (Setting<Float>)this.register(new Setting("Wall Range", (T)5.0f, (T)1.0f, (T)6.0f, v -> this.page.getValue() == Page.BREAK));
        this.instant = (Setting<Boolean>)this.register(new Setting("Instant", (T)true, v -> this.page.getValue() == Page.BREAK));
        this.breakDelay = (Setting<Integer>)this.register(new Setting("Break Delay", (T)1, (T)0, (T)10, v -> this.page.getValue() == Page.BREAK));
        this.antiWeakness = (Setting<Boolean>)this.register(new Setting("Anti Weakness", (T)true, v -> this.page.getValue() == Page.BREAK));
        this.mainhand = (Setting<Boolean>)this.register(new Setting("Mainhand", (T)true, v -> this.page.getValue() == Page.BREAK));
        this.place = (Setting<Boolean>)this.register(new Setting("Place", (T)true, v -> this.page.getValue() == Page.PLACE));
        this.placeRange = (Setting<Float>)this.register(new Setting("Place Range", (T)5.0f, (T)1.0f, (T)6.0f, v -> this.page.getValue() == Page.PLACE && this.place.getValue()));
        this.facePlaceHp = (Setting<Integer>)this.register(new Setting("Faceplace HP", (T)8, (T)0, (T)36, v -> this.page.getValue() == Page.PLACE && this.place.getValue()));
        this.minDamage = (Setting<Double>)this.register(new Setting("MinDamage", (T)4.0, (T)1.0, (T)36.0, v -> this.page.getValue() == Page.PLACE && this.place.getValue()));
        this.maxSelfDamage = (Setting<Integer>)this.register(new Setting("Max SelfDamage", (T)8, (T)1, (T)36, v -> this.page.getValue() == Page.PLACE && this.place.getValue()));
        this.armorScale = (Setting<Integer>)this.register(new Setting("Armor Scale", (T)10, (T)0, (T)100, v -> this.page.getValue() == Page.PLACE));
        this.range = (Setting<Float>)this.register(new Setting("Range", (T)9.0f, (T)1.0f, (T)15.0f, v -> this.page.getValue() == Page.PLACE));
        this.secondCheck = (Setting<Boolean>)this.register(new Setting("Second Check", (T)true, v -> this.page.getValue() == Page.PLACE));
        this.autoSwitch = (Setting<Boolean>)this.register(new Setting("Auto Switch", (T)true, v -> this.page.getValue() == Page.PLACE));
        this.updateController = (Setting<Boolean>)this.register(new Setting("Update Controller", (T)true));
        this.sync = (Setting<Boolean>)this.register(new Setting("Sync", (T)true, v -> this.page.getValue() == Page.RENDER));
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255, v -> !this.sync.getValue() && this.page.getValue() == Page.RENDER));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255, v -> !this.sync.getValue() && this.page.getValue() == Page.RENDER));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255, v -> !this.sync.getValue() && this.page.getValue() == Page.RENDER));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", (T)40, (T)0, (T)255, v -> this.page.getValue() == Page.RENDER));
        this.renderDamage = (Setting<Boolean>)this.register(new Setting("Render Damage", (T)true, v -> this.page.getValue() == Page.RENDER));
        this.placeSet = new HashSet<BlockPos>();
        AutoCrystal.INSTANCE = this;
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
        this.offhand = (this.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.currentTarget = CombatUtil.getTarget(this.range.getValue());
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
        for (final Entity crystal : this.mc.world.loadedEntityList) {
            if (crystal instanceof EntityEnderCrystal && (this.mc.player.canEntityBeSeen(crystal) ? this.breakRange.getValue() : this.breakWallRange.getValue()) > this.mc.player.getDistance(crystal)) {
                final double targetDamage = EntityUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, (EntityLivingBase)this.currentTarget);
                if (targetDamage < this.minDamage.getValue() && EntityUtil.getHealth((EntityLivingBase)this.currentTarget) > this.facePlaceHp.getValue() && !this.lowArmor) {
                    continue;
                }
                final double selfDamage = EntityUtil.calculate(crystal.posX, crystal.posY, crystal.posZ, (EntityLivingBase)this.mc.player);
                if (selfDamage + 2.0 >= EntityUtil.getHealth((EntityLivingBase)this.mc.player) || selfDamage >= targetDamage) {
                    continue;
                }
                if (maxDamage > targetDamage) {
                    continue;
                }
                maxCrystal = crystal;
                maxDamage = targetDamage;
            }
        }
        if (this.breakTicks++ < this.breakDelay.getValue()) {
            return;
        }
        if (maxCrystal != null) {
            this.breakTicks = 0;
            this.mc.getConnection().sendPacket((Packet)new CPacketUseEntity(maxCrystal));
            if (this.updateController.getValue()) {
                this.mc.playerController.updateController();
            }
            this.mc.player.swingArm(((boolean)this.mainhand.getValue()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
        }
    }
    
    private void doPlace() {
        BlockPos placePos = null;
        double maxDamage = 0.5;
        for (final BlockPos pos : BlockUtil.getSphere(this.placeRange.getValue(), true)) {
            if (!BlockUtil.canPlaceCrystal(pos, this.secondCheck.getValue())) {
                continue;
            }
            final double targetDamage = EntityUtil.calculate(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, (EntityLivingBase)this.currentTarget);
            if (targetDamage < this.minDamage.getValue() && EntityUtil.getHealth((EntityLivingBase)this.currentTarget) > this.facePlaceHp.getValue() && !this.lowArmor) {
                continue;
            }
            final double selfDamage = EntityUtil.calculate(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, (EntityLivingBase)this.mc.player);
            if (selfDamage + 2.0 >= EntityUtil.getHealth((EntityLivingBase)this.mc.player) || selfDamage >= targetDamage) {
                continue;
            }
            if (maxDamage > targetDamage) {
                continue;
            }
            placePos = pos;
            maxDamage = targetDamage;
        }
        if (!this.offhand && this.mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && !this.autoSwitch.getValue()) {
            return;
        }
        if (maxDamage != 0.5) {
            if (!this.offhand && this.autoSwitch.getValue() && (this.mc.player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE || !this.mc.player.isHandActive())) {
                final int crystalSlot = ItemUtil.getItemFromHotbar(Items.END_CRYSTAL);
                if (crystalSlot == -1) {
                    return;
                }
                this.mc.player.inventory.currentItem = crystalSlot;
                this.mc.playerController.updateController();
            }
            if (this.updateController.getValue()) {
                this.mc.playerController.updateController();
            }
            this.mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 1.0f, 0.5f));
            this.placeSet.add(placePos);
            this.renderPos = placePos;
            this.currentDamage = maxDamage;
        }
        else {
            this.renderPos = null;
        }
    }
    
    @Override
    public void onRender3D() {
        if (this.renderPos != null) {
            RenderUtil.drawBoxESP(this.renderPos, ((boolean)this.sync.getValue()) ? new Color(Colours.INSTANCE.getColor()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()), 1.0f, true, true, this.alpha.getValue(), 1.0f);
            if (this.renderDamage.getValue()) {
                GlStateManager.pushMatrix();
                RenderUtil.glBillboardDistanceScaled(this.renderPos.getX() + 0.5f, this.renderPos.getY() + 0.5f, this.renderPos.getZ() + 0.5f, (EntityPlayer)this.mc.player, 1.0f);
                if (this.currentDamage != -1.0) {
                    final String damageText = String.format("%.1f", this.currentDamage);
                    GlStateManager.translate((float)(-(this.mc.fontRenderer.getStringWidth(damageText) >> 1)), 0.0f, 0.0f);
                    this.mc.fontRenderer.drawStringWithShadow(damageText, 0.0f, 0.0f, -5592406);
                }
                GlStateManager.popMatrix();
            }
        }
    }
    
    @Subscribe
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && this.instant.getValue()) {
            final SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
            if (packet.getType() == 51 && this.placeSet.contains(new BlockPos(packet.getX(), packet.getY(), packet.getZ()).down())) {
                final AccessorCPacketUseEntity hitPacket = (AccessorCPacketUseEntity)new CPacketUseEntity();
                final int entityId = packet.getEntityID();
                hitPacket.setEntityId(entityId);
                hitPacket.setAction(CPacketUseEntity.Action.ATTACK);
                this.mc.getConnection().sendPacket((Packet)hitPacket);
                this.mc.player.swingArm(((boolean)this.mainhand.getValue()) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet2 = (SPacketSoundEffect)event.getPacket();
            if (packet2.getCategory() == SoundCategory.BLOCKS && packet2.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (final Entity entity : new ArrayList<Entity>(this.mc.world.loadedEntityList)) {
                    if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet2.getX(), packet2.getY(), packet2.getZ()) < 36.0) {
                        entity.setDead();
                        this.mc.world.removeEntity(entity);
                    }
                }
            }
        }
    }
    
    public enum Page
    {
        PLACE, 
        BREAK, 
        RENDER;
    }
}
