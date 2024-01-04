package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.Script;

public class ScriptContext {

    private final Script script;
    private int isForcedToEnd = 0;

    public ScriptContext(Script script) {
        this.script = script;
    }

    public void forceEndScope(int times) {
        isForcedToEnd += times;
    }

    public void forceEndScope() {
        isForcedToEnd++;
    }

    public void stopEndScope() {
        isForcedToEnd = 0;
    }

    public boolean isForcedToEndScope() {
        return isForcedToEnd > 0;
    }

    private int breakLoop = 0;

    public void breakLoop(int n) {
        breakLoop += n;
    }

    public void breakLoop() {
        breakLoop(1);
    }

    public void stopBreakLoop() {
        breakLoop = 0;
    }

    public boolean isLoopBroken() {
        return breakLoop > 0;
    }

    public Script script() {
        return script;
    }

    private final ScriptVariableMap variables = new ScriptVariableMap();

    public ScriptVariableMap variables() {
        return variables;
    }
}
