package me.hollow.trollgod.client.gui.components.items.buttons;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.client.gui.components.items.*;
import me.hollow.trollgod.api.property.*;
import java.util.*;
import me.hollow.trollgod.api.util.render.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.gui.*;
import net.minecraft.init.*;
import net.minecraft.client.audio.*;

public class ModuleButton extends Button
{
    private final Module module;
    private List<Item> items;
    private boolean subOpen;
    
    public ModuleButton(final Module module) {
        super(module.getLabel());
        this.items = new ArrayList<Item>();
        this.module = module;
        this.initSettings();
    }
    
    public void initSettings() {
        final List<Item> newItems = new ArrayList<Item>();
        if (!this.module.getSettings().isEmpty()) {
            for (final Setting setting : this.module.getSettings()) {
                if (setting.getValue() instanceof Boolean && !setting.getName().equals("Enabled")) {
                    newItems.add(new BooleanButton(setting));
                }
                if (setting.getValue() instanceof Bind) {
                    newItems.add(new BindButton(setting));
                }
                if (setting.getValue() instanceof String || setting.getValue() instanceof Character) {
                    newItems.add(new StringButton(setting));
                }
                if (setting.isNumberSetting()) {
                    if (setting.hasRestriction()) {
                        newItems.add(new Slider(setting));
                        continue;
                    }
                    newItems.add(new UnlimitedSlider(setting));
                }
                if (setting.isEnumSetting()) {
                    newItems.add(new EnumButton(setting));
                }
            }
        }
        this.items = newItems;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height - 0.5f, this.getColor(this.isHovering(mouseX, mouseY)));
        TrollGod.fontManager.drawString(this.getName(), this.x + 2.0f, this.y - 2.0f - TrollGui.getClickGui().getTextOffset(), this.module.isEnabled() ? this.getColor() : -1);
        if (!this.items.isEmpty() && this.subOpen) {
            float height = 1.0f;
            for (final Item item : this.items) {
                if (!item.isHidden()) {
                    height += 15.0f;
                    item.setLocation(this.x + 1.0f, this.y + height);
                    item.setHeight(15);
                    item.setWidth(this.width - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                }
                item.update();
            }
        }
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                ModuleButton.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_FALL, 10.0f));
            }
            if (this.subOpen) {
                for (final Item item : this.items) {
                    if (!item.isHidden()) {
                        item.mouseClicked(mouseX, mouseY, mouseButton);
                    }
                }
            }
        }
    }
    
    @Override
    public void onKeyTyped(final char typedChar, final int keyCode) {
        super.onKeyTyped(typedChar, keyCode);
        if (!this.items.isEmpty() && this.subOpen) {
            for (final Item item : this.items) {
                if (!item.isHidden()) {
                    item.onKeyTyped(typedChar, keyCode);
                }
            }
        }
    }
    
    @Override
    public int getHeight() {
        if (this.subOpen) {
            int height = 14;
            for (final Item item : this.items) {
                if (!item.isHidden()) {
                    height += item.getHeight() + 1;
                }
            }
            return height + 2;
        }
        return 14;
    }
    
    public Module getModule() {
        return this.module;
    }
    
    @Override
    public void toggle() {
        this.module.toggle();
    }
    
    @Override
    public boolean getState() {
        return this.module.isEnabled();
    }
}
