package me.hollow.trollgod.client.modules.visual;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.api.property.*;
import java.awt.*;

@ModuleManifest(label = "EnchantColor", listen = false, category = Category.VISUAL, color = 16750899)
public class EnchantColor extends Module
{
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    public static EnchantColor INSTANCE;
    
    public EnchantColor() {
        this.red = (Setting<Integer>)this.register(new Setting("Red", (T)255, (T)0, (T)255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", (T)255, (T)0, (T)255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", (T)255, (T)0, (T)255));
        EnchantColor.INSTANCE = this;
    }
    
    @Override
    public int getColor() {
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue()).getRGB();
    }
}
