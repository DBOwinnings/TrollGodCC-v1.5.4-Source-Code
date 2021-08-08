package me.hollow.trollgod.api.mixin.mixins.gui;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.inventory.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ GuiInventory.class })
public class MixinGuiInventory
{
    @Redirect(method = { "drawScreen" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiInventory;drawDefaultBackground()V"))
    public void drawDefaultBackground(final GuiInventory guiInventory) {
    }
}
