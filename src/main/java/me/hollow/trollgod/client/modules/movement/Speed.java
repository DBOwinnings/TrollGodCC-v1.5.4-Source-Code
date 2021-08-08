package me.hollow.trollgod.client.modules.movement;

import me.hollow.trollgod.client.modules.*;

@ModuleManifest(label = "Speed", listen = false, category = Category.MOVEMENT, color = 11437534)
public class Speed extends Module
{
    @Override
    public void onEnable() {
        this.setSuffix("Strafe");
    }
}
