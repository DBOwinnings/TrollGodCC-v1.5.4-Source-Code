package me.hollow.trollgod.api.mixin;

import net.minecraftforge.fml.relauncher.*;
import org.spongepowered.asm.launch.*;
import org.spongepowered.asm.mixin.*;
import java.util.*;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class Loader implements IFMLLoadingPlugin
{
    public Loader() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.trollgod.json");
    }
    
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
}
