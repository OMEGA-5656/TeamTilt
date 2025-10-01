package com.newgame.teamtilt.levels.worlds2;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.newgame.teamtilt.Platform;
import com.newgame.teamtilt.levels.LevelDefinition;

public class World2Level2 implements LevelDefinition {
    private static final float W = 300f, H = 20f;
    @Override
    public void build(World world, Array<Platform> platforms) {
        platforms.add(new Platform(world, 160, 100, W, H));
        platforms.add(new Platform(world, 460, 160, W, H));
        platforms.add(new Platform(world, 760, 220, W, H));
    }
}


