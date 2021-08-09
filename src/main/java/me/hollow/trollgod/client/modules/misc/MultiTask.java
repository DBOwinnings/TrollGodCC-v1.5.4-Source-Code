/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

@ModuleManifest(label="MultiTask", listen=false, category=Module.Category.MISC, color=-16558593)
public class MultiTask
extends Module {
    private final Setting<Boolean> entityHit = this.register(new Setting<Boolean>("Entities", true));
    private static MultiTask INSTANCE;

    public MultiTask() {
        INSTANCE = this;
    }

    public static MultiTask getInstance() {
        return INSTANCE;
    }

    public void onMouse(int button) {
        if (button == 0 && this.entityHit.getValue().booleanValue() && this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && !(this.mc.objectMouseOver.entityHit instanceof EntityItem) && !(this.mc.objectMouseOver.entityHit instanceof EntityExpBottle) && !(this.mc.objectMouseOver.entityHit instanceof EntityArrow) && this.mc.gameSettings.keyBindUseItem.isKeyDown() && this.mc.player.isHandActive() && this.mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.mc.getConnection().sendPacket((Packet)new CPacketUseEntity(this.mc.objectMouseOver.entityHit));
            this.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
}

