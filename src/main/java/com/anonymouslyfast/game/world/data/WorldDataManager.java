package com.anonymouslyfast.game.world.data;

import com.anonymouslyfast.game.data.DataManager;
import com.anonymouslyfast.game.world.Generators.FlatWorldGenerator;
import com.anonymouslyfast.game.world.Generators.WorldGenerator;
import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WorldDataManager {

    private final DataManager serverDataManager;
    private final WorldManager worldManager;

    private final String tableQuery = """
                CREATE TABLE IF NOT EXISTS `worlds` (
                    name TEXT NOT NULL PRIMARY KEY,
                    uuid TEXT NOT NULL,
                    generator TEXT NOT NULL,
                    save_world_enabled BOOLEAN NOT NULL,
                    lighting_enabled BOOLEAN NOT NULL
                );""";

    public WorldDataManager(DataManager serverDataManager, WorldManager worldManager) {
        serverDataManager.registerTable("worlds", tableQuery);
        this.serverDataManager = serverDataManager;
        this.worldManager = worldManager;
    }

    public Boolean saveWorld(World world) {
        if (!world.isWorldSaveEnabled()) return false;

        try {
            PreparedStatement statement = serverDataManager.getConnection().prepareStatement(
                    "INSERT OR REPLACE INTO worlds(name, uuid, generator, save_world_enabled, lighting_enabled) " +
                            "VALUES (?, ?, ?, ?, ?)"
            );

            statement.setString(1, world.getWorldName());
            statement.setString(2, world.getInstanceContainer().getUuid().toString());
            statement.setString(3, world.getWorldGenerator().toString());
            statement.setBoolean(4, world.isWorldSaveEnabled());
            statement.setBoolean(5, world.isLightingEnabled());
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            //TODO: replace with logger stuff
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void saveAllWorlds() {
        for (World world : worldManager.getAllWorlds()) {
            saveWorld(world);
        }
    }

    public Boolean loadWorlds() {
        try {
            ResultSet resultSet = serverDataManager.getConnection().prepareStatement("SELECT * FROM worlds").executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String uuid = resultSet.getString("uuid");
                String generator = resultSet.getString("generator");
                WorldGenerator parsedGenerator = worldManager.parseStringToWorldGenerator(generator);
                boolean saveWorldEnabled = resultSet.getBoolean("save_world_enabled");
                boolean lightingEnabled = resultSet.getBoolean("lighting_enabled");

                World world = new World(parsedGenerator)
                        .enableSaveWorld(saveWorldEnabled)
                        .enableLighting(lightingEnabled)
                        .setName(name);
                worldManager.registerWorld(world, UUID.fromString(uuid));
            }
        } catch(SQLException e) {
            //TODO: replace with logger stuff
            e.printStackTrace();
            return false;
        }
        return true;
    }






}
