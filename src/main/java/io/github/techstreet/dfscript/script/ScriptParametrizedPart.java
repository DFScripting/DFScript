package io.github.techstreet.dfscript.script;

import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.argument.ScriptConfigArgument;
import io.github.techstreet.dfscript.script.argument.ScriptFunctionArgument;
import io.github.techstreet.dfscript.script.event.ScriptHeader;

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

    public void updateScriptReferences(Script script, ScriptHeader header) {
        for (ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                carg.setScript(script);
            }
            if (arg instanceof ScriptFunctionArgument farg) {
                farg.setHeader(header);
            }
        }
    }

    public void updateConfigArguments(String oldOption, String newOption) {
        for (ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptConfigArgument carg) {
                if (carg.getName() == oldOption) {
                    carg.setOption(newOption);
                }
            }
        }
    }

    public void removeConfigArguments(String option) {
        int index = 0;

        List<ScriptArgument> argList = getArguments();

        while (index < argList.size()) {
            if (argList.get(index) instanceof ScriptConfigArgument carg) {
                if (Objects.equals(carg.getName(), option)) {
                    argList.remove(index);
                    continue;
                }
            }
            index++;
        }
    }

    public void replaceFunctionArgument(String oldArg, String newArg) {
        for (ScriptArgument arg : getArguments()) {
            if (arg instanceof ScriptFunctionArgument carg) {
                if (carg.getName() == oldArg) {
                    carg.setFunctionArg(newArg);
                }
            }
        }
    }

    public void removeFunctionArgument(String arg) {
        int index = 0;

        List<ScriptArgument> argList = getArguments();

        while (index < argList.size()) {
            if (argList.get(index) instanceof ScriptFunctionArgument carg) {
                if (Objects.equals(carg.getName(), arg)) {
                    argList.remove(index);
                    continue;
                }
            }
            index++;
        }
    }
}
