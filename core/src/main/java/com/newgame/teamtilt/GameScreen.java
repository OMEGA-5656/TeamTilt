package com.newgame.teamtilt;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.loaders.SoundLoader;


public class GameScreen implements Screen {
    final TeamTiltMain game;
    private Texture backgroundTexture;
    private Texture characterTexture;
    private Texture platformTexture;
    private Sound buttonClick;

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

    // Starting position of the character
    private final float startX = 150;
    private final float startY = 120;

    public GameScreen(final TeamTiltMain game) {
        this.game = game;

        // Load the background, character, and platform textures
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/background.png"));
        characterTexture = new Texture(Gdx.files.internal("characters/character.png"));
        platformTexture = new Texture(Gdx.files.internal("platforms/platform.png"));

        // Set initial character position
        characterX = startX;
        characterY = startY;

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

        // Load button click sounds
        Sound buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/buttonClickSound.wav"));

        // Create buttons with the textures
        leftButton = new ImageButton(new TextureRegionDrawable(leftTexture));
        rightButton = new ImageButton(new TextureRegionDrawable(rightTexture));
        jumpButton = new ImageButton(new TextureRegionDrawable(jumpTexture));

        // Set button positions and sizes
        leftButton.setPosition(50, 50);
        rightButton.setPosition(200, 50);
        jumpButton.setPosition(Gdx.graphics.getWidth() - 150, 50);

        // Add listeners for buttons
        leftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Button", "Left button touched");
                buttonClickSound.play();
                moveLeft = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Button", "Left button released");
                moveLeft = false;
            }
        });

        rightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Button", "Right button touched");
                buttonClickSound.play();
                moveRight = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Button", "Right button released");
                moveRight = false;
            }
        });

        jumpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("Button", "Jump button touched");
                buttonClickSound.play();
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
            // Check if the character is above the platform and falling down onto it
            if (characterY <= platform.y + platform.height // Character is above the platform
                && characterY + velocityY * delta <= platform.y + platform.height // Character is falling onto the platform
                && characterX + characterTexture.getWidth() > platform.x // Character's right side is past the platform's left side
                && characterX < platform.x + platform.width) { // Character's left side is before the platform's right side

                // Land the character on the platform
                characterY = platform.y + platform.height; // Align the character with the platform's top
                velocityY = 0; // Stop vertical movement
                isGrounded = true; // Character is grounded
                break; // Stop checking other platforms
            }
        }

        // Check if the character has fallen off the screen
        if (characterY + characterTexture.getHeight() < 0) {
            // Respawn the character if it's fallen below the screen height
            characterX = startX;
            characterY = startY;
            velocityY = 0;
            isGrounded = false;
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
