package io.github.techstreet.dfscript.commands;

import com.mojang.brigadier.CommandDispatcher;
import io.github.techstreet.dfscript.commands.misc.*;
import io.github.techstreet.dfscript.loader.Loadable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.techstreet.dfscript.util.hypercube.HypercubeRank;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class CommandManager implements Loadable {

    private final CommandManager instance;
    private static final List<Command> commands = new ArrayList<>();
    public static final HashMap<Command, HypercubeRank> rankedCommands = new HashMap<>();

    public CommandManager() {
        instance = this;
    }

    public CommandManager getInstance() {
        return instance;
    }

    @Override
    public void load() {
    	
    	// misc commands
        commands.add(new PingCommand());
        commands.add(new ScriptsCommand());

        // Example of registering commands with a required df rank
        // rankedCommands.put(new TestCommand(), DFRank.JRHELPER);
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> attachTo(dispatcher));
    }

    private void attachTo(CommandDispatcher<FabricClientCommandSource> cd) {
        for (Command command : commands) {
            command.register(cd);
        }
    }

    public static List<Command> getCommands() { return commands; }
}
