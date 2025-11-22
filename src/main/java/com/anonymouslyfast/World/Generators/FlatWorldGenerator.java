package com.anonymouslyfast.World.Generators;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;

public class FlatWorldGenerator implements WorldGenerator {
    @Override
    public Generator createGenerator() {
        return unit -> {
            unit.modifier().fillHeight(0, 1, Block.BEDROCK);
            unit.modifier().fillHeight(1, 3, Block.DIRT);
            unit.modifier().fillHeight(3, 4, Block.GRASS_BLOCK);
        };
    }

    @Override
    public Pos getDefaultSpawnPosition() {
        return new Pos(0, 5, 0);
    }
}
