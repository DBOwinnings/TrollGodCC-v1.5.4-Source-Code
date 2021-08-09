package me.hollow.trollgod.api.util.font;

import me.hollow.trollgod.api.interfaces.*;
import java.util.regex.*;
import net.minecraft.client.gui.*;
import me.hollow.trollgod.client.modules.visual.*;
import java.awt.*;

public class RainbowText implements Minecraftable
{
    public static Rainbow rainbow;
    private static final Pattern COLOR_CODE_PATTERN;
    private static final int[] colorCodes;
    
    public static void drawStringWithShadow(final String text, double x, final double y, final int color, final boolean useCustom) {
        int rgb = color;
        int displayColor = 0;
        final char[] characters = text.toCharArray();
        final String[] parts = RainbowText.COLOR_CODE_PATTERN.split(text);
        int index = 0;
        for (int i = 0; i < parts.length; ++i) {
            final String s = parts[i];
            final String[] parts2 = s.split("");
            for (int j = 0; j < parts2.length; ++j) {
                final String s2 = parts2[j];
                if (displayColor == 0) {
                    rgb = RainbowText.rainbow.drawStringWithShadow(s2, (float)x, (float)y, rgb);
                }
                else {
                    rgb = RainbowText.rainbow.updateRainbow(rgb);
                    RainbowText.mc.fontRenderer.drawStringWithShadow(s2, (float)x, (float)y, displayColor);
                }
                x += RainbowText.rainbow.getCharWidth(s2.charAt(0));
                ++index;
            }
            if (index < characters.length) {
                final char colorCode = characters[index];
                if (colorCode == '\uFFFD' ) {
                    final char colorChar = characters[index + 1];
                    final int codeIndex = "0123456789abcdef".indexOf(colorChar);
                    if (codeIndex < 0) {
                        if (useCustom && colorChar == 'r') {
                            displayColor = -1;
                        }
                        else if (colorChar == (useCustom ? '.' : 'r')) {
                            displayColor = 0;
                        }
                    }
                    else {
                        displayColor = RainbowText.colorCodes[codeIndex];
                    }
                    index += 2;
                }
            }
        }
    }
    
    static {
        RainbowText.rainbow = new Rainbow();
        COLOR_CODE_PATTERN = Pattern.compile("�[0123456789abcdefklmnor]");
        colorCodes = new int[] { 0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 5635925, 5636095, 16733525, 16733695, 16777045, 16777215 };
    }
    
    public static class Rainbow
    {
        private static int rgb;
        public static int a;
        public static int r;
        public static int g;
        public static int b;
        static float hue;
        FontRenderer fontRenderer;
        public int FONT_HEIGHT;
        
        public Rainbow() {
            this.fontRenderer = Minecraftable.mc.fontRenderer;
            this.FONT_HEIGHT = 9;
        }
        
        public void updateRainbow() {
            Rainbow.rgb = Color.HSBtoRGB(Rainbow.hue, HUD.saturation.getValue() / 255.0f, HUD.brightness.getValue() / 255.0f);
            Rainbow.a = (Rainbow.rgb >>> 24 & 0xFF);
            Rainbow.r = (Rainbow.rgb >>> 16 & 0xFF);
            Rainbow.g = (Rainbow.rgb >>> 8 & 0xFF);
            Rainbow.b = (Rainbow.rgb & 0xFF);
            Rainbow.hue += 1.0E-5f;
            if (Rainbow.hue > 1.0f) {
                --Rainbow.hue;
            }
        }
        
        public int updateRainbow(final int IN) {
            float hue2 = Color.RGBtoHSB(new Color(IN).getRed(), new Color(IN).getGreen(), new Color(IN).getBlue(), null)[0];
            hue2 += HUD.factor.getValue() / 1000.0f;
            if (hue2 > 1.0f) {
                --hue2;
            }
            return Color.HSBtoRGB(hue2, HUD.saturation.getValue() / 255.0f, HUD.brightness.getValue() / 255.0f);
        }
        
        public int drawStringWithShadow(final String text, final float x, final float y, int color) {
            if (color == -1) {
                color = Rainbow.rgb;
                this.updateRainbow();
            }
            else {
                color = this.updateRainbow(color);
            }
            this.fontRenderer.drawStringWithShadow(text, x, y, color);
            return color;
        }
        
        public int drawString(final String text, final int x, final int y, int color) {
            if (color == -1) {
                color = Rainbow.rgb;
            }
            this.updateRainbow();
            this.fontRenderer.drawStringWithShadow(text, (float)x, (float)y, color);
            return color;
        }
        
        public int getStringWidth(final String text) {
            return this.fontRenderer.getStringWidth(text);
        }
        
        public int getCharWidth(final char character) {
            return this.fontRenderer.getCharWidth(character);
        }
        
        public int getHeight() {
            return this.FONT_HEIGHT;
        }
        
        static {
            Rainbow.hue = 0.01f;
        }
    }
}
