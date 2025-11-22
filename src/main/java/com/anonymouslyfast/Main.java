package com.anonymouslyfast;

import com.anonymouslyfast.World.Generators.FlatWorldGenerator;
import com.anonymouslyfast.World.Generators.WorldGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;

public class Main {

    private static final String serverAdress = "0.0.0.0";
    private static final int serverPort = 25565; // Default mc port

    static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Temporary basic world
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        // WorldGenerator voidGenerator = new VoidWorldGenerator();
        WorldGenerator flatGenerator = new FlatWorldGenerator();
        instanceContainer.setGenerator(flatGenerator.createGenerator());

        // Simple lighting
        instanceContainer.setChunkSupplier(LightingChunk::new);

        // Simple player join event
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(flatGenerator.getDefaultSpawnPosition());
        });

        minecraftServer.start(serverAdress, serverPort);
    }
}
