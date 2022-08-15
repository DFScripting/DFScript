package io.github.techstreet.dfscript.script.execution;

import java.util.function.Consumer;

public class ScriptScopeVariables {

    Runnable preTask;
    Consumer<ScriptActionContext> condition;
    ScriptActionContext ctx;

    ScriptScopeVariables(Runnable run, Consumer<ScriptActionContext> cond, ScriptActionContext ctx) {
        preTask = run;
        condition = cond;
        this.ctx = ctx;
    }
}
