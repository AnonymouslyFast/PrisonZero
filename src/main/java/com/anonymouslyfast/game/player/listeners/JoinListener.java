package com.anonymouslyfast.game.player.listeners;

import com.anonymouslyfast.game.world.World;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
public class JoinListener implements EventListener<AsyncPlayerConfigurationEvent> {

    private final World world;

    public JoinListener(World world) {
        this.world = world;
    }

    @Override
    public Class<AsyncPlayerConfigurationEvent> eventType() {
        return AsyncPlayerConfigurationEvent.class;
    }

    @Override
    public Result run(AsyncPlayerConfigurationEvent event) {
        final Player player = event.getPlayer();
        player.setGameMode(GameMode.CREATIVE);
        event.setSpawningInstance(world.getInstanceContainer());
        player.setRespawnPoint(world.getSpawnPoint());
        return Result.SUCCESS;
    }
}
