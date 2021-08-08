package me.hollow.trollgod.client.command.commands;

import me.hollow.trollgod.client.command.*;
import me.hollow.trollgod.*;

@CommandManifest(label = "Save", aliases = { "s" })
public class SaveCommand extends Command
{
    @Override
    public void execute(final String[] args) {
        TrollGod.INSTANCE.getConfigManager().saveConfig(TrollGod.INSTANCE.getConfigManager().config.replaceFirst("TrollGod/", ""));
    }
}
