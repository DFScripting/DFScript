package io.github.techstreet.dfscript.config.structure;


import net.minecraft.text.Text;

import java.util.Optional;

public interface IRawTranslation<T> extends IRawKey<T> {
    default T setRawKey(String key) {
        return null;
    }

    default Optional<Text> getRawKey() {
        return Optional.empty();
    }

    default T setRawTooltip(String key) {
        return null;
    }

    default Optional<Text> getRawTooltip() {
        return Optional.empty();
    }
}
