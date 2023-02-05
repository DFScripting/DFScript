package io.github.techstreet.dfscript.script;

import com.google.gson.*;
import io.github.techstreet.dfscript.screen.widget.CScrollPanel;
import io.github.techstreet.dfscript.script.action.ScriptActionType;
import io.github.techstreet.dfscript.script.action.ScriptBuiltinAction;
import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptConfigArgument;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ScriptParametrizedPart extends ScriptPart implements ScriptRunnable {

    List<ScriptArgument> arguments;

    public ScriptParametrizedPart(List<ScriptArgument> arguments) {
        this.arguments = arguments;
    }
    //ScriptGroup getGroup();

    public List<ScriptArgument> getArguments() {
        return arguments;
    }

    public void updateScriptReferences(Script script) {
        for(ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                carg.setScript(script);
            }
        }
    }

    public void updateConfigArguments(String oldOption, String newOption) {
        for(ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                if(carg.getName() == oldOption)
                {
                    carg.setOption(newOption);
                }
            }
        }
    }

    public void removeConfigArguments(String option) {
        int index = 0;

        List<ScriptArgument> argList = getArguments();

        while(index < argList.size()) {
            if (argList.get(index) instanceof ScriptConfigArgument carg) {
                if(Objects.equals(carg.getName(), option))
                {
                    argList.remove(index);
                    continue;
                }
            }
            index++;
        }
    }
}
