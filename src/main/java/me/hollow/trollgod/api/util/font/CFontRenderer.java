package me.hollow.trollgod.api.util.font;

import net.minecraft.client.renderer.texture.*;
import java.awt.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;

public class CFontRenderer extends CFont
{
    protected final CharData[] boldChars;
    protected final CharData[] italicChars;
    protected final CharData[] boldItalicChars;
    private final int[] colorCode;
    private final String colorcodeIdentifiers = "0123456789abcdefklmnor";
    protected DynamicTexture texBold;
    protected DynamicTexture texItalic;
    protected DynamicTexture texItalicBold;
    
    public CFontRenderer(final Font font, final boolean antiAlias, final boolean fractionalMetrics) {
        super(font, antiAlias, fractionalMetrics);
        this.boldChars = new CharData[256];
        this.italicChars = new CharData[256];
        this.boldItalicChars = new CharData[256];
        this.colorCode = new int[32];
        this.setupMinecraftColorcodes();
        this.setupBoldItalicIDs();
    }
    
    public float drawStringWithShadow(final String text, final double x, final double y, final int color) {
        final float shadowWidth = this.drawString(text, x + 1.0, y + 1.0, color, true);
        return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
    }
    
    public float drawString(final String text, final float x, final float y, final int color) {
        return this.drawString(text, x, y, color, false);
    }
    
    public float drawCenteredStringWithShadow(final String text, final float x, final float y, final int color) {
        return this.drawStringWithShadow(text, x - (float)this.getStringWidth(text) / 2.0f, y, color);
    }
    
    public float drawCenteredString(final String text, final float x, final float y, final int color) {
        return this.drawString(text, x - (float)this.getStringWidth(text) / 2.0f, y, color);
    }
    
    public float drawString(final String text, double x, double y, int color, final boolean shadow) {
        --x;
        y -= 2.0;
        if (text == null) {
            return 0.0f;
        }
        if (color == 553648127) {
            color = 16777215;
        }
        if ((color & 0xFC000000) == 0x0) {
            color |= 0xFF000000;
        }
        if (shadow) {
            color = ((color & 0xFCFCFC) >> 2 | (color & 0xFF000000));
        }
        CharData[] currentData = this.charData;
        final float alpha = (color >> 24 & 0xFF) / 255.0f;
        boolean bold = false;
        boolean italic = false;
        x *= 2.0;
        y *= 2.0;
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((color >> 16 & 0xFF) / 255.0f, (color >> 8 & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, alpha);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.tex.getGlTextureId());
        GL11.glBindTexture(3553, this.tex.getGlTextureId());
        for (int i = 0; i < text.length(); ++i) {
            final char character = text.charAt(i);
            if (character == '\uFFFD' ) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }
                    if (shadow) {
                        colorIndex += 16;
                    }
                    final int colorcode = this.colorCode[colorIndex];
                    GlStateManager.color((colorcode >> 16 & 0xFF) / 255.0f, (colorcode >> 8 & 0xFF) / 255.0f, (colorcode & 0xFF) / 255.0f, alpha);
                }
                else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    }
                    else {
                        GlStateManager.bindTexture(this.texBold.getGlTextureId());
                        currentData = this.boldChars;
                    }
                }
                else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
                        currentData = this.boldItalicChars;
                    }
                    else {
                        GlStateManager.bindTexture(this.texItalic.getGlTextureId());
                        currentData = this.italicChars;
                    }
                }
                else {
                    bold = false;
                    italic = false;
                    GlStateManager.color((color >> 16 & 0xFF) / 255.0f, (color >> 8 & 0xFF) / 255.0f, (color & 0xFF) / 255.0f, alpha);
                    GlStateManager.bindTexture(this.tex.getGlTextureId());
                    currentData = this.charData;
                }
                ++i;
            }
            else if (character < currentData.length) {
                this.drawChar(currentData, character, (float)x, (float)y);
                final double n = x;
                final int n2 = currentData[character].width - 8;
                this.getClass();
                x = n + (n2 + 0);
            }
        }
        GL11.glHint(3155, 4352);
        GL11.glPopMatrix();
        return (float)x / 2.0f;
    }
    
    @Override
    public double getStringWidth(final String text) {
        if (text == null) {
            return 0.0;
        }
        int width = 0;
        final CharData[] currentData = this.charData;
        for (int size = text.length(), i = 0; i < size; ++i) {
            final char character = text.charAt(i);
            if (character == '\uFFFD' ) {
                ++i;
            }
            else if (character < currentData.length) {
                final int n = width;
                final int n2 = currentData[character].width - 8;
                this.getClass();
                width = n + (n2 + 0);
            }
        }
        return width / 2.0f;
    }
    
    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        this.setupBoldItalicIDs();
    }
    
    @Override
    public void setAntiAlias(final boolean antiAlias) {
        super.setAntiAlias(antiAlias);
        this.setupBoldItalicIDs();
    }
    
    @Override
    public void setFractionalMetrics(final boolean fractionalMetrics) {
        super.setFractionalMetrics(fractionalMetrics);
        this.setupBoldItalicIDs();
    }
    
    private void setupBoldItalicIDs() {
        this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
        this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
        this.texItalicBold = this.setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
    }
    
    private void setupMinecraftColorcodes() {
        for (int index = 0; index < 32; ++index) {
            final int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index & 0x1) * 170 + noClue;
            if (index == 6) {
                red += 85;
            }
            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCode[index] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
        }
    }
}
