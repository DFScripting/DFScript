package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.options.ScriptNamedOption;

import java.util.Objects;

public class ScriptEditSettingScreen extends CScreen {
    private final Script script;
    private final ScriptNamedOption option;

    public ScriptEditSettingScreen(Script script, ScriptNamedOption o) {
        super(100, 50);
        this.script = script;
        option = o;

        CTextField input = new CTextField(option.getName(), 2, 2, 96, 35, true);

        input.setChangedListener(() -> input.textColor = 0xFFFFFF);

        CButton confirm = new CButton(2, 37, 46, 10, "Rename", () -> {
            if(!Objects.equals(option.getName(), input.getText())) {
                if(script.optionExists(input.getText())) {
                    input.textColor = 0xFF3333;
                } else {
                    //script.replaceOption(option.getName(), input.getText());
                    option.setName(input.getText());
                    close();
                }
            } else {
                close();
            }
        });

        CButton cancel = new CButton(52, 37, 46, 10, "Cancel", this::close);

        widgets.add(input);
        widgets.add(confirm);
        widgets.add(cancel);
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptSettingsScreen(script, true));
    }
}
