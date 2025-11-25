package com.anonymouslyfast.game.world;

import com.anonymouslyfast.Main;
import com.anonymouslyfast.game.world.generators.WorldGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import net.sandrohc.schematic4j.schematic.Schematic;
import net.sandrohc.schematic4j.schematic.types.SchematicBlock;

import java.util.Map;

public class World {

    private final static String WORLDS_PATH = Main.BASE_SERVER_PATH + "Worlds/";

    private final WorldGenerator worldGenerator;
    private boolean enabledLighting;
    private InstanceContainer instanceContainer;
    private boolean saveWorldEnabled;
    private String worldName = "world";
    private Pos spawnPoint;

    // Builder
    public World(WorldGenerator worldGenerator) {
        this.worldGenerator = worldGenerator;
        this.spawnPoint = worldGenerator.getDefaultSpawnPosition();
    }

    public World setName(String name) {
        if (instanceContainer != null) return this;
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

    // Schematics
    public void pasteSchematic(Schematic schematic, Vec position) {
        if (schematic == null) return;
        if (instanceContainer == null) return;

        AbsoluteBlockBatch batch = new AbsoluteBlockBatch();

        // Looping through
        for (int x = 0; x < schematic.width(); x++) {
            for (int y = 0; y < schematic.height(); y++) {
                for (int z = 0; z < schematic.length(); z++) {

                    SchematicBlock schematicBlock = schematic.block(x, y, z);
                    String blockName = schematicBlock.block;
                    Map<String, String> blockProperties = schematicBlock.states;
                    Block block = Block.fromKey(blockName);
                    block = block.withProperties(blockProperties);

                    batch.setBlock(x, y, z, block);
                }
            }
        }
        batch.apply(instanceContainer, () -> {
            System.out.println("failed to paste schematic.");
        });
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

    public Boolean  isLightingEnabled() { return enabledLighting; }

    public Boolean isWorldSaveEnabled() { return saveWorldEnabled; }

    public Pos getSpawnPoint() { return spawnPoint; }
    public void setSpawnPoint(Pos spawnPoint) { this.spawnPoint = spawnPoint; }


}
