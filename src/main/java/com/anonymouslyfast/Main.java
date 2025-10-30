package com.anonymouslyfast;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;

public class Main {

    private static final String serverAdress = "0.0.0.0";
    private static final int serverPort = 25565; // Default mc port

    static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Temporary basic world
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setGenerator(unit -> {
            unit.modifier().fillHeight(0, 40, Block.STONE);
        });

        // Simple lighting
        instanceContainer.setChunkSupplier(LightingChunk::new);

        // Simple player join event
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0,43,0));
        });

        minecraftServer.start(serverAdress, serverPort); // Currently starting on local port
    }
}
