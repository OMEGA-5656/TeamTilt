package com.newgame.teamtilt;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

    // List to hold multiple platforms
    private Array<Rectangle> platforms;

    // Touch controls
    private Stage stage;
    private ImageButton leftButton;
    private ImageButton rightButton;
    private ImageButton jumpButton;

    private boolean moveLeft = false;
    private boolean moveRight = false;

    // Movement parameters
    private float speed = 200f;

    public GameScreen(final TeamTiltMain game) {
        this.game = game;

        // Load the background, character, and platform textures
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/background.png"));
        characterTexture = new Texture(Gdx.files.internal("characters/character.png"));
        platformTexture = new Texture(Gdx.files.internal("platforms/platform.png"));

        // Set initial character position
        characterX = 150;
        characterY = 120;

        // Initialize platforms array
        platforms = new Array<>();
        addPlatform(100, 100, 300, 20);  // spawning platform
        addPlatform(400, 120, 300, 20);

        // Initialize stage for touch controls
        stage = new Stage(new ScreenViewport());

        // Load button textures (ensure they're translucent PNGs)
        Texture leftTexture = new Texture(Gdx.files.internal("buttons/left.png"));
        Texture rightTexture = new Texture(Gdx.files.internal("buttons/right.png"));
        Texture jumpTexture = new Texture(Gdx.files.internal("buttons/jump.png"));

        // Create buttons with the textures
        leftButton = new ImageButton(new TextureRegionDrawable(leftTexture));
        rightButton = new ImageButton(new TextureRegionDrawable(rightTexture));
        jumpButton = new ImageButton(new TextureRegionDrawable(jumpTexture));

        // Set button positions and sizes
        leftButton.setPosition(50, 50);
        rightButton.setPosition(200, 50);
        jumpButton.setPosition(Gdx.graphics.getWidth() - 150, 50);

        // Add listeners for buttons
        leftButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                moveLeft = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                moveLeft = false;
            }
        });

        rightButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                moveRight = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                moveRight = false;
            }
        });

        jumpButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isGrounded) {
                    velocityY = 250f; // Jump velocity
                    isGrounded = false;
                }
                return true;
            }
        });

        // Add buttons to the stage
        stage.addActor(leftButton);
        stage.addActor(rightButton);
        stage.addActor(jumpButton);

        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);
    }

    // Method to add platforms
    private void addPlatform(float x, float y, float width, float height) {
        Rectangle platform = new Rectangle();
        platform.x = x;
        platform.y = y;
        platform.width = width;
        platform.height = height;
        platforms.add(platform);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Apply gravity if not grounded
        if (!isGrounded) {
            velocityY += gravity * delta;
            characterY += velocityY * delta;
        }

        // Apply horizontal movement
        if (moveLeft) {
            characterX -= speed * delta;
        }
        if (moveRight) {
            characterX += speed * delta;
        }

        // Collision detection with any platform
        isGrounded = false; // Reset grounded state
        for (Rectangle platform : platforms) {
            if (characterY <= platform.y + platform.height && characterX + characterTexture.getWidth() > platform.x && characterX < platform.x + platform.width) {
                characterY = platform.y + platform.height; // Snap character to platform
                velocityY = 0; // Stop vertical movement
                isGrounded = true; // Character is grounded
                break; // No need to check other platforms
            }
        }

        // Draw the background, platforms, and character
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Draw background

        // Draw each platform
        for (Rectangle platform : platforms) {
            game.batch.draw(platformTexture, platform.x, platform.y, platform.width, platform.height);
        }

        // Draw the character
        game.batch.draw(characterTexture, characterX, characterY);
        game.batch.end();

        // Draw the stage with buttons
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        // Dispose of the textures and stage
        backgroundTexture.dispose();
        characterTexture.dispose();
        platformTexture.dispose();
        stage.dispose();
    }
}
