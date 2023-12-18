package io.github.techstreet.dfscript.script.argument;

import io.github.techstreet.dfscript.script.ScriptPart;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.execution.ScriptVariableMap;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Function;

public enum ScriptVariableScope {
    SCRIPT(
            Text.literal("Script").setStyle(Style.EMPTY.withColor(Formatting.GRAY).withItalic(false)),
            (task) -> task.context().variables()
    ),
    TASK(
            Text.literal("Task").setStyle(Style.EMPTY.withColor(Formatting.GREEN).withItalic(false)),
            (task) -> task.variables()
    ),

    FUNCTION(
            Text.literal("Function").setStyle(Style.EMPTY.withColor(Formatting.BLUE).withItalic(false)),
            (task) -> task.stack().getFunctionElement().getVarMap()
    )
    ;


    private final Function<ScriptTask, ScriptVariableMap> varMapFunction;

    private final Text name;

    ScriptVariableScope(Text name, Function<ScriptTask, ScriptVariableMap> varMapFunction) {
        this.name = name;
        this.varMapFunction = varMapFunction;
    }

    public ScriptVariableMap getMap(ScriptTask task) {
        return varMapFunction.apply(task);
    }

    public Text getName() {
        return name;
    }
    public Text getShortName() {
        String firstChar = name.getString().substring(0,1);
        Text shortName = Text.literal(firstChar).setStyle(name.getStyle());

        return shortName;
    }
}
