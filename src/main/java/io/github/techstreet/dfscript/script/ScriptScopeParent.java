package io.github.techstreet.dfscript.script;

import java.util.function.Consumer;

public interface ScriptScopeParent {
    void forEach(Consumer<ScriptSnippet> consumer);

    ScriptContainer container();
}
