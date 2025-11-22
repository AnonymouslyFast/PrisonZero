package com.anonymouslyfast.World.Generators;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.Generator;

public class VoidWorldGenerator implements WorldGenerator {

    @Override
    public Generator createGenerator() {
        return unit -> {
            if (unit.absoluteStart().x() == 0 && unit.absoluteStart().z() == 0) {
                unit.modifier().setBlock(0,0,0, Block.BEDROCK);
            }
        };
    }

    @Override
    public Pos getDefaultSpawnPosition() {
        return new Pos(0, 1, 0);
    }
}
