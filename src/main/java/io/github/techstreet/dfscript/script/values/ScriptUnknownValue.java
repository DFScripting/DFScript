package io.github.techstreet.dfscript.script.values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ScriptUnknownValue extends ScriptValue{

    @Override
    String typeName() {
        return "Unknown";
    }

    @Override
    public ScriptValue convertTo(ScriptValue type) {
        if (type instanceof ScriptDictionaryValue) {
            HashMap<String, ScriptValue> map = new HashMap<>();
            map.put("Unknown", this);
            return new ScriptDictionaryValue(map);
        } else if (type instanceof ScriptListValue) {
            List<ScriptValue> list = List.of();
            return new ScriptListValue(list);
        } else if (type instanceof ScriptBoolValue) {
            return new ScriptBoolValue(false);
        } else {
            return super.convertTo(type);
        }
    }

    @Override
    public String asString() {
        return "Unknown";
    }

    @Override
    public HashMap<String, ScriptValue> asDictionary() {
        return new HashMap<>();
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        return Objects.equals(other.typeName(), typeName());
    }

    @Override
    public List<ScriptValue> asList() {
        return new ArrayList<>();
    }

    @Override
    public double asNumber() {
        return 0;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    public int compare(ScriptValue other) {
        return 0;
    }
}
