/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.gui.components.items;

import java.awt.Color;
import me.hollow.trollgod.api.interfaces.Minecraftable;
import me.hollow.trollgod.client.modules.client.ClickGui;

public class Item
implements Minecraftable {
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    private boolean hidden;
    final String name;

    public Item(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }

    public int getColor(boolean hovered) {
        return !hovered ? 0x11555555 : -2007673515;
    }

    public int getColor() {
        return new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue()).getRGB();
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    }

    public void update() {
    }

    public void onKeyTyped(char typedChar, int keyCode) {
    }

    public final float getX() {
        return this.x;
    }

    public final float getY() {
        return this.y;
    }

    public final int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

