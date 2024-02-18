package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import io.github.techstreet.dfscript.script.values.ScriptVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScriptVariableMap {
    private final HashMap<String, ScriptVariable> variables = new HashMap<>();

    public ScriptValue get(String name) {
        if (!has(name)) {
            return new ScriptUnknownValue();
        }
        return variables.get(name).get();
    }

    public ScriptVariable getReference(String name) {
        if(has(name))
        {
            return variables.get(name);
        }
        ScriptVariable newVar = new ScriptVariable();
        variables.put(name, newVar);
        return newVar;
    }

    public void set(String name, ScriptValue value) {
        if(has(name))
        {
            variables.get(name).set(value);
            return;
        }
        variables.put(name, new ScriptVariable(value));
    }

    public List<Map.Entry<String, ScriptVariable>> list(String filter) {
        return
                variables.entrySet().stream().filter(entry -> entry.getKey().contains(filter)).collect(Collectors.toList());
    }

    public int count() {
        return variables.size();
    }

    public boolean has(String name) {
        return variables.containsKey(name);
    }
}
