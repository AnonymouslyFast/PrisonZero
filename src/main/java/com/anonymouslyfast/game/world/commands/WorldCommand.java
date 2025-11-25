package com.anonymouslyfast.game.world.commands;

import com.anonymouslyfast.Main;
import com.anonymouslyfast.game.world.generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.generators.VoidWorldGenerator;
import com.anonymouslyfast.game.world.generators.WorldGenerator;
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

        addSubcommand(new CreateWorldCommand(worldManager));
        addSubcommand(new GenerateWorldCommand(worldManager));
        addSubcommand(new TeleportCommand(worldManager));
        addSubcommand(new ImportSchematicCommand(worldManager));
        addSubcommand(new ListWorldsCommand(worldManager));
        addSubcommand(new SetSpawnPoint(worldManager));

    }



}
