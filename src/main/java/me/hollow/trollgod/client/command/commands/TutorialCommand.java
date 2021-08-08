package me.hollow.trollgod.client.command.commands;

import me.hollow.trollgod.client.command.*;
import net.minecraft.client.*;
import net.minecraft.client.tutorial.*;
import me.hollow.trollgod.api.util.*;

@CommandManifest(label = "Tutorial", aliases = { "tut" })
public class TutorialCommand extends Command
{
    @Override
    public void execute(final String[] args) {
        Minecraft.getMinecraft().gameSettings.tutorialStep = TutorialSteps.NONE;
        Minecraft.getMinecraft().getTutorial().setStep(TutorialSteps.NONE);
        MessageUtil.sendClientMessage("Set tutorial step to none!", -11114);
    }
}
