package me.hollow.trollgod.client.modules.misc;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import net.minecraftforge.client.event.*;
import net.minecraft.client.gui.*;
import me.hollow.trollgod.api.util.*;
import java.awt.*;
import java.awt.datatransfer.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.common.*;

@ModuleManifest(label = "AutoRespawn", listen = false, category = Category.MISC, color = -835840)
public class AutoRespawn extends Module
{
    private final Setting<Boolean> copyToClipboard;
    
    public AutoRespawn() {
        this.copyToClipboard = (Setting<Boolean>)this.register(new Setting("Clipboard", (T)true));
    }
    
    @SubscribeEvent
    public void onGui(final GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            this.mc.player.respawnPlayer();
            final String deathCoords = "XYZ : " + (int)this.mc.player.posX + " " + (int)this.mc.player.posY + " " + (int)this.mc.player.posZ;
            MessageUtil.sendClientMessage(deathCoords, 0);
            if (this.copyToClipboard.getValue()) {
                try {
                    final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(deathCoords), null);
                }
                catch (Exception e) {
                    MessageUtil.sendClientMessage(e.getMessage(), 0);
                }
            }
        }
    }
    
    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }
    
    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
    }
}
