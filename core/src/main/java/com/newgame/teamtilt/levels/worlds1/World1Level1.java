package com.newgame.teamtilt.levels.worlds1;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.newgame.teamtilt.Platform;
import com.newgame.teamtilt.levels.LevelDefinition;

public class World1Level1 implements LevelDefinition {
    private static final float PLATFORM_WIDTH = 300f;
    private static final float PLATFORM_HEIGHT = 20f;

    @Override
    public void build(World world, Array<Platform> platforms) {
        platforms.add(new Platform(world, 100, 100, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 400, 120, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 700, 170, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 400, 240, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 50, 240, PLATFORM_WIDTH, PLATFORM_HEIGHT));
    }
}


