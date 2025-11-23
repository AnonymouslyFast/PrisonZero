package com.anonymouslyfast.game.world.commands;

import com.anonymouslyfast.Main;
import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;
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
        Argument<String> stringArg = ArgumentType.String("argument").setSuggestionCallback(
                (sender, context, suggestion) -> {
                    suggestion.addEntry(new SuggestionEntry("importSchematic"));
                });

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
            if (!args.get(stringArg).equalsIgnoreCase("importschem")) return;
            Player player = (Player) sender;
            World world = worldManager.getWorld(player.getInstance().getUuid());
            String fileName = args.get(schemFileArg);
            File schemFile = new File(Main.BASE_SERVER_PATH + "Schematics/" + fileName);
            if (!schemFile.exists()) {
                sender.sendMessage("Schematic not found!");
                return;
            }

            try {
                Schematic schem = SchematicLoader.load(schemFile.getPath());
                world.pasteSchematic(schem, player.getPosition().asVec());
                System.out.println("schematic pasting");
            } catch (ParsingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }, stringArg, schemFileArg);
    }


}
