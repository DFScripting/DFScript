package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.script.action.ScriptActionType;

import java.util.function.Consumer;

public interface ScriptScopeParent {
    void forEach(Consumer<ScriptSnippet> consumer);
    ScriptContainer container();
}
