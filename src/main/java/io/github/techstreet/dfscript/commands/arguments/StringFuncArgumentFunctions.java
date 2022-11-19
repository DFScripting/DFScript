package io.github.techstreet.dfscript.commands.arguments;

import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public enum StringFuncArgumentFunctions {
    SCRIPTS((v) -> {
        List<String> possible = new ArrayList<>();
        for (Script s : ScriptManager.getInstance().getScripts()) {
            possible.add(s.getName().replaceAll(" ", "_"));
        }
        return possible;
    });

    StringFuncArgumentFunctions(Function<Void, List<String>> func) {
        this.func = func;
    }

    public Function<Void, List<String>> getFunction() {
        return func;
    }

    Function<Void, List<String>> func;
}
