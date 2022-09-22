package io.github.techstreet.dfscript.features;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.event.ServerJoinEvent;
import io.github.techstreet.dfscript.event.system.EventManager;
import io.github.techstreet.dfscript.loader.Loadable;
import io.github.techstreet.dfscript.util.VersionUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class UpdateAlerts implements Loadable {
    @Override
    public void load() {
        EventManager.getInstance().register(ServerJoinEvent.class, (event -> {
            int latestVersion = VersionUtil.getLatestVersion();
            int currentVersion = VersionUtil.getCurrentVersionInt();
            int versionsBehind = latestVersion - currentVersion;

            if (versionsBehind >= 1) {
                MutableText message = Text.literal("")
                        .append(Text.literal(String.format("You are currently on build #%s of DFScript, which is %s versions behind the latest (%s). ",
                                currentVersion, versionsBehind, latestVersion))
                                .styled(style -> style.withColor(Formatting.YELLOW)))
                        .append(Text.literal("Click here to download the latest version!")
                                .styled(style -> {
                                    style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/TechStreetDev/DFScript/releases/latest"));
                                    style.withColor(Formatting.AQUA);
                                    return style;
                                }));

                DFScript.MC.player.sendMessage(message, false);
            }
        }));
    }
}
