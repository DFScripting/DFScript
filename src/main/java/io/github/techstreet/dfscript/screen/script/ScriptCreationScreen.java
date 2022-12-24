package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CText;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.ScriptManager;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptCreationScreen extends CScreen {

    // invalid file name chars
    // there's definitely a better place to put this but this felt the simplest rn
    Pattern ILLEGAL_CHARS = Pattern.compile("[\\x00-\\x1f<>:\"\\\\/|?*]");

    protected ScriptCreationScreen() {
        super(103,35);

        widgets.add(new CText(4, 3, Text.literal("Name your script:")));
        CTextField name = new CTextField("My Script", 3, 8, 95, 8, true);
        CButton addButton = new CButton(3, 19, 46, 10, "Create", () -> {
            Matcher m = ILLEGAL_CHARS.matcher(name.getText());
            if(m.find()) return;
            ScriptManager.getInstance().createScript(name.getText());
            DFScript.MC.setScreen(new ScriptListScreen(true));
        });
        name.setChangedListener(() -> {
            name.textColor = 0xFFFFFF;
            addButton.setDisabled(false);
            Matcher m = ILLEGAL_CHARS.matcher(name.getText());
            if(m.find()) {
                name.textColor = 0xFF3333;
                addButton.setDisabled(true);
            }
        });
        widgets.add(name);
        widgets.add(addButton);
        widgets.add(new CButton(52, 19, 46, 10, "Cancel", () -> {
            DFScript.MC.setScreen(new ScriptAddScreen());
        }));
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddScreen());
    }
}
