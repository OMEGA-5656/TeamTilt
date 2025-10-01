package com.newgame.teamtilt.levels.worlds3;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.newgame.teamtilt.Platform;
import com.newgame.teamtilt.levels.LevelDefinition;

public class World3Level4 implements LevelDefinition {
    private static final float W = 300f, H = 20f;
    @Override
    public void build(World world, Array<Platform> platforms) {
        platforms.add(new Platform(world, 160, 140, W, H));
        platforms.add(new Platform(world, 460, 200, W, H));
    }
}


