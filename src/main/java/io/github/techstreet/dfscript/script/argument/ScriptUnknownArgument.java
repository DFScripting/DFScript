package io.github.techstreet.dfscript.script.argument;

import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.execution.ScriptContext;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;

public record ScriptUnknownArgument() implements ScriptArgument {
    @Override
    public ScriptValue getValue(ScriptTask task) {
        return new ScriptUnknownValue();
    }

    @Override
    public boolean convertableTo(ScriptActionArgument.ScriptActionArgumentType type) {
        return false;
    }
}
