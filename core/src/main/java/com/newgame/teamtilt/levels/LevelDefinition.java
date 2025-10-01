package com.newgame.teamtilt.levels;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.newgame.teamtilt.Platform;

public interface LevelDefinition {
    void build(World world, Array<Platform> platforms);
}


