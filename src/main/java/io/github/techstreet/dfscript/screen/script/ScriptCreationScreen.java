package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.ScriptManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptCreationScreen extends CScreen {

    // invalid file name chars
    // there's definitely a better place to put this but this felt the simplest rn
    Pattern ILLEGAL_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");

    protected ScriptCreationScreen() {
        super(105, 60);

        CTextField name = new CTextField("My Script", 2, 2, 96, 36, true);

        name.setChangedListener(() -> name.textColor = 0xFFFFFF);

        widgets.add(name);

        widgets.add(new CButton(2, 42, 48, 15, "Create", () -> {
            String scriptName = name.getText();

            Matcher m = ILLEGAL_CHARS.matcher(scriptName);

            if (m.find()) {
                name.textColor = 0xFF3333;
                return;
            }

            ScriptManager.getInstance().createScript(name.getText());
            io.github.techstreet.dfscript.DFScript.MC.setScreen(new ScriptListScreen());
        }));

        widgets.add(new CButton(50, 42, 48, 15, "Cancel", () -> {
            DFScript.MC.setScreen(new ScriptAddScreen());
        }));
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddScreen());
    }
}
