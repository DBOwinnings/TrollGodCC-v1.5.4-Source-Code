/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.gui.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.api.interfaces.Minecraftable;
import me.hollow.trollgod.api.util.render.RenderUtil;
import me.hollow.trollgod.client.gui.TrollGui;
import me.hollow.trollgod.client.gui.components.items.Item;
import me.hollow.trollgod.client.gui.components.items.buttons.Button;
import me.hollow.trollgod.client.modules.client.ClickGui;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class Component
implements Minecraftable {
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    public boolean drag;
    private final List<Item> items = new ArrayList<Item>();
    private boolean hidden = false;
    private final String name;

    public Component(String name, int x, int y, boolean open) {
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

    private void drag(int mouseX, int mouseY) {
        if (!this.drag) {
            return;
        }
        this.x = this.x2 + mouseX;
        this.y = this.y2 + mouseY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drag(mouseX, mouseY);
        float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0f : 0.0f;
        RenderUtil.drawRect(this.x - 4, this.y - 2, this.x + this.width + 4, this.y + this.height - 6, new Color(ClickGui.getInstance().categoryRed.getValue(), ClickGui.getInstance().categoryGreen.getValue(), ClickGui.getInstance().categoryBlue.getValue(), ClickGui.getInstance().categoryAlpha.getValue()).getRGB());
        if (this.open) {
            RenderUtil.drawRect(this.x, (float)this.y + 12.0f, this.x + this.width, (float)(this.y + this.height) + totalItemHeight, new Color(0, 0, 0, ClickGui.getInstance().alpha.getValue()).getRGB());
        }
        TrollGod.fontManager.drawString(this.getName(), (float)this.x + (float)this.width / 2.0f - (float)(TrollGod.fontManager.getStringWidth(this.getName()) / 2), (float)this.y - 4.0f - (float)TrollGui.getClickGui().getTextOffset(), 0xFFFFFF);
        if (this.open) {
            float y = (float)(this.getY() + this.getHeight()) - 3.0f;
            for (int i = 0; i < this.getItems().size(); ++i) {
                Item item = this.getItems().get(i);
                if (item.isHidden()) continue;
                item.setLocation((float)this.x + 2.0f, y);
                item.setWidth(this.getWidth() - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += (float)item.getHeight() + 1.0f;
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            for (Component component : TrollGui.getClickGui().getComponents()) {
                if (!component.drag) continue;
                component.drag = false;
            }
            this.drag = true;
            return;
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord((SoundEvent)SoundEvents.BLOCK_ANVIL_FALL, (float)10.0f));
            return;
        }
        if (!this.open) {
            return;
        }
        for (Item item : this.getItems()) {
            item.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }
        if (!this.open) {
            return;
        }
        for (Item item : this.getItems()) {
            item.mouseReleased(mouseX, mouseY, releaseButton);
        }
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (!this.open) {
            return;
        }
        for (Item item : this.getItems()) {
            item.onKeyTyped(typedChar, keyCode);
        }
    }

    public void addButton(Button button) {
        this.items.add(button);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
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

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHidden(boolean hidden) {
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

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0.0f;
        for (Item item : this.getItems()) {
            height += (float)item.getHeight() + 1.5f;
        }
        return height;
    }
}

