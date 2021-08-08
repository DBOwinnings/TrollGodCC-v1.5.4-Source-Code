package me.hollow.trollgod.client.managers;

import me.hollow.trollgod.client.modules.*;
import me.hollow.trollgod.client.modules.client.*;
import me.hollow.trollgod.client.modules.combat.*;
import me.hollow.trollgod.client.modules.movement.*;
import me.hollow.trollgod.client.modules.player.*;
import me.hollow.trollgod.client.modules.misc.*;
import me.hollow.trollgod.client.modules.visual.*;
import java.util.*;

public class ModuleManager
{
    private final List<Module> modules;
    private int size;
    
    public ModuleManager() {
        this.modules = new ArrayList<Module>();
        this.size = 0;
    }
    
    public void init() {
        this.register(new HUD());
        this.register(new ClickGui());
        this.register(new PopCounter());
        this.register(new MiddleClick());
        this.register(new Manage());
        this.register(new Colours());
        this.register(new FontModule());
        this.register(new SelfTrap());
        this.register(new KillAura());
        this.register(new AutoArmor());
        this.register(new AutoCrystal());
        this.register(new AutoTrap());
        this.register(new Burrow());
        this.register(new Criticals());
        this.register(new HoleFiller());
        this.register(new Offhand());
        this.register(new AutoFeetPlace());
        this.register(new AntiRegear());
        this.register(new FakePlayer());
        this.register(new PacketCanceller());
        this.register(new MultiTask());
        this.register(new NoRotate());
        this.register(new AutoRespawn());
        this.register(new AntiPush());
        this.register(new ChatSuffix());
        this.register(new ReverseStep());
        this.register(new Speed());
        this.register(new Step());
        this.register(new Strafe());
        this.register(new LiquidTweaks());
        this.register(new SpeedTest());
        this.register(new BehindSpoof());
        this.register(new Interact());
        this.register(new Stacker());
        this.register(new SpeedMine());
        this.register(new AutoTotem());
        this.register(new InstaMine());
        this.register(new AntiVoid());
        this.register(new NoFall());
        this.register(new LavaESP());
        this.register(new BlockHighlight());
        this.register(new EnchantColor());
        this.register(new EntityESP());
        this.register(new HoleESP());
        this.register(new Nametags());
        this.register(new SkyColour());
        this.register(new ViewmodelChanger());
        this.register(new ShulkerPreview());
        this.register(new VoidESP());
        this.register(new NoRender());
        this.register(new Skeleton());
        this.register(new LogOutSpots());
        this.register(new TimeChanger());
        this.register(new Trajectories());
        this.register(new NoArmorRender());
        this.register(new Tracers());
        this.modules.forEach(Module::onLoad);
        this.size = this.modules.size();
    }
    
    public int getSize() {
        return this.size;
    }
    
    private void register(final Module module) {
        this.modules.add(module);
    }
    
    public final List<Module> getModules() {
        return this.modules;
    }
    
    public final Module getModuleByClass(final Class<?> clazz) {
        Module module = null;
        for (int size = this.modules.size(), i = 0; i < size; ++i) {
            final Module m = this.modules.get(i);
            if (m.getClass() == clazz) {
                module = m;
            }
        }
        return module;
    }
    
    public final List<Module> getModulesByCategory(final Module.Category category) {
        final List<Module> list = new ArrayList<Module>();
        for (final Module module : this.modules) {
            if (module.getCategory().equals(category)) {
                list.add(module);
            }
        }
        return list;
    }
    
    public final Module getModuleByLabel(final String label) {
        Module module = null;
        for (final Module m : this.modules) {
            if (m.getLabel().equalsIgnoreCase(label)) {
                module = m;
            }
        }
        return module;
    }
}
