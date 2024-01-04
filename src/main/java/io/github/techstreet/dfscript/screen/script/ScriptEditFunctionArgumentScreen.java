package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CButton;
import io.github.techstreet.dfscript.screen.widget.CTextField;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.event.ScriptFunction;

import java.util.Objects;

public class ScriptEditFunctionArgumentScreen extends CScreen {
    private final Script script;

    private final ScriptFunction function;
    private final ScriptActionArgument argument;

    public ScriptEditFunctionArgumentScreen(Script script, ScriptFunction f, ScriptActionArgument a) {
        super(100, 50);
        this.script = script;
        argument = a;
        function = f;

        CTextField input = new CTextField(argument.name(), 2, 2, 96, 35, true);

        input.setChangedListener(() -> input.textColor = 0xFFFFFF);

        CButton confirm = new CButton(2, 37, 46, 10, "Rename", () -> {
            if (!Objects.equals(argument.name(), input.getText())) {
                if (function.argList().argumentExists(input.getText())) {
                    input.textColor = 0xFF3333;
                } else {
                    //script.replaceOption(option.getName(), input.getText());
                    function.replaceArgument(argument.name(), input.getText());
                    argument.setName(input.getText());
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
        DFScript.MC.setScreen(new ScriptEditFunctionScreen(function, script));
    }
}
