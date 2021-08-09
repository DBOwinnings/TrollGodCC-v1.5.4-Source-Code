/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ModuleManifest(label="MiddleClick", listen=false, category=Module.Category.CLIENT)
public class MiddleClick
extends Module {
    private static MiddleClick INSTANCE;
    private final Setting<Boolean> friends = this.register(new Setting<Boolean>("Friends", true));

    public MiddleClick() {
        INSTANCE = this;
    }

    public static MiddleClick getInstance() {
        return INSTANCE;
    }

    public void run(int mouse) {
        if (mouse == 2 && this.friends.getValue().booleanValue() && this.mc.objectMouseOver.entityHit != null) {
            Entity entity = this.mc.objectMouseOver.entityHit;
            if (!(entity instanceof EntityPlayer)) {
                return;
            }
            if (TrollGod.INSTANCE.getFriendManager().isFriend(entity.getName())) {
                TrollGod.INSTANCE.getFriendManager().removeFriend(entity.getName());
            } else {
                TrollGod.INSTANCE.getFriendManager().addFriend(entity.getName());
            }
        }
    }
}

