package com.anonymouslyfast.World.Generators;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.generator.Generator;

public interface WorldGenerator {

    Generator createGenerator();
    Pos getDefaultSpawnPosition();
}
