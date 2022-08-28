package io.github.techstreet.dfscript.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.techstreet.dfscript.commands.misc.ScriptsCommand;
import io.github.techstreet.dfscript.loader.Loadable;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;

public class CommandManager implements Loadable {

    private final CommandManager instance;
    private static final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        instance = this;
    }

    public CommandManager getInstance() {
        return instance;
    }

    @Override
    public void load() {
        commands.add(new ScriptsCommand());

        attachTo(ClientCommandManager.DISPATCHER);
    }

    private void attachTo(CommandDispatcher<FabricClientCommandSource> cd) {
        for (Command command : commands) {
            command.register(cd);
        }
    }

    public static List<Command> getCommands() { return commands; }
}
