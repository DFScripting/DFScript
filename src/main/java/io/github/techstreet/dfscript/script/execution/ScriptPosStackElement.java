package io.github.techstreet.dfscript.script.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ScriptPosStackElement {
    private int pos;
    private int originalPos;
    private Runnable preTask = null;
    private Consumer<ScriptActionContext> condition = null;

    private ScriptActionContext defaultCtx = null;

    private Map<String, Object> scopeVariables = new HashMap<>();

    public ScriptPosStackElement(int initial) {
        originalPos = initial;
        setPos(originalPos);
    }
    public ScriptPosStackElement(int initial, Runnable preTask) {
        originalPos = initial;
        setPos(originalPos);
        this.preTask = preTask;
    }
    public ScriptPosStackElement(int initial, Runnable preTask, Consumer<ScriptActionContext> condition) {
        originalPos = initial;
        setPos(originalPos);
        this.preTask = preTask;
        this.condition = condition;
    }

    public ScriptPosStackElement(int initial, ScriptScopeVariables variables) {
        originalPos = initial;
        setPos(originalPos);

        if(variables == null)
        {
            this.preTask = null;
            this.condition = null;
            this.defaultCtx = null;
            return;
        }

        this.preTask = variables.preTask;
        this.condition = variables.condition;
        this.defaultCtx = variables.ctx;
    }
    public ScriptPosStackElement setPos(int pos) {
        this.pos = pos;
        return this;
    }
    public int getPos() {
        return pos;
    }
    public int getOriginalPos() {
        return originalPos;
    }
    public void runPreTask() {
        if (preTask != null) {
            preTask.run();
        }
    }
    public boolean checkCondition() {
        if(condition == null)
        {
            return false;
        }

        defaultCtx.setLastIfResult(false);

        condition.accept(defaultCtx);
        return defaultCtx.lastIfResult();
    }
    public boolean hasCondition() {
        return condition != null;
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
}