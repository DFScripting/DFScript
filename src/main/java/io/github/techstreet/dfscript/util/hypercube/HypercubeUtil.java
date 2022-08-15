package io.github.techstreet.dfscript.util.hypercube;

import io.github.techstreet.dfscript.commands.Command;
import io.github.techstreet.dfscript.commands.CommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HypercubeUtil {
    private static HypercubeRank rank = HypercubeRank.DEFAULT;
    private static final List<Command> regCommands = new ArrayList<>();

    public static HypercubeRank getRank() {
        return rank;
    }

    public static void setRank(HypercubeRank rank) {
        if (!(HypercubeUtil.rank.ordinal() >= rank.ordinal())) {
            HypercubeUtil.rank = rank;

            for (Command command : CommandManager.rankedCommands.keySet()) {
                HypercubeRank r = CommandManager.rankedCommands.get(command);

                if (rank.hasPermission(r)) {
                    if (!regCommands.contains(command)) {
                        regCommands.add(command);
                        command.register(ClientCommandManager.getActiveDispatcher());
                    }
                }
            }
        }
    }

    public static final Identifier channel = new Identifier("hypercube", "codeutilities");
}
