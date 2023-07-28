package io.github.techstreet.dfscript.script;

import com.google.gson.JsonElement;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;
import io.github.techstreet.dfscript.script.execution.ScriptPosStackElement;
import io.github.techstreet.dfscript.script.execution.ScriptScopeVariables;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRenderSnippetElement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ScriptContainer {
    List<ScriptSnippet> snippets;

    public ScriptContainer(int snippetCount) {
        snippets = new ArrayList<>();
        for(int i = 0; i < snippetCount; i++) {
            snippets.add(new ScriptSnippet());
        }
    }

    public void runSnippet(ScriptTask task, int snippetIndex, ScriptScopeParent parent)
    {
        snippets.get(snippetIndex).run(task, parent, null);
    }

    public void runSnippet(ScriptTask task, int snippetIndex, ScriptScopeParent parent, ScriptActionContext context)
    {
        snippets.get(snippetIndex).run(task, parent, context);
    }

    public ScriptPartRenderSnippetElement createSnippet(int snippetIndex) {
        return new ScriptPartRenderSnippetElement(snippets.get(snippetIndex));
    }

    public void forEach(Consumer<ScriptSnippet> consumer) {
        for(ScriptSnippet snippet : snippets) {
            consumer.accept(snippet);
        }
    }

    public int createSnippet(int snippetIndex, CScrollPanel panel, int y, int indent, Script script) {
        return snippets.get(snippetIndex).create(panel, y, indent, script);
    }

    public void setSnippet(int snippetIndex, ScriptSnippet snippet) {
        snippets.set(snippetIndex, snippet);
    }

    public ScriptSnippet getSnippet(int snippetIndex) {
        return snippets.get(snippetIndex);
    }
}
