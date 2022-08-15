package io.github.techstreet.dfscript.script.values;

public class ScriptTextValue extends ScriptValue {

    private final String value;

    public ScriptTextValue(String value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "Text";
    }

    @Override
    public String asText() {
        return value;
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        return other.asText().equals(value);
    }
}
