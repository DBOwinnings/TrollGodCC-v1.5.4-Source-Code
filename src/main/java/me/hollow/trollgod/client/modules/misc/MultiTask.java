package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.util.math.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.util.*;

@ModuleManifest(label = "MultiTask", listen = false, category = Category.MISC, color = -16558593)
public class MultiTask extends Module
{
    private final Setting<Boolean> entityHit;
    private static MultiTask INSTANCE;
    
    public MultiTask() {
        this.entityHit = (Setting<Boolean>)this.register(new Setting("Entities", (T)true));
        MultiTask.INSTANCE = this;
    }
    
    public static MultiTask getInstance() {
        return MultiTask.INSTANCE;
    }
    
    public void onMouse(final int button) {
        if (button == 0 && this.entityHit.getValue() && this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && !(this.mc.objectMouseOver.entityHit instanceof EntityItem) && !(this.mc.objectMouseOver.entityHit instanceof EntityExpBottle) && !(this.mc.objectMouseOver.entityHit instanceof EntityArrow) && this.mc.gameSettings.keyBindUseItem.isKeyDown() && this.mc.player.isHandActive() && this.mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.mc.getConnection().sendPacket((Packet)new CPacketUseEntity(this.mc.objectMouseOver.entityHit));
            this.mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
}
