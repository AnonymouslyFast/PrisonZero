package com.anonymouslyfast.game.world.commands;

import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public class GenerateWorldCommand extends Command {

    public GenerateWorldCommand(WorldManager worldManager) {
        super("generate");

        var worldArgument = ArgumentType.String("world").setSuggestionCallback(
                (sender, context, suggestion) -> {
                    for (String name : worldManager.getAllWorldNames()) {
                        suggestion.addEntry(new SuggestionEntry(name));
                    }
                }
        );

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            if (!worldManager.isNameTaken(context.get(worldArgument))) {
                player.sendMessage("This world does not exist! Please try again. (Name is not recognized)");
                return;
            }

            World world = worldManager.getWorld(context.get(worldArgument));
            if (world == null) {
                player.sendMessage("That world does not exist! Please try again. (World is Null!)");
                return;
            }

            if (world.getInstanceContainer() != null) {
                player.sendMessage("This world is already generated!");
                return;
            }

            worldManager.unregisterWorld(worldManager.getSavedWorldUUID(world));
            World newWorld = world.generate();
            worldManager.registerWorld(newWorld);

            player.sendMessage(context.get(worldArgument) + " has been generated!");

        }, worldArgument);
    }

}
