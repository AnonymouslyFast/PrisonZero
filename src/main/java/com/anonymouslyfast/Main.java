package com.anonymouslyfast;

import com.anonymouslyfast.game.data.DataManager;
import com.anonymouslyfast.game.player.listeners.JoinListener;
import com.anonymouslyfast.game.world.Generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;
import com.anonymouslyfast.game.world.commands.WorldCommand;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;


public final class Main {

    public static final String BASE_SERVER_PATH = "MinecraftServer/";

    private static final String serverAddress = "0.0.0.0";
    private static final int serverPort = 25565; // Default mc port

    private static DataManager serverDataManager;
    private static WorldManager worldManager;

    static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        checkDirs();

        serverDataManager = new DataManager(BASE_SERVER_PATH + "Databases/server.db");
        worldManager = new WorldManager(serverDataManager);
        serverDataManager.loadTables();


        World activeWorld = registerWorldSystem();

        MinecraftServer.getCommandManager().register(new WorldCommand(worldManager));

        registerPlayerEvents(activeWorld);

        minecraftServer.start(serverAddress, serverPort);
    }

    static void registerPlayerEvents(World world) {
        EventNode<@NotNull Event> playerNode = EventNode.all("Player");
        playerNode.addListener(new JoinListener(world));

        MinecraftServer.getGlobalEventHandler().addChild(playerNode);
    }

    static World registerWorldSystem() {
        worldManager.getWorldDataManager().loadWorlds();

        // Saves worlds on shutdown
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
            MinecraftServer.getInstanceManager().getInstances().forEach(instance -> {
                instance.saveChunksToStorage().join();
            });
            worldManager.getWorldDataManager().saveAllWorlds();
        });

        World world = null;
        // Generating world in first pos, if there's one.
        if (worldManager.getAllWorlds().length > 0) {
            world = worldManager.getAllWorlds()[0];
            UUID uuid = worldManager.getSavedWorldUUID(world);
            if (uuid == null) {
                worldManager.getSavedWorldUUID(world);
                return world;
            }
            worldManager.unregisterWorld(uuid); // removing since old uuid has been saved by dataManager.
            world.generate();
            worldManager.getWorldDataManager().saveWorld(world);
            worldManager.registerWorld(world);

            // Creating a default world if there's no worlds.
        } else if (worldManager.getAllWorlds().length == 0) {
           world = new World(new FlatWorldGenerator())
                    .setName("defaultWorld")
                    .enableSaveWorld(true)
                    .enableLighting(true)
                    .generate();
            worldManager.registerWorld(world);
            worldManager.getWorldDataManager().saveWorld(world);
        }
        return world;
    }

    // Making sure all essential directories exist.
    static void checkDirs() {
        // Making sure Base folder exist
        File baseFolder = new File(BASE_SERVER_PATH);
        if (!baseFolder.exists()) baseFolder.mkdir();

        // Making sure schematics folder exist
        File schemFolder = new File(BASE_SERVER_PATH + "Schematics/");
        if (!schemFolder.exists()) schemFolder.mkdir();
    }

}
