package com.anonymouslyfast;

import com.anonymouslyfast.game.player.listeners.JoinListener;
import com.anonymouslyfast.game.world.Generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.World;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import org.jetbrains.annotations.NotNull;


public final class Main {

    private static final String serverAddress = "0.0.0.0";
    private static final int serverPort = 25565; // Default mc port

    static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();

        World world = new World(new FlatWorldGenerator())
                .enableLighting(true)
                .enableSaveWorld(true)
                .generate();

        registerPlayerEvents(world);

        minecraftServer.start(serverAddress, serverPort);
    }

    static void registerPlayerEvents(World world) {
        EventNode<@NotNull Event> playerNode = EventNode.all("Player");
        playerNode.addListener(new JoinListener(world));

        MinecraftServer.getGlobalEventHandler().addChild(playerNode);
    }
}
