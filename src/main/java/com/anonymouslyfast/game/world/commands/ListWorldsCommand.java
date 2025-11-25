package com.anonymouslyfast.game.world.commands;

import com.anonymouslyfast.game.world.WorldManager;
import net.minestom.server.command.builder.Command;

public class ListWorldsCommand extends Command {

    public ListWorldsCommand(WorldManager worldManager) {
        super("list");
        addSyntax((sender, context) -> {
            sender.sendMessage("=== PrisonZero's Worlds ===");
            for (String worldName : worldManager.getAllWorldNames()) {
                sender.sendMessage("  - " + worldName);
            }

        });
    }

}
