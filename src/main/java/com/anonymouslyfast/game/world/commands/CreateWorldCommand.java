package com.anonymouslyfast.game.world.commands;

import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;
import com.anonymouslyfast.game.world.WorldType;
import com.anonymouslyfast.game.world.generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.generators.VoidWorldGenerator;
import com.anonymouslyfast.game.world.generators.WorldGenerator;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public class CreateWorldCommand extends Command {

    public CreateWorldCommand(WorldManager worldManager) {
        super("create");

        var worldTypeArg = ArgumentType.Enum("worldType", WorldType.class);
        var worldNameArg = ArgumentType.String("worldName");
        var worldHasLightingArg = ArgumentType.Boolean("hasDefaultLighting").setDefaultValue(true);
        var worldHasWorldSave = ArgumentType.Boolean("hasWorldSave").setDefaultValue(true);

        addSyntax((sender, context) -> {
            if (!(sender instanceof Player player)) return;

            if (worldManager.isNameTaken(context.get(worldNameArg))) {
                player.sendMessage("This name is already taken by another world! Please try again.");
                return;
            }

            // Getting the generator
            WorldGenerator generator = switch (context.get(worldTypeArg)) {
                case FLAT ->  new FlatWorldGenerator();
                default -> new VoidWorldGenerator();
            };

            // Creating & Registering World
            World world = new World(generator)
                    .setName(context.get(worldNameArg))
                    .enableSaveWorld(context.get(worldHasWorldSave))
                    .enableLighting(context.get(worldHasLightingArg))
                    .generate();

            worldManager.registerWorld(world);

            player.sendMessage("Created the world" + context.get(worldNameArg) + "!");

        }, worldTypeArg, worldNameArg, worldHasLightingArg, worldHasWorldSave);
    }
}
