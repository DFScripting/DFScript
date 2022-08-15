package io.github.techstreet.dfscript.config.structure;

import io.github.techstreet.dfscript.loader.v2.IManager;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigSubGroup implements IManager<ConfigSetting<?>>, IRawTranslation<ConfigSubGroup> {
    private final List<ConfigSetting<?>> settings = new ArrayList<>();
    private boolean startExpanded = true;
    private final String name;

    private Text rawKey = null;
    private Text rawTooltip = null;

    public ConfigSubGroup(String name) {
        this.name = name;
    }

    @Override
    public ConfigSubGroup setRawKey(String key) {
        this.rawKey = Text.literal(key);
        return this;
    }

    @Override
    public Optional<Text> getRawKey() {
        return Optional.ofNullable(rawKey);
    }

    @Override
    public ConfigSubGroup setRawTooltip(String key) {
        this.rawTooltip = Text.literal(key);
        return this;
    }

    @Override
    public Optional<Text> getRawTooltip() {
        return Optional.ofNullable(rawTooltip);
    }

    public String getName() {
        return name;
    }

    public ConfigSubGroup setStartExpanded(boolean startExpanded) {
        this.startExpanded = startExpanded;
        return this;
    }

    public boolean isStartExpanded() {
        return startExpanded;
    }

    @Deprecated
    @Override
    public void initialize() {/*not needed*/}

    @Override
    public void register(ConfigSetting<?> object) {
        this.settings.add(object);
    }

    @Override
    public List<ConfigSetting<?>> getRegistered() {
        return settings;
    }
}
