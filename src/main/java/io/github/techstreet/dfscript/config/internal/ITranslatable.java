package io.github.techstreet.dfscript.config.internal;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public interface ITranslatable {
    default MutableText getTranslation(String key) {
        return ITranslatable.get(key);
    }

    static MutableText get(String key) {
        return Text.translatable(key);
    }
}
