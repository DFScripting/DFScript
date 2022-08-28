package io.github.techstreet.dfscript.script.values;

public class ScriptNumberValue extends ScriptValue {

    private final double value;

    public ScriptNumberValue(double value) {
        this.value = value;
    }

    @Override
    String typeName() {
        return "Number";
    }

    @Override
    public double asNumber() {
        return value;
    }

    @Override
    public boolean valueEquals(ScriptValue other) {
        if (!(other instanceof ScriptNumberValue)
            && !(other instanceof ScriptUnknownValue)) {
            return false;
        }
        return value == other.asNumber();
    }

    @Override
    public String asText() {
        if (value % 1 == 0) {
            return String.valueOf((int) value);
        }
        return String.valueOf(value);
    }

    @Override
    public int compare(ScriptValue other) {
        if(other instanceof ScriptNumberValue) {
            if(asNumber() == other.asNumber()) {
                return 0;
            }

            if(asNumber() > other.asNumber()) {
                return 1;
            }

            return -1;
        }
        return asText().compareTo(other.asText());
    }
}
