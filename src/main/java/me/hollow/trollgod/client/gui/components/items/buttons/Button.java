package me.hollow.trollgod.client.gui.components.items.buttons;

import me.hollow.trollgod.client.gui.components.items.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;
import me.hollow.trollgod.client.gui.components.*;
import java.util.*;

public class Button extends Item
{
    private boolean state;
    
    public Button(final String name) {
        super(name);
        this.height = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height - 0.5f, this.getColor(this.isHovering(mouseX, mouseY)));
        TrollGod.fontManager.drawString(this.getName(), this.x + 2.0f, this.y - 2.0f - TrollGui.getClickGui().getTextOffset(), this.getState() ? this.getColor() : -1);
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }
    
    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        Button.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_METAL_PLACE, 10.0f));
    }
    
    public void toggle() {
    }
    
    public boolean getState() {
        return this.state;
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    public boolean isHovering(final int mouseX, final int mouseY) {
        final ArrayList<Component> components = TrollGui.getClickGui().getComponents();
        for (int i = 0; i < components.size(); ++i) {
            if (components.get(i).drag) {
                return false;
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
