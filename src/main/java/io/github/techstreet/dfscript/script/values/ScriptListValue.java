package io.github.techstreet.dfscript.script.values;

import java.util.ArrayList;
import java.util.List;

public class ScriptListValue extends ScriptValue {

    private final List<ScriptValue> value;

    public ScriptListValue(List<ScriptValue> value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "List";
    }

    @Override
    public List<ScriptValue> asList() {
        return new ArrayList<>(value);
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        if (!(other instanceof ScriptListValue)
            && !(other instanceof ScriptUnknownValue)) {
            return false;
        }
        List<ScriptValue> otherList = other.asList();
        if (otherList.size() != value.size()) {
            return false;
        }
        for (int i = 0; i < value.size(); i++) {
            if (!value.get(i).valueEquals(otherList.get(i))) {
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
