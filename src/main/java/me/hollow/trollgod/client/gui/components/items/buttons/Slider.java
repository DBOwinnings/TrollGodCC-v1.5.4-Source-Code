package me.hollow.trollgod.client.gui.components.items.buttons;

import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import com.mojang.realmsclient.gui.*;
import me.hollow.trollgod.client.gui.*;
import me.hollow.trollgod.client.gui.components.*;
import java.util.*;
import org.lwjgl.input.*;

public class Slider extends Button
{
    public final Setting setting;
    private final Number min;
    private final Number max;
    private final int difference;
    
    public Slider(final Setting setting) {
        super(setting.getName());
        this.setting = setting;
        this.min = setting.getMin();
        this.max = setting.getMax();
        this.difference = this.max.intValue() - this.min.intValue();
        this.width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.dragSetting(mouseX, mouseY);
        RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.0f, this.y + this.height - 0.5f, this.isHovering(mouseX, mouseY) ? -2007673515 : 290805077);
        RenderUtil.drawRect(this.x, this.y, (this.setting.getValue().floatValue() <= this.min.floatValue()) ? this.x : (this.x + (this.width + 7.0f) * this.partialMultiplier()), this.y + this.height - 0.5f, 1884640597);
        TrollGod.fontManager.drawString(this.getName() + " " + ChatFormatting.GRAY + ((this.setting.getValue() instanceof Float) ? this.setting.getValue() : Double.valueOf(this.setting.getValue().doubleValue())), this.x + 2.0f, this.y - 1.0f - TrollGui.getClickGui().getTextOffset(), -1);
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            this.setSettingFromX(mouseX);
        }
    }
    
    @Override
    public boolean isHovering(final int mouseX, final int mouseY) {
        for (final Component component : TrollGui.getClickGui().getComponents()) {
            if (component.drag) {
                return false;
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() + 8.0f && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
    
    @Override
    public void update() {
        this.setHidden(!this.setting.isVisible());
    }
    
    private void dragSetting(final int mouseX, final int mouseY) {
        if (this.isHovering(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            this.setSettingFromX(mouseX);
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    private void setSettingFromX(final int mouseX) {
        final float percent = (mouseX - this.x) / (this.width + 7.4f);
        if (this.setting.getValue() instanceof Double) {
            final double result = this.setting.getMin() + this.difference * percent;
            this.setting.setValue(Math.round(10.0 * result) / 10.0);
        }
        else if (this.setting.getValue() instanceof Float) {
            final float result2 = this.setting.getMin() + this.difference * percent;
            this.setting.setValue(Math.round(10.0f * result2) / 10.0f);
        }
        else if (this.setting.getValue() instanceof Integer) {
            this.setting.setValue(this.setting.getMin() + (int)(this.difference * percent));
        }
    }
    
    private float middle() {
        return this.max.floatValue() - this.min.floatValue();
    }
    
    private float part() {
        return this.setting.getValue().floatValue() - this.min.floatValue();
    }
    
    private float partialMultiplier() {
        return this.part() / this.middle();
    }
}
