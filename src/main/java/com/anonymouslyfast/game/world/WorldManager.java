package com.anonymouslyfast.game.world;

import com.anonymouslyfast.game.data.DataManager;
import com.anonymouslyfast.game.world.Generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.Generators.VoidWorldGenerator;
import com.anonymouslyfast.game.world.Generators.WorldGenerator;
import com.anonymouslyfast.game.world.data.WorldDataManager;

import java.util.HashMap;

public final class WorldManager {

    private final HashMap<String, World> worlds = new HashMap<>();

    private final WorldDataManager worldDataManager;

    public WorldManager(DataManager serverDataManager) {
        this.worldDataManager = new WorldDataManager(serverDataManager, this);
    }

    public WorldGenerator parseStringToWorldGenerator(String worldGenerator) {
        return switch (worldGenerator) {
            case "flatWorldGenerator" -> new FlatWorldGenerator();
            case "voidWorldGenerator" -> new VoidWorldGenerator();
            default -> null;
        };
    }

    // Getters/Setters
    public void registerWorld(World world) {
        worlds.put(world.getWorldName(), world);
    }
    public void unregisterWorld(World world) {
        worlds.remove(world.getWorldName());
    }

    public World[] getAllWorlds() {
        return worlds.values().toArray(new World[0]);
    }

    public World getWorld(String name) {
        return worlds.get(name);
    }

    public WorldDataManager getWorldDataManager() {
        return worldDataManager;
    }




}
