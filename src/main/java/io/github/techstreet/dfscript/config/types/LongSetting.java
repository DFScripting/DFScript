package io.github.techstreet.dfscript.config.types;

import io.github.techstreet.dfscript.config.structure.ConfigSetting;

public class LongSetting extends ConfigSetting<Long> {
    public LongSetting() {
    }

    public LongSetting(String key, Long defaultValue) {
        super(key, defaultValue);
    }
}
