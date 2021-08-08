package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraft.client.entity.*;

@ModuleManifest(label = "LiquidTweaks", listen = false, category = Category.MOVEMENT)
public class LiquidTweaks extends Module
{
    private final Setting<Boolean> vertical;
    private final Setting<Boolean> weird;
    
    public LiquidTweaks() {
        this.vertical = (Setting<Boolean>)this.register(new Setting("Vertical", (T)true));
        this.weird = (Setting<Boolean>)this.register(new Setting("Weird", (T)false));
    }
    
    @Override
    public void onUpdate() {
        if (this.mc.player.isInLava() && !this.weird.getValue() && this.vertical.getValue() && !this.mc.player.collidedVertically && this.mc.gameSettings.keyBindSneak.isKeyDown()) {
            final EntityPlayerSP player = this.mc.player;
            player.motionY -= 0.06553;
        }
    }
}
