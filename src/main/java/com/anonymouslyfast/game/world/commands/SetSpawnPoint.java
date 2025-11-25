package com.anonymouslyfast.game.world.commands;

import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class SetSpawnPoint extends Command {

    public SetSpawnPoint(WorldManager worldManager) {
        super("setspawnpoint");

        addSyntax((sender, args) -> {
            if (!(sender instanceof Player player)) return;

            World world = worldManager.getWorld(player.getInstance().getUuid());
            if (world == null) {
                player.sendMessage("You are not in a registered world!");
                return;
            }

            world.setSpawnPoint(player.getPosition());
            player.sendMessage("This world's spawn point, has been set to your current location!");

        });
    }

}
