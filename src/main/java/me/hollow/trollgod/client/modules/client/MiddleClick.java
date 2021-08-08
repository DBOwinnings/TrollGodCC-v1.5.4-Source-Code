package me.hollow.trollgod.client.modules.client;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.entity.player.*;
import me.hollow.trollgod.*;
import net.minecraft.entity.*;

@ModuleManifest(label = "MiddleClick", listen = false, category = Category.CLIENT)
public class MiddleClick extends Module
{
    private static MiddleClick INSTANCE;
    private final Setting<Boolean> friends;
    
    public MiddleClick() {
        this.friends = (Setting<Boolean>)this.register(new Setting("Friends", (T)true));
        MiddleClick.INSTANCE = this;
    }
    
    public static MiddleClick getInstance() {
        return MiddleClick.INSTANCE;
    }
    
    public void run(final int mouse) {
        if (mouse == 2 && this.friends.getValue() && this.mc.objectMouseOver.entityHit != null) {
            final Entity entity = this.mc.objectMouseOver.entityHit;
            if (!(entity instanceof EntityPlayer)) {
                return;
            }
            if (TrollGod.INSTANCE.getFriendManager().isFriend(entity.getName())) {
                TrollGod.INSTANCE.getFriendManager().removeFriend(entity.getName());
            }
            else {
                TrollGod.INSTANCE.getFriendManager().addFriend(entity.getName());
            }
        }
    }
}
