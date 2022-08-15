package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptVariableArgument;
import io.github.techstreet.dfscript.script.values.ScriptValue;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public record ScriptActionContext(ScriptContext context, List<ScriptArgument> arguments, Event event, Consumer<ScriptScopeVariables> inner, ScriptTask task, HashMap<String, List<ScriptArgument>> argMap, Script script) {

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
        return arg(name).getValue(event,context);
    }

    public List<ScriptValue> pluralValue(String name) {
        return pluralArg(name).stream().map(arg -> arg.getValue(event,context)).collect(Collectors.toList());
    }

    public ScriptVariableArgument variable(String name) {
        return (ScriptVariableArgument) arg(name);
    }

    public void scheduleInner(Runnable runnable) {
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
    }

    public void setScopeVariable(String name, Object object) {
        task().stack().peekElement(0).setVariable(name, object);
    }

    public Object getScopeVariable(String name) {
        return task().stack().peekElement(0).getVariable(name);
    }

    public boolean hasScopeVariable(String name) {
        return task().stack().peekElement(0).hasVariable(name);
    }
}
