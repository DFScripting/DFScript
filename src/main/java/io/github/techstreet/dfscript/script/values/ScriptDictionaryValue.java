package io.github.techstreet.dfscript.script.values;

import java.util.HashMap;

public class ScriptDictionaryValue extends ScriptValue {

    private final HashMap<String, ScriptValue> value;

    public ScriptDictionaryValue(HashMap<String, ScriptValue> value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "Dictionary";
    }

    @Override
    public HashMap<String, ScriptValue> asDictionary() {
        return new HashMap<>(value);
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        if (!(other instanceof ScriptDictionaryValue)
            && !(other instanceof ScriptUnknownValue)) {
            return false;
        }
        HashMap<String, ScriptValue> otherValue = other.asDictionary();
        if (otherValue.size() != value.size()) {
            return false;
        }
        for (String key : value.keySet()) {
            if (!otherValue.containsKey(key)) {
                return false;
            }
            if (!value.get(key).valueEquals(otherValue.get(key))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String asText() {
        return value.toString();
    }
}
