package io.github.techstreet.dfscript.config.types;

import io.github.techstreet.dfscript.config.structure.ConfigSetting;

public class FloatSetting extends ConfigSetting<Float> {
    public FloatSetting() {
    }

    public FloatSetting(String key, Float defaultValue) {
        super(key, defaultValue);
    }
}
