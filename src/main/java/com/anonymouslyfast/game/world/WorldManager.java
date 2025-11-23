package com.anonymouslyfast.game.world;

import com.anonymouslyfast.game.data.DataManager;
import com.anonymouslyfast.game.world.Generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.Generators.VoidWorldGenerator;
import com.anonymouslyfast.game.world.Generators.WorldGenerator;
import com.anonymouslyfast.game.world.data.WorldDataManager;


import java.util.HashMap;
import java.util.UUID;

public final class WorldManager {

    private final HashMap<UUID, World> worlds = new HashMap<>();

    private final WorldDataManager worldDataManager;

    public WorldManager(DataManager serverDataManager) {
        this.worldDataManager = new WorldDataManager(serverDataManager, this);
    }

    public WorldGenerator parseStringToWorldGenerator(String worldGenerator) {
        return switch (worldGenerator) {
            case "FlatWorldGenerator" -> new FlatWorldGenerator();
            case "VoidWorldGenerator" -> new VoidWorldGenerator();
            default -> new VoidWorldGenerator();
        };
    }

    // Getters/Setters
    public void registerWorld(World world, UUID uuid) {
        worlds.put(uuid, world);
    }
    public void unregisterWorld(World world) {
        worlds.remove(world.getInstanceContainer().getUuid());
    }

    public World[] getAllWorlds() {
        return worlds.values().toArray(new World[0]);
    }


    public World getWorld(UUID instanceUUID) {
        return worlds.get(instanceUUID);
    }

    public WorldDataManager getWorldDataManager() {
        return worldDataManager;
    }




}
