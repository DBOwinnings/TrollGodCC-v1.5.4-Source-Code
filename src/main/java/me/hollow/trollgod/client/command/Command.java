package me.hollow.trollgod.client.command;

import java.lang.annotation.*;

public class Command
{
    String label;
    String[] aliases;
    
    public Command() {
        if (this.getClass().isAnnotationPresent(CommandManifest.class)) {
            final CommandManifest manifest = this.getClass().getAnnotation(CommandManifest.class);
            this.label = manifest.label();
            this.aliases = manifest.aliases();
        }
    }
    
    public void execute(final String[] args) {
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String[] getAliases() {
        return this.aliases;
    }
}
