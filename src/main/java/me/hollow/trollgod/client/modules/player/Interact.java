package me.hollow.trollgod.client.modules.player;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.client.events.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.api.mixin.accessors.*;
import tcb.bces.listener.*;

@ModuleManifest(label = "Interact", category = Category.PLAYER, color = 10858157)
public class Interact extends Module
{
    private final Setting<Boolean> xp;
    private final Setting<Boolean> footXp;
    private final Setting<Boolean> blocks;
    
    public Interact() {
        this.xp = (Setting<Boolean>)this.register(new Setting("XP", (T)true));
        this.footXp = (Setting<Boolean>)this.register(new Setting("Foot XP", (T)true, v -> this.xp.getValue()));
        this.blocks = (Setting<Boolean>)this.register(new Setting("Blocks", (T)true));
    }
    
    @Subscribe
    public void onUpdate(final UpdateEvent event) {
        if (event.getStage() == 0 && ((this.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE && this.xp.getValue()) || (this.mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && this.blocks.getValue()))) {
            if (this.mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE && this.footXp.getValue()) {
                TrollGod.INSTANCE.getRotationManager().setPitch(90.0f);
            }
            ((IMinecraft)this.mc).setRightClickDelayTimer(0);
        }
    }
}
