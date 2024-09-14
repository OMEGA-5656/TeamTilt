package com.newgame.teamtilt;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

public class GameScreen implements Screen {
    final TeamTiltMain game;
    private Texture backgroundTexture;
    private Texture characterTexture;

    // Character position
    private float characterX;
    private float characterY;

    public GameScreen(final TeamTiltMain game) {
        this.game = game;

        // Load the background and character textures
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/background.png"));
        characterTexture = new Texture(Gdx.files.internal("characters/character.png"));

        // Set initial character position (centered on screen)
        characterX = (Gdx.graphics.getWidth() - characterTexture.getWidth()) / 2;
        characterY = (Gdx.graphics.getHeight() - characterTexture.getHeight()) / 2;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Clear the screen with a white background
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the background and character
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Draw background
        game.batch.draw(characterTexture, characterX, characterY); // Draw character
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Dispose of the textures to prevent memory leaks
        backgroundTexture.dispose();
        characterTexture.dispose();
    }
}
