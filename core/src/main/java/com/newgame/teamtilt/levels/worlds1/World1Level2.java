package com.newgame.teamtilt.levels.worlds1;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.newgame.teamtilt.Platform;
import com.newgame.teamtilt.levels.LevelDefinition;

public class World1Level2 implements LevelDefinition {
    private static final float W = 300f, H = 20f;
    @Override
    public void build(World world, Array<Platform> platforms) {
        // simple placeholder layout
        platforms.add(new Platform(world, 100, 160, W, H));
        platforms.add(new Platform(world, 300, 220, W, H));
        platforms.add(new Platform(world, 550, 180, W, H));
    }
}


