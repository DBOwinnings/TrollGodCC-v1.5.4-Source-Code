package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.client.events.*;
import me.hollow.trollgod.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "BehindSpoof", category = Category.PLAYER)
public class BehindSpoof extends Module
{
    private final Setting<Boolean> onlyOnBackwards;
    
    public BehindSpoof() {
        this.onlyOnBackwards = (Setting<Boolean>)this.register(new Setting("Require Movement", (T)true));
    }
    
    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        if (event.getStage() == 1) {
            return;
        }
        if (this.onlyOnBackwards.getValue() && !this.mc.gameSettings.keyBindBack.isKeyDown()) {
            return;
        }
        TrollGod.INSTANCE.getRotationManager().setPlayerRotations((this.mc.player.rotationYaw + 180.0f) % 360.0f, this.mc.player.rotationPitch);
    }
}
