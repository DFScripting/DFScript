package io.github.techstreet.dfscript.screen.script;

import io.github.techstreet.dfscript.DFScript;
import io.github.techstreet.dfscript.screen.CScreen;
import io.github.techstreet.dfscript.screen.widget.CItem;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.action.*;
import io.github.techstreet.dfscript.script.conditions.ScriptBranch;
import io.github.techstreet.dfscript.script.conditions.ScriptBuiltinCondition;
import io.github.techstreet.dfscript.script.conditions.ScriptConditionType;
import io.github.techstreet.dfscript.script.event.ScriptFunction;
import io.github.techstreet.dfscript.script.repetitions.ScriptBuiltinRepetition;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetitionType;

import java.util.ArrayList;

public class ScriptAddFunctionArgumentScreen extends CScreen {

    private final Script script;

    private final ScriptFunction function;
    private final int insertIndex;

    public ScriptAddFunctionArgumentScreen(Script script, ScriptFunction function, int insertIndex) {
        super(size(), size());
        int size = size();
        this.script = script;
        this.insertIndex = insertIndex;
        this.function = function;

        int x = 3;
        int y = 3;

        /*if (category == null) {
            for (ScriptEventType type : ScriptEventType.values()) {
                CItem item = new CItem(x, y, type.getIcon());
                item.setClickListener((btn) -> {
                    ScriptEvent event = new ScriptEvent(type);
                    script.getParts().add(insertIndex, event);
                    DFScript.MC.setScreen(new ScriptEditScreen(script));
                });
                widgets.add(item);
                x += 10;
                if (x >= size - 10) {
                    x = 3;
                    y += 10;
                }
            }
        }*/

        for (ScriptActionArgument.ScriptActionArgumentType type : ScriptActionArgument.ScriptActionArgumentType.values()) {
            CItem item = new CItem(x, y, type.icon());
            item.setClickListener((btn) -> {
                ScriptActionArgument arg = new ScriptActionArgument(function.argList().getUnnamedArgument(), type);
                function.argList().add(insertIndex, arg);
                DFScript.MC.setScreen(new ScriptEditFunctionScreen(function, script));
            });
            widgets.add(item);
            x += 10;
            if (x >= size-10) {
                x = 3;
                y += 10;
            }
        }
    }

    private static int size() {
        int amount = ScriptActionArgument.ScriptActionArgumentType.values().length;
        return (int) (Math.ceil(Math.sqrt(amount))*10)+4;
    }

    @Override
    public void close() {
        DFScript.MC.setScreen(new ScriptEditFunctionScreen(function, script));
    }
}
