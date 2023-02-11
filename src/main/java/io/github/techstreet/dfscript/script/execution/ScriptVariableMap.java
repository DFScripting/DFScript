package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.values.ScriptUnknownValue;
import io.github.techstreet.dfscript.script.values.ScriptValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScriptVariableMap {
    private final HashMap<String, ScriptValue> variables = new HashMap<>();

    public ScriptValue get(String name) {
        if (!variables.containsKey(name)) {
            return new ScriptUnknownValue();
        }
        return variables.get(name);
    }

    public void set(String name, ScriptValue value) {
        variables.put(name, value);
    }

    public List<Map.Entry<String, ScriptValue>> list(String filter) {
        return variables.entrySet().stream().filter(entry -> entry.getKey().contains(filter)).collect(Collectors.toList());
    }

    public int count() {
        return variables.size();
    }

    public boolean has(String name) {
        return variables.containsKey(name);
    }
}
