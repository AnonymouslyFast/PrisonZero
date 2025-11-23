package com.anonymouslyfast.game.world;

import com.anonymouslyfast.game.world.Generators.WorldGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;

public class World {

    private final String WORLDS_PATH = "Worlds/";

    private final WorldGenerator worldGenerator;
    private boolean enabledLighting;
    private InstanceContainer instanceContainer;
    private boolean saveWorldEnabled;
    private String worldName = "world";

    // Builder
    public World(WorldGenerator worldGenerator) {
        this.worldGenerator = worldGenerator;
    }

    public World setName(String name) {
        if (instanceContainer != null) return null;
        this.worldName = name;
        return this;
    }


    public World enableLighting(boolean enable) {
        enabledLighting = enable;
        return this;
    }

    public World enableSaveWorld(boolean enable) {
        saveWorldEnabled = enable;
        return this;
    }

    public World generate()
    {

        // Create Instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        instanceContainer = instanceManager.createInstanceContainer();

        if (saveWorldEnabled) instanceContainer.setChunkLoader(new AnvilLoader(WORLDS_PATH + worldName));

        instanceContainer.setGenerator(worldGenerator.createGenerator());

        // Lighting
        if (enabledLighting) instanceContainer.setChunkSupplier(LightingChunk::new);

        return this;
    }

    // Getters/Setters
    public InstanceContainer getInstanceContainer()
    {
        return instanceContainer;
    }

    public WorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public String getWorldName()
    {
        return worldName;
    }


}
