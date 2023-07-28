package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.action.ScriptActionArgument;
import io.github.techstreet.dfscript.script.action.ScriptActionArgumentList;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptVariableArgument;
import io.github.techstreet.dfscript.script.values.ScriptValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ScriptActionContext {
    private final ScriptTask task;
    private final List<ScriptArgument> arguments;
    private final HashMap<String, List<ScriptArgument>> argMap;

    private final HashMap<String, ScriptActionArgument> actionArgMap;

    public ScriptActionContext(ScriptTask task, List<ScriptArgument> arguments) {
        this.task = task;
        this.arguments = arguments;
        this.argMap = new HashMap<>();
        this.actionArgMap = new HashMap<>();
    }

    public void setArg(String name, List<ScriptArgument> args) {
        argMap.put(name, args);
    }

    public List<ScriptArgument> pluralArg(String messages) {
        return argMap.get(messages);
    }

    public ScriptArgument arg(String name) {
        return argMap.get(name).get(0);
    }

    public ScriptValue value(String name) {
        return arg(name).getValue(task);
    }

    public List<ScriptValue> pluralValue(String name) {
        return pluralArg(name).stream().map(arg -> arg.getValue(task)).collect(Collectors.toList());
    }

    public ScriptVariableArgument variable(String name) {
        return (ScriptVariableArgument) arg(name);
    }

    public void setVariable(String name, ScriptValue value) {
        variable(name).setValue(value, task());
    }

    /*public void scheduleInner(Runnable runnable) {
        inner.accept(new ScriptScopeVariables(runnable, null, this));
    }

    public void scheduleInner(Runnable runnable, Consumer<ScriptActionContext> condition) {
        inner.accept(new ScriptScopeVariables(runnable, condition, this));
    }
    public void scheduleInner() {
        inner.accept(new ScriptScopeVariables(null, null, this));
    }

    public boolean lastIfResult(int n) {
        return task().stack().peekElement(n).getVariable("lastIfResult").equals(true);
    }

    public boolean lastIfResult() {
        return lastIfResult(0);
    }

    public void setLastIfResult(boolean a, int n) {
        task().stack().peekElement(n).setVariable("lastIfResult", a);
    }
    public void setLastIfResult(boolean a) {
        setLastIfResult(a, 0);
    }*/

    public void setScopeVariable(String name, Object object) {
        task().stack().peek(0).setVariable(name, object);
    }

    public Object getScopeVariable(String name) {
        return task().stack().peek(0).getVariable(name);
    }

    public boolean hasScopeVariable(String name) {
        return task().stack().peek(0).hasVariable(name);
    }

    public ScriptTask task() {
        return task;
    }

    public List<ScriptArgument> arguments() {
        return arguments;
    }

    public HashMap<String, List<ScriptArgument>> argMap() {
        return argMap;
    }
    public HashMap<String, ScriptActionArgument> actionArgMap() {
        return actionArgMap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScriptActionContext) obj;
        return Objects.equals(this.task, that.task) &&
                Objects.equals(this.arguments, that.arguments) &&
                Objects.equals(this.argMap, that.argMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, arguments, argMap);
    }

    @Override
    public String toString() {
        return "ScriptActionContext[" +
                "task=" + task + ", " +
                "arguments=" + arguments + ", " +
                "argMap=" + argMap + ']';
    }

}
