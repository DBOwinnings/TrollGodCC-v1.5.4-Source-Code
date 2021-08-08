package me.hollow.trollgod.client.gui.components.items;

import me.hollow.trollgod.api.interfaces.*;
import me.hollow.trollgod.client.modules.client.*;
import java.awt.*;

public class Item implements Minecraftable
{
    protected float x;
    protected float y;
    protected int width;
    protected int height;
    private boolean hidden;
    final String name;
    
    public Item(final String name) {
        this.name = name;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public int getColor(final boolean hovered) {
        return hovered ? -2007673515 : 290805077;
    }
    
    public int getColor() {
        return new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue()).getRGB();
    }
    
    public void setLocation(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
    }
    
    public void update() {
    }
    
    public void onKeyTyped(final char typedChar, final int keyCode) {
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
    
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
}
