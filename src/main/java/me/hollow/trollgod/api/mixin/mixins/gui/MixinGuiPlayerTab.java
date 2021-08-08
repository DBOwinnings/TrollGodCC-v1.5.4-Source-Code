package me.hollow.trollgod.api.mixin.mixins.gui;

import org.spongepowered.asm.mixin.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.network.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import me.hollow.trollgod.client.modules.client.*;
import org.spongepowered.asm.mixin.injection.*;
import net.minecraft.scoreboard.*;
import me.hollow.trollgod.*;
import com.mojang.realmsclient.gui.*;

@Mixin({ GuiPlayerTabOverlay.class })
public class MixinGuiPlayerTab
{
    @Inject(method = { "getPlayerName" }, at = { @At("HEAD") }, cancellable = true)
    public void getPlayerName(final NetworkPlayerInfo networkPlayerInfoIn, final CallbackInfoReturnable returnable) {
        if (Manage.INSTANCE.tabTweaks.getValue()) {
            returnable.cancel();
            returnable.setReturnValue(this.getPlayerName(networkPlayerInfoIn));
        }
    }
    
    public String getPlayerName(final NetworkPlayerInfo networkPlayerInfoIn) {
        final String dname = (networkPlayerInfoIn.getDisplayName() != null) ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName((Team)networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (TrollGod.INSTANCE.getFriendManager().isFriend(dname)) {
            String tweaks = "";
            if (Manage.INSTANCE.highlightFriends.getValue()) {
                tweaks = tweaks + ChatFormatting.DARK_PURPLE + "" + ChatFormatting.BOLD;
            }
            return tweaks + dname;
        }
        return dname;
    }
}
