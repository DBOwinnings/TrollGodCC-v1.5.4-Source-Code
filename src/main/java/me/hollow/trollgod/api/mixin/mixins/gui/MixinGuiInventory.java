/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.gui;

import net.minecraft.client.gui.inventory.GuiInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={GuiInventory.class})
public class MixinGuiInventory {
    @Redirect(method={"drawScreen"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/inventory/GuiInventory;drawDefaultBackground()V"))
    public void drawDefaultBackground(GuiInventory guiInventory) {
    }
}

