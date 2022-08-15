package io.github.techstreet.dfscript.config.types;

import io.github.techstreet.dfscript.config.structure.ConfigSetting;

public class DoubleSetting extends ConfigSetting<Double> {
    public DoubleSetting() {
    }

    public DoubleSetting(String key, Double defaultValue) {
        super(key, defaultValue);
    }
}
