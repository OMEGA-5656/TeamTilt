package com.newgame.teamtilt;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TeamTiltMain extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Set the first screen here, for example, the Main Menu Screen
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Calls the render method of the active screen
    }

    @Override
    public void dispose() {
        batch.dispose();
        // Dispose of other resources if necessary
    }
}
