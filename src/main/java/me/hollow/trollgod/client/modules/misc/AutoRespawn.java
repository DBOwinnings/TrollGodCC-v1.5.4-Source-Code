/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.modules.misc;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import me.hollow.trollgod.api.property.Setting;
import me.hollow.trollgod.api.util.MessageUtil;
import me.hollow.trollgod.client.modules.Module;
import me.hollow.trollgod.client.modules.ModuleManifest;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleManifest(label="AutoRespawn", listen=false, category=Module.Category.MISC, color=-835840)
public class AutoRespawn
extends Module {
    private final Setting<Boolean> copyToClipboard = this.register(new Setting<Boolean>("Clipboard", true));

    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            this.mc.player.respawnPlayer();
            String deathCoords = "XYZ : " + (int)this.mc.player.posX + " " + (int)this.mc.player.posY + " " + (int)this.mc.player.posZ;
            MessageUtil.sendClientMessage(deathCoords, 0);
            if (this.copyToClipboard.getValue().booleanValue()) {
                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
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

