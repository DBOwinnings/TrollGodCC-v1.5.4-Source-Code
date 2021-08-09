/*
 * Decompiled with CFR 0.151.
 */
package me.hollow.trollgod.api.mixin;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@IFMLLoadingPlugin.MCVersion(value="1.12.2")
public class Loader
implements IFMLLoadingPlugin {
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

    public void injectData(Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }
}

