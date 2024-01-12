package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.event.system.Event;
import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.util.chat.ChatUtil;

public class ScriptTask {

    private final ScriptPosStack stack;
    private final Event event;
    private boolean running;
    private final Script script;
    private final ScriptContext context;

    public ScriptTask(ScriptPosStack stack, Event event, Script script) {
        this.stack = stack;
        this.event = event;
        this.script = script;
        this.context = script.getContext();
        running = true;
    }

    public ScriptTask(Event event, Script script) {
        this(new ScriptPosStack(), event, script);
    }

    public ScriptContext context() {
        return context;
    }

    public void stop() {
        running = false;
    }

    public void run() {
        if (script.disabled()) { // don't run the code if it's disabled obviously
            return;
        }

        running = true;
        while(true) {
            if(!running) break;
            if(stack.size() <= 0) break;

            if(stack.peek().executeOnce(this)) {
                stack.pop();
            }
        }
        //script.execute(this);
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

    private final ScriptVariableMap variables = new ScriptVariableMap();
    public ScriptVariableMap variables() {
        return variables;
    }
}
