package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

    public void breakLoop(int n)
    {
        breakLoop += n;
    }

    public void breakLoop()
    {
        breakLoop(1);
    }

    public void stopBreakLoop()
    {
        breakLoop = 0;
    }

    public boolean isLoopBroken()
    {
        return breakLoop > 0;
    }

    private final HashMap<String,ScriptValue> variables = new HashMap<>();

    public ScriptValue getVariable(String name) {
        if (!variables.containsKey(name)) {
            return new ScriptUnknownValue();
        }
        return variables.get(name);
    }

    public void setVariable(String name, ScriptValue value) {
        variables.put(name, value);
    }

    public List<Entry<String, ScriptValue>> listVariables(String filter) {
        return variables.entrySet().stream().filter(entry -> entry.getKey().contains(filter)).collect(Collectors.toList());
    }

    public int getVariableCount() {
        return variables.size();
    }

    public Script script() {
        return script;
    }
}
