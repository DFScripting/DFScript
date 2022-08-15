package io.github.techstreet.dfscript.script.values;

import java.util.HashMap;
import java.util.List;

public abstract class ScriptValue {

    abstract String typeName();

    public String asText() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to text");
    }

    public double asNumber() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to number");
    }

    public List<ScriptValue> asList() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to list");
    }

    public HashMap<String,ScriptValue> asDictionary() {
        throw new UnsupportedOperationException("Cannot convert " + typeName() + " to directory");
    }

    @Override
    public String toString() {
        return asText();
    }

    public abstract boolean valueEquals(ScriptValue other);
}
