package com.newgame.teamtilt;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class GameScreen implements Screen {
    final TeamTiltMain game;
    private Texture backgroundTexture;
    private Texture characterTexture;
    private Texture platformTexture;

    // Character position and physics variables
    private float characterX;
    private float characterY;
    private float velocityY = 0;
    private final float gravity = -500f;
    private boolean isGrounded = false;

    // Platform
    private Rectangle platform;

    public GameScreen(final TeamTiltMain game) {
        this.game = game;

        // Load the background, character, and platform textures
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/background.png"));
        characterTexture = new Texture(Gdx.files.internal("characters/character.png"));
        platformTexture = new Texture(Gdx.files.internal("platforms/platform.png"));

        // Set initial character position (centered on screen)
        characterX = 150;
        characterY = 120;

        // Set platform size and position
        platform = new Rectangle();
        platform.x = 100; // X-position of platform
        platform.y = 100; // Y-position of platform
        platform.width = 300; // Width of platform
        platform.height = 20; // Height of platform
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Clear the screen with a white background
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Apply gravity if not grounded
        if (!isGrounded) {
            velocityY += gravity * delta;
        }
        characterY += velocityY * delta;

        // Collision detection with platform
        if (characterY <= platform.y + platform.height && characterY + characterTexture.getHeight() >= platform.y &&
            characterX + characterTexture.getWidth() > platform.x && characterX < platform.x + platform.width) {

            // If falling down and the character's feet are on the platform
            if (velocityY < 0) {
                characterY = platform.y + platform.height; // Snap character to platform
                velocityY = 0; // Stop vertical movement
                isGrounded = true;
            }
        } else {
            isGrounded = false; // Allow falling if not on platform
        }

        // Draw the background, platform, and character
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Draw background
        game.batch.draw(platformTexture, platform.x, platform.y, platform.width, platform.height); // Draw platform
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
        platformTexture.dispose();
    }
}
