package me.hollow.trollgod.client.command.commands;

import me.hollow.trollgod.client.command.*;
import me.hollow.trollgod.api.util.*;
import me.hollow.trollgod.*;
import org.lwjgl.input.*;
import me.hollow.trollgod.api.property.*;
import me.hollow.trollgod.client.modules.*;

@CommandManifest(label = "Bind", aliases = { "b" })
public class BindCommand extends Command
{
    @Override
    public void execute(final String[] args) {
        if (args.length < 2) {
            MessageUtil.sendClientMessage("Use the command like this -> (module, bind)", true);
            return;
        }
        final Module module = TrollGod.INSTANCE.getModuleManager().getModuleByLabel(args[1]);
        if (module != null) {
            final int index = Keyboard.getKeyIndex(args[2].toUpperCase());
            module.bind.setValue(new Bind(index));
            MessageUtil.sendClientMessage(module.getLabel() + " has been bound to " + Keyboard.getKeyName(index), false);
        }
    }
}
