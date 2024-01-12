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
    Pattern ILLEGAL_CHARS = Pattern.compile("[\\\\/:*?\"<>|\n]");

    protected ScriptCreationScreen() {
        super(100, 33);

        widgets.add(new CText(4, 4, Text.of("Create Script")));

        CTextField name = new CTextField("My Script", 4, 9, 92, 9, true){
            @Override
            public void keyPressed(int keyCode, int scanCode, int modifiers) {
                if(keyCode == 257) return;
                super.keyPressed(keyCode, scanCode, modifiers);
            }
        };

        name.setChangedListener(() -> {
            String scriptName = name.getText();

            Matcher m = ILLEGAL_CHARS.matcher(scriptName);

            name.textColor = m.find() ? 0xFF3333 : 0xFFFFFF;
        });

        widgets.add(name);

        widgets.add(new CButton(4, 20, 44, 9, "Create", () -> {
            String scriptName = name.getText();

            Matcher m = ILLEGAL_CHARS.matcher(scriptName);

            if (m.find()) return;

            ScriptManager.getInstance().createScript(name.getText());
            DFScript.MC.setScreen(new ScriptListScreen(true));
        }));

        widgets.add(new CButton(52, 20, 44, 9, "Cancel", () -> {
            DFScript.MC.setScreen(new ScriptAddScreen());
        }));
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptAddScreen());
    }
}
