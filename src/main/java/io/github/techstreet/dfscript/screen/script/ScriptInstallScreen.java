package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import io.github.techstreet.dfscript.script.VirtualScript;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptInstallScreen extends CScreen {

    protected ScriptInstallScreen(VirtualScript script) {
        super(125, 40);

        CText name = new CText(5, 5, Text.literal("Name: " + script.getName()));
        CText owner = new CText(5, 12, Text.literal("Creator: " + script.getOwner()));
        CText id = new CText(5, 19, Text.literal("ID: " + script.getId()));

        widgets.add(name);
        widgets.add(owner);
        widgets.add(id);

        if (DFScript.MC.player != null) {
            for (Script s : ScriptManager.getInstance().getScripts()) {
                if (s.getServer() != null && s.getServer().contains(script.getId())) {
                    if (s.getOwner().contains(script.getOwner())) {
                        return;
                    }

                    widgets.add(new CButton(80, 26, 40, 10, "Update", () -> {
                        script.download(true);
                        DFScript.MC.setScreen(new ScriptListScreen());
                    }));

                    return;
                }
            }

            widgets.add(new CButton(80, 26, 40, 10, "Install", () -> {
                script.download(false);
                DFScript.MC.setScreen(new ScriptListScreen());
            }));
        }
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddScreen());
    }
}
