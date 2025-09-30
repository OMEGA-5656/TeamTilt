package com.newgame.teamtilt;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    final TeamTiltMain game;
    private Texture backgroundTexture;
    private Texture platformTexture;
    private Texture leftTexture, rightTexture, jumpTexture;
    private Texture buttonUpTexture, buttonDownTexture, overlayTexture;
    private Stage stage;
    private ImageButton leftButton, rightButton, jumpButton;
    private TextButton pauseButton, resumeButton, quitButton;
    private Image overlayImage;
    private Group pauseMenuGroup;
    private Skin uiSkin;
    private boolean isPaused = false;
    // Movement and grounded state handled by InputHandler
    private final float PLATFORM_WIDTH = 300;
    private final float PLATFORM_HEIGHT = 20;

    // Box2D variables
    public World world;
    private Box2DDebugRenderer debugRenderer;
    public Array<Platform> platforms;

    // Player instance
    private Player player;
    private InputHandler inputHandler;

    public GameScreen(final TeamTiltMain game) {
        this.game = game;

        // Load textures
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/background.png"));
        platformTexture = new Texture(Gdx.files.internal("platforms/platform.png"));
        Texture characterTexture = new Texture(Gdx.files.internal("characters/character.png"));

        // Initialize Box2D world
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        platforms = new Array<>();

        // Create player
        player = new Player(world, characterTexture);

        // Initialize InputHandler and set it as the input processor
        inputHandler = new InputHandler(player.getBody(), player.getSpeed());

        // Contact listener
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.getFixtureA().getBody() == player.getBody() || contact.getFixtureB().getBody() == player.getBody()) {
                    inputHandler.setGrounded(true);
                }
            }

            @Override
            public void endContact(Contact contact) {
                if (contact.getFixtureA().getBody() == player.getBody() || contact.getFixtureB().getBody() == player.getBody()) {
                    inputHandler.setGrounded(false);
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        // Initialize stage and touch controls
        stage = new Stage(new ScreenViewport());
        createTouchControls();
        Gdx.input.setInputProcessor(stage);

        // Initialize pause UI
        setupUiSkin();
        createPauseControls();

        // Add platforms using the new Platform class
        platforms.add(new Platform(world, 100, 100, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 400, 120, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 700, 170, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 400, 240, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 50, 240, PLATFORM_WIDTH, PLATFORM_HEIGHT));
    }

    private void createTouchControls() {
        // Load button textures
        leftTexture = new Texture(Gdx.files.internal("buttons/left.png"));
        rightTexture = new Texture(Gdx.files.internal("buttons/right.png"));
        jumpTexture = new Texture(Gdx.files.internal("buttons/jump.png"));

        // Create buttons
        leftButton = new ImageButton(new TextureRegionDrawable(leftTexture));
        rightButton = new ImageButton(new TextureRegionDrawable(rightTexture));
        jumpButton = new ImageButton(new TextureRegionDrawable(jumpTexture));

        leftButton.setPosition(50, 50);
        rightButton.setPosition(200, 50);
        jumpButton.setPosition(Gdx.graphics.getWidth() - 150, 50);

        leftButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                inputHandler.moveLeft();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                inputHandler.stopMovement();
            }
        });

        rightButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                inputHandler.moveRight();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                inputHandler.stopMovement();
            }
        });

        jumpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                inputHandler.jump();
                return true;
            }
        });

        // Add buttons to stage
        stage.addActor(leftButton);
        stage.addActor(rightButton);
        stage.addActor(jumpButton);
    }

    private void setupUiSkin() {
        uiSkin = new Skin();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        uiSkin.add("default-font", font, BitmapFont.class);

        // Create simple colored textures for button states
        buttonUpTexture = createColoredTexture(1, 1, 0.2f, 0.2f, 0.2f, 1f);
        buttonDownTexture = createColoredTexture(1, 1, 0.35f, 0.35f, 0.35f, 1f);
        TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(buttonUpTexture));
        TextureRegionDrawable downDrawable = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));

        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle style = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        style.up = upDrawable;
        style.down = downDrawable;
        style.font = font;
        uiSkin.add("default", style);

        // Overlay for pause background
        overlayTexture = createColoredTexture(1, 1, 0f, 0f, 0f, 0.5f);
        overlayImage = new Image(new TextureRegionDrawable(new TextureRegion(overlayTexture)));
        overlayImage.setFillParent(true);
        overlayImage.setVisible(false);
        stage.addActor(overlayImage);
    }

    private Texture createColoredTexture(int width, int height, float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void createPauseControls() {
        pauseButton = new TextButton("Pause", uiSkin);
        pauseButton.setSize(160, 70);
        pauseButton.setPosition(Gdx.graphics.getWidth() - pauseButton.getWidth() - 20, Gdx.graphics.getHeight() - pauseButton.getHeight() - 20);
        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setPaused(true);
                return true;
            }
        });
        stage.addActor(pauseButton);

        pauseMenuGroup = new Group();
        pauseMenuGroup.setVisible(false);

        resumeButton = new TextButton("Resume", uiSkin);
        resumeButton.setSize(240, 80);
        quitButton = new TextButton("Quit", uiSkin);
        quitButton.setSize(240, 80);

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        resumeButton.setPosition(centerX - resumeButton.getWidth() / 2f, centerY + 50);
        quitButton.setPosition(centerX - quitButton.getWidth() / 2f, centerY - 50 - quitButton.getHeight());

        resumeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setPaused(false);
                return true;
            }
        });

        quitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Return to main menu
                game.setScreen(new MainMenuScreen(game));
                return true;
            }
        });

        pauseMenuGroup.addActor(resumeButton);
        pauseMenuGroup.addActor(quitButton);
        stage.addActor(pauseMenuGroup);
    }

    private void setPaused(boolean paused) {
        isPaused = paused;
        overlayImage.setVisible(paused);
        pauseMenuGroup.setVisible(paused);
        leftButton.setDisabled(paused);
        rightButton.setDisabled(paused);
        jumpButton.setDisabled(paused);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Step the physics world only when not paused
        if (!isPaused) {
            world.step(1 / 60f, 6, 2);

            // Update player movement
            inputHandler.updateMovement();
            player.updateMovement(inputHandler.moveLeft, inputHandler.moveRight);
        }

        // Respawn player if falling
        if (player.isFalling()) {
            player.respawn();
        }

        // Draw background, platforms, and character
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw platforms
        for (Platform platform : platforms) {
            Vector2 position = platform.getPosition();
            float platformX = (position.x * player.getPPM()) - (PLATFORM_WIDTH / 2);
            float platformY = (position.y * player.getPPM()) - (PLATFORM_HEIGHT / 2);
            game.batch.draw(platformTexture, platformX, platformY, PLATFORM_WIDTH, PLATFORM_HEIGHT);
        }

        // Draw the player
        game.batch.draw(player.getTexture(),
            player.getBody().getPosition().x * player.getPPM() - player.getTexture().getWidth() / 2,
            player.getBody().getPosition().y * player.getPPM() - player.getTexture().getHeight() / 2);

        game.batch.end();

        // Debug render (optional)
        debugRenderer.render(world, game.batch.getProjectionMatrix().cpy().scale(player.getPPM(), player.getPPM(), 0));

        // Draw the UI buttons
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
        backgroundTexture.dispose();
        platformTexture.dispose();
        player.getTexture().dispose();
        leftTexture.dispose();
        rightTexture.dispose();
        jumpTexture.dispose();
        inputHandler.dispose();
        world.dispose();
        debugRenderer.dispose();
        stage.dispose();
        if (uiSkin != null) uiSkin.dispose();
        if (buttonUpTexture != null) buttonUpTexture.dispose();
        if (buttonDownTexture != null) buttonDownTexture.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
    }
}
