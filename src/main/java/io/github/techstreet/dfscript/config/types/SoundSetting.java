package io.github.techstreet.dfscript.config.types;

import io.github.techstreet.dfscript.config.ConfigSounds;

public class SoundSetting extends DropdownSetting<ConfigSounds> {

    public SoundSetting(String key) {
        super(key, DropdownSetting.fromEnum(ConfigSounds.NONE));
    }
}
