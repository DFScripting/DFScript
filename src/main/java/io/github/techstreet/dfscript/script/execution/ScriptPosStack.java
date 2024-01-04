package io.github.techstreet.dfscript.script.execution;

import io.github.techstreet.dfscript.script.ScriptScopeParent;
import io.github.techstreet.dfscript.script.ScriptSnippet;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

import java.util.ArrayList;
import java.util.List;

public class ScriptPosStack {

    private final List<ScriptPosStackElement> data = new ArrayList<>();

    public ScriptPosStack() {

    }

    public void push(ScriptSnippet snippet, ScriptScopeParent parent, ScriptActionContext context) {
        data.add(new ScriptPosStackElement(snippet, parent, context));
    }

    public void pop() {
        if (isEmpty()) {
            return;
        }
        ScriptPosStackElement element = data.remove(data.size() - 1);
    }

    public ScriptPosStackElement peek() {
        return peek(0);
    }

    public ScriptPosStackElement peek(int n) {
        if (!(data.size() - 1 - n >= 0)) {
            return null;
        }
        return data.get(data.size() - 1 - n);
    }

    public int size() {
        return data.size();
    }

    public ScriptPosStackElement getFunctionElement() {
        for (int i = 0; i < data.size(); i++) {
            ScriptPosStackElement element = peek(i);
            if (element.getParent() instanceof ScriptHeader) {
                return element;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void clear() {
        data.clear();
    }

    public void increase() {
        ScriptPosStackElement element = peek();
        data.set(data.size() - 1, element.setPos(element.getPos() + 1));
    }
}
