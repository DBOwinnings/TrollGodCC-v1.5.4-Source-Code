package me.hollow.trollgod.client.gui.components;

import me.hollow.trollgod.api.interfaces.*;
import me.hollow.trollgod.client.gui.components.items.*;
import me.hollow.trollgod.client.modules.client.*;
import java.awt.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import java.util.*;
import me.hollow.trollgod.client.gui.components.items.buttons.*;

public class Component implements Minecraftable
{
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    public boolean drag;
    private final List<Item> items;
    private boolean hidden;
    private final String name;
    
    public Component(final String name, final int x, final int y, final boolean open) {
        this.items = new ArrayList<Item>();
        this.hidden = false;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 18;
        this.open = open;
        this.setupItems();
    }
    
    public final String getName() {
        return this.name;
    }
    
    public void setupItems() {
    }
    
    private void drag(final int mouseX, final int mouseY) {
        if (!this.drag) {
            return;
        }
        this.x = this.x2 + mouseX;
        this.y = this.y2 + mouseY;
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drag(mouseX, mouseY);
        final float totalItemHeight = this.open ? (this.getTotalItemHeight() - 2.0f) : 0.0f;
        RenderUtil.drawRect((float)(this.x - 4), (float)(this.y - 2), (float)(this.x + this.width + 4), (float)(this.y + this.height - 6), new Color(ClickGui.getInstance().categoryRed.getValue(), ClickGui.getInstance().categoryGreen.getValue(), ClickGui.getInstance().categoryBlue.getValue(), ClickGui.getInstance().categoryAlpha.getValue()).getRGB());
        if (this.open) {
            RenderUtil.drawRect((float)this.x, this.y + 12.0f, (float)(this.x + this.width), this.y + this.height + totalItemHeight, new Color(0, 0, 0, ClickGui.getInstance().alpha.getValue()).getRGB());
        }
        TrollGod.fontManager.drawString(this.getName(), this.x + this.width / 2.0f - TrollGod.fontManager.getStringWidth(this.getName()) / 2, this.y - 4.0f - TrollGui.getClickGui().getTextOffset(), 16777215);
        if (this.open) {
            float y = this.getY() + this.getHeight() - 3.0f;
            for (int i = 0; i < this.getItems().size(); ++i) {
                final Item item = this.getItems().get(i);
                if (!item.isHidden()) {
                    item.setLocation(this.x + 2.0f, y);
                    item.setWidth(this.getWidth() - 4);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    y += item.getHeight() + 1.0f;
                }
            }
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            for (final Component component : TrollGui.getClickGui().getComponents()) {
                if (component.drag) {
                    component.drag = false;
                }
            }
            this.drag = true;
            return;
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            Component.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_FALL, 10.0f));
            return;
        }
        if (!this.open) {
            return;
        }
        for (final Item item : this.getItems()) {
            item.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }
        if (!this.open) {
            return;
        }
        for (final Item item : this.getItems()) {
            item.mouseReleased(mouseX, mouseY, releaseButton);
        }
    }
    
    public void onKeyTyped(final char typedChar, final int keyCode) {
        if (!this.open) {
            return;
        }
        for (final Item item : this.getItems()) {
            item.onKeyTyped(typedChar, keyCode);
        }
    }
    
    public void addButton(final Button button) {
        this.items.add(button);
    }
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }
    
    public boolean isHidden() {
        return this.hidden;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public final List<Item> getItems() {
        return this.items;
    }
    
    private boolean isHovering(final int mouseX, final int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }
    
    private float getTotalItemHeight() {
        float height = 0.0f;
        for (final Item item : this.getItems()) {
            height += item.getHeight() + 1.5f;
        }
        return height;
    }
}
