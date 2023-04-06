package io.github.techstreet.dfscript.script.argument;

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
}