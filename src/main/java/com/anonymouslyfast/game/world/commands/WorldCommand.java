package com.anonymouslyfast.game.world.commands;

import com.anonymouslyfast.Main;
import com.anonymouslyfast.game.world.Generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.Generators.VoidWorldGenerator;
import com.anonymouslyfast.game.world.Generators.WorldGenerator;
import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;
import com.anonymouslyfast.game.world.WorldType;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.sandrohc.schematic4j.SchematicLoader;
import net.sandrohc.schematic4j.exception.ParsingException;
import net.sandrohc.schematic4j.schematic.Schematic;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WorldCommand extends Command {

    public WorldCommand(WorldManager worldManager) {
        super("world");

        // Creates the arguments
        var stringArg = ArgumentType.String("argument").setSuggestionCallback(
                (sender, context, suggestion) -> {
                    suggestion.addEntry(new SuggestionEntry("create"));
                    suggestion.addEntry(new SuggestionEntry("generate"));
                    suggestion.addEntry(new SuggestionEntry("delete"));
                    suggestion.addEntry(new SuggestionEntry("list"));
                    suggestion.addEntry(new SuggestionEntry("teleport"));
                    suggestion.addEntry(new SuggestionEntry("importSchematic"));
                });

        makeCreateWorldArgument(worldManager, stringArg);
        makeGenerateWorldArgument(worldManager, stringArg);
        makeTeleportArgument(worldManager, stringArg);
        makeImportSchematicArgument(worldManager, stringArg);

    }

    void makeGenerateWorldArgument(WorldManager worldManager, Argument<String> stringArg) {
        var worldArgument = ArgumentType.String("world").setSuggestionCallback(
                (sender, context, suggestion) -> {
                    for (String name : worldManager.getAllWorldNames()) {
                        suggestion.addEntry(new SuggestionEntry(name));
                    }
                }
        );

        addSyntax((sender, context) -> {
            if (!context.get(stringArg).equals("generate")) return;
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

            worldManager.unregisterWorld(world);
            world.generate();
            worldManager.registerWorld(world);

            player.sendMessage(context.get(worldArgument) + " has been generated!");

        }, stringArg, worldArgument);
    }

    void makeTeleportArgument(WorldManager worldManager, Argument<String> argument) {

        var worldArgument = ArgumentType.String("world").setSuggestionCallback(
                (sender, context, suggestion) -> {
                    for (String name : worldManager.getAllWorldNames()) {
                        suggestion.addEntry(new SuggestionEntry(name));
                    }
                }
        );

        addSyntax((sender, context) -> {
            if (!context.get(argument).equals("teleport")) return;
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

            if (world.getInstanceContainer() == null) {
                player.sendMessage("This world is not generated! Please use the generate command, and try again.");
                return;
            }

            player.setInstance(world.getInstanceContainer());
            player.teleport(world.getWorldGenerator().getDefaultSpawnPosition());
            player.sendMessage("Teleported you to " + world.getWorldName() + "!");

        }, argument, worldArgument);
    }

    void makeCreateWorldArgument(WorldManager worldManager, Argument<String> stringArg) {

        var worldTypeArg = ArgumentType.Enum("worldType", WorldType.class);
        var worldNameArg = ArgumentType.String("worldName");
        var worldHasLightingArg = ArgumentType.Boolean("hasDefaultLighting").setDefaultValue(true);
        var worldHasWorldSave = ArgumentType.Boolean("hasWorldSave").setDefaultValue(true);



        addSyntax((sender, context) -> {
           if (!context.get(stringArg).equalsIgnoreCase("create")) return;
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

        }, stringArg, worldTypeArg, worldNameArg, worldHasLightingArg, worldHasWorldSave);
    }

    void makeImportSchematicArgument(WorldManager worldManager, Argument<String> stringArg) {
        Path schemFolder = Paths.get(Main.BASE_SERVER_PATH + "Schematics/");
        var schemFileArg = ArgumentType.String("schemFile").setSuggestionCallback(
                (sender, context, suggestion) -> {
                    try {
                        DirectoryStream<Path> schemFiles = Files.newDirectoryStream(schemFolder);
                        for (Path path : schemFiles) {
                            suggestion.addEntry(new SuggestionEntry(path.getFileName().toString()));
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        addSyntax((sender, args) -> {
            if (!args.get(stringArg).equalsIgnoreCase("importSchematic")) return;
            Player player = (Player) sender;
            World world = worldManager.getWorld(player.getInstance().getUuid());
            if (world == null) {
                player.sendMessage("UUID: " + player.getInstance().getUuid());
                player.sendMessage("saved UUID: " + worldManager.getAllWorlds()[0].getInstanceContainer().getUuid());
            }
            String fileName = args.get(schemFileArg);
            File schemFile = new File(Main.BASE_SERVER_PATH + "Schematics/" + fileName);
            if (!schemFile.exists()) {
                player.sendMessage("Schematic not found!");
                return;
            }

            try {
                Schematic schem = SchematicLoader.load(schemFile.getPath());
                world.pasteSchematic(schem, player.getPosition().asVec());
                player.sendMessage("Schematic pasting!");
            } catch (ParsingException e) {
                e.printStackTrace();
                player.sendMessage("Schematic failed!");
            } catch (IOException e) {
                e.printStackTrace();
                player.sendMessage("Schematic failed!");
            }

        }, stringArg, schemFileArg);
    }


}
