/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import me.hollow.trollgod.TrollGod;
import me.hollow.trollgod.client.gui.components.Component;
import me.hollow.trollgod.client.gui.components.items.Item;
import me.hollow.trollgod.client.gui.components.items.buttons.ModuleButton;
import me.hollow.trollgod.client.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class TrollGui
extends GuiScreen {
    private final ArrayList<Component> components = new ArrayList();
    private static TrollGui INSTANCE = new TrollGui();

    public TrollGui() {
        INSTANCE = this;
        this.load();
    }

    public static TrollGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TrollGui();
        }
        return INSTANCE;
    }

    public static TrollGui getClickGui() {
        return TrollGui.getInstance();
    }

    private void load() {
        int x = -84;
        for (final Module.Category category : Module.Category.values()) {
            this.components.add(new Component(category.name(), x += 110, 4, true){

                @Override
                public void setupItems() {
                    for (Module module : TrollGod.INSTANCE.getModuleManager().getModulesByCategory(category)) {
                        this.addButton(new ModuleButton(module));
                    }
                }
            });
        }
        for (Component component : this.components) {
            component.getItems().sort(Comparator.comparing(Item::getName));
        }
    }

    public void updateModule(Module module) {
        block0: for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton)item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
                continue block0;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.checkMouseWheel();
        for (Component component : this.components) {
            component.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        for (Component component : this.components) {
            component.mouseClicked(mouseX, mouseY, clickedButton);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        for (Component component : this.components) {
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
        block3: {
            int dWheel;
            block2: {
                dWheel = Mouse.getDWheel();
                if (dWheel >= 0) break block2;
                for (Component component : this.components) {
                    component.setY(component.getY() - 10);
                }
                break block3;
            }
            if (dWheel <= 0) break block3;
            for (Component component : this.components) {
                component.setY(component.getY() + 10);
            }
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) continue;
            return component;
        }
        return null;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (Component component : this.components) {
            component.onKeyTyped(typedChar, keyCode);
        }
    }
}

