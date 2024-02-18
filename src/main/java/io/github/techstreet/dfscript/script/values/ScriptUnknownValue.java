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
