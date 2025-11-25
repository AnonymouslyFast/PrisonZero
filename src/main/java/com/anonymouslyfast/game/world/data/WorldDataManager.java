package com.anonymouslyfast.game.world.data;

import com.anonymouslyfast.game.data.DataManager;
import com.anonymouslyfast.game.world.generators.WorldGenerator;
import com.anonymouslyfast.game.world.World;
import com.anonymouslyfast.game.world.WorldManager;
import net.minestom.server.coordinate.Pos;

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
                    lighting_enabled BOOLEAN NOT NULL,
                    spawn_position TEXT NOT NULL
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
                    "INSERT OR REPLACE INTO worlds(name, uuid, generator, save_world_enabled, lighting_enabled, spawn_position) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
            );

            statement.setString(1, world.getWorldName());
            statement.setString(2, world.getInstanceContainer().getUuid().toString());
            statement.setString(3, world.getWorldGenerator().toString());
            statement.setBoolean(4, world.isWorldSaveEnabled());
            statement.setBoolean(5, world.isLightingEnabled());
            String position = world.getSpawnPoint().x() + "," + world.getSpawnPoint().y() + "," + world.getSpawnPoint().z();
            position = position + "," + world.getSpawnPoint().yaw() + "," + world.getSpawnPoint().pitch();
            statement.setString(6, position);
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

//    public UUID getWorldUuid(World world) {
//        try {
//            PreparedStatement preparedStatement = serverDataManager.getConnection().prepareStatement("SELECT uuid FROM worlds WHERE name = ?");
//            preparedStatement.setString(1, world.getWorldName());
//            ResultSet resultSet = preparedStatement.executeQuery();
//            if (resultSet.next()) {
//                return UUID.fromString(resultSet.getString("uuid"));
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }

    public Boolean loadWorlds() {
        try {
            ResultSet resultSet = serverDataManager.getConnection().prepareStatement("SELECT * FROM worlds").executeQuery();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String uuid = resultSet.getString("uuid");
                String spawn_position = resultSet.getString("spawn_position");
                String generator = resultSet.getString("generator");
                WorldGenerator parsedGenerator = worldManager.parseStringToWorldGenerator(generator);
                boolean saveWorldEnabled = resultSet.getBoolean("save_world_enabled");
                boolean lightingEnabled = resultSet.getBoolean("lighting_enabled");

                // Parsing saved pos text to Pos object
                String[] spawnPosCoords = spawn_position.split(",");
                Pos spawnPosition = parsedGenerator.getDefaultSpawnPosition();
                if (spawnPosCoords.length == 5) {
                    Double x = Double.valueOf(spawnPosCoords[0]);
                    Double y = Double.valueOf(spawnPosCoords[1]);
                    Double z = Double.valueOf(spawnPosCoords[2]);
                    float yaw = Float.parseFloat(spawnPosCoords[3]);
                    float pitch = Float.parseFloat(spawnPosCoords[4]);
                    spawnPosition = new Pos(x, y, z, yaw, pitch);
                }


                World world = new World(parsedGenerator)
                        .enableSaveWorld(saveWorldEnabled)
                        .enableLighting(lightingEnabled)
                        .setName(name);
                world.setSpawnPoint(spawnPosition);
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
