/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin.mixins.gui;

import me.hollow.trollgod.client.modules.visual.ShulkerPreview;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={GuiScreen.class})
public class MixinGuiScreen {
    private final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
    private final FontRenderer fontRenderer;

    public MixinGuiScreen() {
        this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
    }

    @Inject(method={"renderToolTip"}, at={@At(value="HEAD")}, cancellable=true)
    public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo ci) {
        NBTTagCompound blockEntityTag;
        NBTTagCompound tagCompound;
        if (ShulkerPreview.getInstance().isEnabled() && stack.getItem() instanceof ItemShulkerBox && (tagCompound = stack.getTagCompound()) != null && tagCompound.hasKey("BlockEntityTag", 10) && (blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag")).hasKey("Items", 9)) {
            ci.cancel();
            NonNullList nonnulllist = NonNullList.withSize((int)27, (Object)ItemStack.EMPTY);
            ItemStackHelper.loadAllItems((NBTTagCompound)blockEntityTag, (NonNullList)nonnulllist);
            GlStateManager.enableBlend();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int width = Math.max(144, this.fontRenderer.getStringWidth(stack.getDisplayName()) + 3);
            int x1 = x + 12;
            int y1 = y - 12;
            int height = 57;
            this.itemRender.zLevel = 300.0f;
            this.drawGradientRectP(x1 - 3, y1 - 4, x1 + width + 3, y1 - 3, -267386864, -267386864);
            this.drawGradientRectP(x1 - 3, y1 + height + 3, x1 + width + 3, y1 + height + 4, -267386864, -267386864);
            this.drawGradientRectP(x1 - 3, y1 - 3, x1 + width + 3, y1 + height + 3, -267386864, -267386864);
            this.drawGradientRectP(x1 - 4, y1 - 3, x1 - 3, y1 + height + 3, -267386864, -267386864);
            this.drawGradientRectP(x1 + width + 3, y1 - 3, x1 + width + 4, y1 + height + 3, -267386864, -267386864);
            this.drawGradientRectP(x1 - 3, y1 - 3 + 1, x1 - 3 + 1, y1 + height + 3 - 1, 0x505000FF, 1344798847);
            this.drawGradientRectP(x1 + width + 2, y1 - 3 + 1, x1 + width + 3, y1 + height + 3 - 1, 0x505000FF, 1344798847);
            this.drawGradientRectP(x1 - 3, y1 - 3, x1 + width + 3, y1 - 3 + 1, 0x505000FF, 0x505000FF);
            this.drawGradientRectP(x1 - 3, y1 + height + 2, x1 + width + 3, y1 + height + 3, 1344798847, 1344798847);
            this.fontRenderer.drawString(stack.getDisplayName(), x + 12, y - 12, 0xFFFFFF);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            int size = nonnulllist.size();
            for (int i = 0; i < size; ++i) {
                int iX = x + i % 9 * 16 + 11;
                int iY = y + i / 9 * 16 - 11 + 8;
                ItemStack itemStack = (ItemStack)nonnulllist.get(i);
                this.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
                this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, itemStack, iX, iY, null);
            }
            RenderHelper.disableStandardItemLighting();
            this.itemRender.zLevel = 0.0f;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    private void drawGradientRectP(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel((int)7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, 300.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, 300.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, 300.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 300.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel((int)7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}

