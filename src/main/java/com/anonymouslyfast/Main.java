package com.anonymouslyfast;

import com.anonymouslyfast.game.data.DataManager;
import com.anonymouslyfast.game.player.listeners.JoinListener;
import com.anonymouslyfast.game.world.Generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.NotNull;


public final class Main {

    private static final String serverAddress = "0.0.0.0";
    private static final int serverPort = 25565; // Default mc port

    private static final DataManager serverDataManager = new DataManager("Databases/server.db");
    private static final WorldManager worldManager = new WorldManager(serverDataManager);

    static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        World activeWorld = registerWorldSystem();
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
        });

        World world = null;
        // Generating world in first pos, if there's one.
        if (worldManager.getAllWorlds().length > 0) {
            world = worldManager.getAllWorlds()[0];
            world.generate();
        // Creating a default world if there's no worlds.
        } else if (worldManager.getAllWorlds().length == 0) {
           world = new World(new FlatWorldGenerator())
                    .setName("defaultWorld")
                    .enableSaveWorld(true)
                    .enableLighting(true)
                    .generate();
            worldManager.registerWorld(world);
        }
        return world;
    }

}
