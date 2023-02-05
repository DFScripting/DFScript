package io.github.techstreet.dfscript.script.action;

import io.github.techstreet.dfscript.script.argument.ScriptArgument;
import io.github.techstreet.dfscript.script.execution.ScriptActionContext;

import java.util.ArrayList;
import java.util.List;

public class ScriptActionArgumentList extends ArrayList<ScriptActionArgument> {
    public ScriptActionArgumentList(ScriptActionArgumentList current) {
        this.addAll(current);
    }

    public ScriptActionArgumentList() {

    }

    private void generatePossibilities(List<ScriptActionArgumentList> possibilities, ScriptActionArgumentList current, int pos) {
        if (pos >= size()) {
            possibilities.add(new ScriptActionArgumentList(current));
            return;
        }

        ScriptActionArgument arg = get(pos);
        if (arg.optional()) {
            generatePossibilities(possibilities, new ScriptActionArgumentList(current), pos + 1);
        }
        current.add(arg);
        generatePossibilities(possibilities, current, pos + 1);
    }

    public List<ScriptActionArgumentList> generatePossibilities() {
        List<ScriptActionArgumentList> possibilities = new ArrayList<>();

        generatePossibilities(possibilities, new ScriptActionArgumentList(), 0);

        return possibilities;
    }

    public void getArgMap(ScriptActionContext ctx) {
        List<ScriptActionArgumentList> possibilities = generatePossibilities();

        search:
        for (List<ScriptActionArgument> possibility : possibilities) {
            int pos = 0;
            ctx.argMap().clear();
            for (ScriptActionArgument arg : possibility) {
                List<ScriptArgument> args = new ArrayList<>();
                if (pos >= ctx.arguments().size()) {
                    continue search;
                }
                if (ctx.arguments().get(pos).convertableTo(arg.type())) {
                    args.add(ctx.arguments().get(pos));
                    pos++;
                }
                if (arg.plural()) {
                    while (pos < ctx.arguments().size()) {
                        if (ctx.arguments().get(pos).convertableTo(arg.type())) {
                            args.add(ctx.arguments().get(pos));
                            pos++;
                        } else {
                            break;
                        }
                    }
                }
                ctx.setArg(arg.name(), args);
            }
            if (pos == ctx.arguments().size()) {
                return;
            }
        }
        ctx.argMap().clear();
        throw new IllegalArgumentException();
    }
}
