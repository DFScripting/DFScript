package io.github.techstreet.dfscript.config.types;

import io.github.techstreet.dfscript.config.structure.ConfigSetting;

public class IntegerSetting extends ConfigSetting<Integer> {
    public IntegerSetting() {
    }

    public IntegerSetting(String key, Integer defaultValue) {
        super(key, defaultValue);
    }
}
