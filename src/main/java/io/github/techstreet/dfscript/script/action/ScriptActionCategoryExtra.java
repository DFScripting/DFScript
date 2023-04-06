package io.github.techstreet.dfscript.script.action;

import io.github.techstreet.dfscript.script.ScriptPart;
import net.minecraft.item.ItemStack;

import java.util.function.Function;
import java.util.function.Supplier;

public record ScriptActionCategoryExtra(ItemStack icon, Supplier<ScriptPart> createPartFunction) {
    public ItemStack getIcon() {
        return icon;
    }

    public ScriptPart getPart() {
        return createPartFunction.get();
    }
}
