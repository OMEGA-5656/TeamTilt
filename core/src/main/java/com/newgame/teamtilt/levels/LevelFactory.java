package com.newgame.teamtilt.levels;

public final class LevelFactory {
    private LevelFactory() {}

    public static LevelDefinition getLevel(int worldIndex, int levelIndex) {
        int w = Math.max(1, Math.min(4, worldIndex));
        int l = Math.max(1, Math.min(6, levelIndex));
        if (w == 1 && l == 1) return new com.newgame.teamtilt.levels.worlds1.World1Level1();
        if (w == 1 && l == 2) return new com.newgame.teamtilt.levels.worlds1.World1Level2();
        if (w == 1 && l == 3) return new com.newgame.teamtilt.levels.worlds1.World1Level3();
        if (w == 1 && l == 4) return new com.newgame.teamtilt.levels.worlds1.World1Level4();
        if (w == 1 && l == 5) return new com.newgame.teamtilt.levels.worlds1.World1Level5();
        if (w == 1 && l == 6) return new com.newgame.teamtilt.levels.worlds1.World1Level6();
        if (w == 2 && l == 1) return new com.newgame.teamtilt.levels.worlds2.World2Level1();
        if (w == 2 && l == 2) return new com.newgame.teamtilt.levels.worlds2.World2Level2();
        if (w == 2 && l == 3) return new com.newgame.teamtilt.levels.worlds2.World2Level3();
        if (w == 2 && l == 4) return new com.newgame.teamtilt.levels.worlds2.World2Level4();
        if (w == 2 && l == 5) return new com.newgame.teamtilt.levels.worlds2.World2Level5();
        if (w == 2 && l == 6) return new com.newgame.teamtilt.levels.worlds2.World2Level6();
        if (w == 3 && l == 1) return new com.newgame.teamtilt.levels.worlds3.World3Level1();
        if (w == 3 && l == 2) return new com.newgame.teamtilt.levels.worlds3.World3Level2();
        if (w == 3 && l == 3) return new com.newgame.teamtilt.levels.worlds3.World3Level3();
        if (w == 3 && l == 4) return new com.newgame.teamtilt.levels.worlds3.World3Level4();
        if (w == 3 && l == 5) return new com.newgame.teamtilt.levels.worlds3.World3Level5();
        if (w == 3 && l == 6) return new com.newgame.teamtilt.levels.worlds3.World3Level6();
        if (w == 4 && l == 1) return new com.newgame.teamtilt.levels.worlds4.World4Level1();
        if (w == 4 && l == 2) return new com.newgame.teamtilt.levels.worlds4.World4Level2();
        if (w == 4 && l == 3) return new com.newgame.teamtilt.levels.worlds4.World4Level3();
        if (w == 4 && l == 4) return new com.newgame.teamtilt.levels.worlds4.World4Level4();
        if (w == 4 && l == 5) return new com.newgame.teamtilt.levels.worlds4.World4Level5();
        return new com.newgame.teamtilt.levels.worlds4.World4Level6();
    }
}


