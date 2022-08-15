package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.Script;

public class ScriptTask {

    private final ScriptPosStack stack;
    private final Event event;
    private boolean running;
    private final Script script;

    public ScriptTask(ScriptPosStack stack, Event event, Script script) {
        this.stack = stack;
        this.event = event;
        this.script = script;
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void run() {
        running = true;
        script.execute(this);
    }

    public ScriptPosStack stack() {
        return stack;
    }

    public Event event() {
        return event;
    }

    public boolean isRunning() {
        return running;
    }
    public void schedule(int posCopy, ScriptScopeVariables scriptScopeVariables) {
        stack.push(posCopy, scriptScopeVariables);

        if(stack.peekElement().hasCondition() && !stack.peekElement().checkCondition()) {
            stack.pop();
            return;
        }
    }
}
