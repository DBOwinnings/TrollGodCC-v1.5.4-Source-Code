package me.hollow.trollgod.client.command.commands;

import me.hollow.trollgod.client.command.*;
import me.hollow.trollgod.*;
import me.hollow.trollgod.api.util.*;
import me.hollow.trollgod.client.modules.*;

@CommandManifest(label = "Toggle", aliases = { "t" })
public class ToggleCommand extends Command
{
    @Override
    public void execute(final String[] args) {
        if (args.length < 2) {
            return;
        }
        final Module module = TrollGod.INSTANCE.getModuleManager().getModuleByLabel(args[1]);
        if (module != null) {
            module.toggle();
            MessageUtil.sendClientMessage(module.getLabel() + " has been toggled " + (module.isEnabled() ? "on" : "off"), false);
        }
    }
}
