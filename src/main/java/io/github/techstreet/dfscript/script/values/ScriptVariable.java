package io.github.techstreet.dfscript.script.values;

import java.util.HashMap;
import java.util.List;

public class ScriptVariable extends ScriptValue {
    private ScriptValue value;

    public ScriptVariable() {
        this.value = new ScriptUnknownValue();
    }

    public ScriptVariable(ScriptValue value) {
        this.value = value;
    }

    @Override
    public ScriptValue get() {
        return value;
    }

    public void set(ScriptValue value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return get().asString();
    }

    @Override
    public double asNumber() {
        return get().asNumber();
    }

    @Override
    public List<ScriptValue> asList() {
        return get().asList();
    }

    @Override
    public HashMap<String,ScriptValue> asDictionary() {
        return get().asDictionary();
    }

    @Override
    public boolean asBoolean() {
        return get().asBoolean();
    }

    @Override
    String typeName() {
        return "Variable";
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        return get().valueEquals(other);
    }
}
