package com.anonymouslyfast.game.world.generators;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.generator.Generator;

public interface WorldGenerator {

    Generator createGenerator();
    Pos getDefaultSpawnPosition();

    @Override
    String toString();
}
