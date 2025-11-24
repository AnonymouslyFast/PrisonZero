package com.anonymouslyfast.game.world;

import com.anonymouslyfast.game.data.DataManager;
import com.anonymouslyfast.game.world.Generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.Generators.VoidWorldGenerator;
import com.anonymouslyfast.game.world.Generators.WorldGenerator;
import com.anonymouslyfast.game.world.data.WorldDataManager;
import org.jetbrains.annotations.NotNull;


import java.util.*;

public final class WorldManager {

    private final HashMap<String, World> worlds = new HashMap<>();
    private final HashMap<String, World> worldNames = new HashMap<>();

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
    public void registerWorld(World world) {
        worlds.put(world.getInstanceContainer().getUuid().toString(), world);
        worldDataManager.saveWorld(world);
        worldNames.put(world.getWorldName(), world);
    }
    //IMPORTANT: ONLY USE IF WORLD ISN'T GENERATED.
    public void registerWorld(World world, UUID uuid) {
        worlds.put(uuid.toString(), world);
        worldNames.put(world.getWorldName(), world);
    }

    public void unregisterWorld(World world) { worlds.remove(world.getInstanceContainer().getUuid().toString()); }
    public void unregisterWorld(UUID worldUUID) { worlds.remove(worldUUID.toString()); }

    public World[] getAllWorlds() {
        return worlds.values().toArray(new World[0]);
    }

    public List<String> getAllWorldNames() {
        return worldNames.keySet().stream().toList();
    }

    public boolean isNameTaken(String name) {
        return worldNames.containsKey(name);
    }


    public World getWorld(UUID instanceUUID) {
        return worlds.get(instanceUUID.toString());
    }

    public World getWorld(String name) {
        return worldNames.get(name);
    }

    public WorldDataManager getWorldDataManager() {
        return worldDataManager;
    }

    public UUID getSavedWorldUUID(World world) {
        for (Map.Entry<String, World> worldEntry : worlds.entrySet()) {
            if (worldEntry.getValue().equals(world)) {
                return UUID.fromString(worldEntry.getKey());
            }
        }
        return null;
    }




}
