package io.github.techstreet.dfscript.script.action;

import io.github.techstreet.dfscript.script.Script;
import io.github.techstreet.dfscript.script.ScriptParametrizedPart;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptTask;
import io.github.techstreet.dfscript.script.render.ScriptPartRender;

import java.util.List;

public abstract class ScriptAction extends ScriptParametrizedPart {

    public ScriptAction(List<ScriptArgument> arguments) {
        super(arguments);
    }

    @Override
    public void run(ScriptTask task) {

    }

    @Override
    public boolean isDeprecated() {
        return false;
    }

    @Override
    public void create(ScriptPartRender render, Script script) {
    }
}