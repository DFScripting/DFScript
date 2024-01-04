package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;
import net.minecraft.text.Text;

public class ScriptDeletionScreen extends CScreen {

    public ScriptDeletionScreen(Script script) {
        super(103, 27);

        widgets.add(new CText(5, 5, Text.literal("Delete " + script.getName() + "?")));

        widgets.add(new CButton(5, 12, 45, 10, "Delete", () -> {
            ScriptManager.getInstance().deleteScript(script);
            DFScript.MC.setScreen(new ScriptListScreen(true));
        }));

        widgets.add(new CButton(52, 12, 45, 10, "Cancel", () -> DFScript.MC.setScreen(new ScriptListScreen(true))));
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptListScreen(true));
    }
}
