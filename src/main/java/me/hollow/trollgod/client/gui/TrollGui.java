package me.hollow.trollgod.client.gui;

import net.minecraft.client.gui.*;
import me.hollow.trollgod.client.gui.components.*;
import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.client.gui.components.items.buttons.*;
import me.hollow.trollgod.client.gui.components.items.*;
import java.util.*;
import java.util.function.*;
import org.lwjgl.input.*;
import java.io.*;

public class TrollGui extends GuiScreen
{
    private final ArrayList<Component> components;
    private static TrollGui INSTANCE;
    
    public TrollGui() {
        this.components = new ArrayList<Component>();
        (TrollGui.INSTANCE = this).load();
    }
    
    public static TrollGui getInstance() {
        if (TrollGui.INSTANCE == null) {
            TrollGui.INSTANCE = new TrollGui();
        }
        return TrollGui.INSTANCE;
    }
    
    public static TrollGui getClickGui() {
        return getInstance();
    }
    
    private void load() {
        int x = -84;
        for (final Module.Category category : Module.Category.values()) {
            final ArrayList<Component> components = this.components;
            final String name = category.name();
            x += 110;
            components.add(new Component(name, x, 4, true) {
                @Override
                public void setupItems() {
                    for (final Module module : TrollGod.INSTANCE.getModuleManager().getModulesByCategory(category)) {
                        this.addButton(new ModuleButton(module));
                    }
                }
            });
        }
        for (final Component component : this.components) {
            component.getItems().sort(Comparator.comparing((Function<? super Item, ? extends Comparable>)Item::getName));
        }
    }
    
    public void updateModule(final Module module) {
        for (final Component component : this.components) {
            for (final Item item : component.getItems()) {
                if (item instanceof ModuleButton) {
                    final ModuleButton button = (ModuleButton)item;
                    final Module mod = button.getModule();
                    if (module != null && module.equals(mod)) {
                        button.initSettings();
                        break;
                    }
                    continue;
                }
            }
        }
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.checkMouseWheel();
        for (final Component component : this.components) {
            component.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
    
    public void mouseClicked(final int mouseX, final int mouseY, final int clickedButton) {
        for (final Component component : this.components) {
            component.mouseClicked(mouseX, mouseY, clickedButton);
        }
    }
    
    public void mouseReleased(final int mouseX, final int mouseY, final int releaseButton) {
        for (final Component component : this.components) {
            component.mouseReleased(mouseX, mouseY, releaseButton);
        }
    }
    
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    public final ArrayList<Component> getComponents() {
        return this.components;
    }
    
    public void checkMouseWheel() {
        final int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            for (final Component component : this.components) {
                component.setY(component.getY() - 10);
            }
        }
        else if (dWheel > 0) {
            for (final Component component : this.components) {
                component.setY(component.getY() + 10);
            }
        }
    }
    
    public int getTextOffset() {
        return -6;
    }
    
    public Component getComponentByName(final String name) {
        for (final Component component : this.components) {
            if (component.getName().equalsIgnoreCase(name)) {
                return component;
            }
        }
        return null;
    }
    
    public void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (final Component component : this.components) {
            component.onKeyTyped(typedChar, keyCode);
        }
    }
    
    static {
        TrollGui.INSTANCE = new TrollGui();
    }
}
