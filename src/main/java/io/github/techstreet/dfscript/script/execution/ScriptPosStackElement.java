package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.ScriptScopeParent;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.repetitions.ScriptRepetition;
import io.github.techstreet.dfscript.util.chat.ChatUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ScriptPosStackElement {
    private int pos = 0;
    private final ScriptSnippet snippet;
    private final ScriptScopeParent parent;

    private Map<String, Object> scopeVariables = new HashMap<>();

    public ScriptPosStackElement(ScriptSnippet snippet, ScriptScopeParent parent) {
        this.snippet = snippet;
        this.parent = parent;
        setPos(0);
        if(parent instanceof ScriptRepetition) {
            setPos(this.snippet.size());
        }
    }
    public ScriptPosStackElement setPos(int pos) {
        this.pos = pos;
        return this;
    }
    public int getPos() {
        return pos;
    }

    public ScriptScopeParent getParent() {
        return parent;
    }

    public boolean executeOnce(ScriptTask task) {
        if(pos >= snippet.size()) {
            if (!(parent instanceof ScriptRepetition r)) {
                return true;
            }
            if(!hasVariable("Lagslayer Count")) {
                setVariable("Lagslayer Count", 0);
            }

            setVariable("Lagslayer Count", (Integer)getVariable("Lagslayer Count")+1);

            if((Integer)getVariable("Lagslayer Count") > 100000) {
                ChatUtil.error("Lagslayer triggered in script: " + task.context().script().getName());
                task.stop();
                return true;
            }

            if (!r.checkCondition(task)) {
                return true;
            }
            pos = 0;
        }

        snippet.get(pos).run(task);

        pos++;
        return false;
    }

    public Object getVariable(String name) {
        return scopeVariables.get(name);
    }

    public void setVariable(String name, Object value) {
        scopeVariables.put(name, value);
    }

    public boolean hasVariable(String name) {
        return scopeVariables.containsKey(name);
    }

    public void skip() {
        setPos(snippet.size());
    }
}