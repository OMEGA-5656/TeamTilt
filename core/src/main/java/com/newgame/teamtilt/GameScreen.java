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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameScreen implements Screen {
    final TeamTiltMain game;
    private Texture backgroundTexture;
    private Texture platformTexture;
    private Stage stage;
    private ImageButton leftButton, rightButton, jumpButton;
    private boolean moveLeft = false, moveRight = false;
    private boolean isGrounded = false;
    private final float PLATFORM_WIDTH = 300;
    private final float PLATFORM_HEIGHT = 20;

    // Box2D variables
    public World world;
    private Box2DDebugRenderer debugRenderer;
    public Array<Platform> platforms;

    // Player instance
    private Player player;

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

        // Contact listener
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (contact.getFixtureA().getBody() == player.getBody() || contact.getFixtureB().getBody() == player.getBody()) {
                    isGrounded = true;
                }
            }

            @Override
            public void endContact(Contact contact) {
                if (contact.getFixtureA().getBody() == player.getBody() || contact.getFixtureB().getBody() == player.getBody()) {
                    isGrounded = false;
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

        // Add platforms using the new Platform class
        platforms.add(new Platform(world, 100, 100, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 400, 120, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 700, 170, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 400, 240, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        platforms.add(new Platform(world, 50, 240, PLATFORM_WIDTH, PLATFORM_HEIGHT));
    }

    private void createTouchControls() {
        // Load button textures
        Texture leftTexture = new Texture(Gdx.files.internal("buttons/left.png"));
        Texture rightTexture = new Texture(Gdx.files.internal("buttons/right.png"));
        Texture jumpTexture = new Texture(Gdx.files.internal("buttons/jump.png"));

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
                moveLeft = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                moveLeft = false;
            }
        });

        rightButton.addListener(new InputListener() {
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

        jumpButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (isGrounded) {
                    player.jump();
                    isGrounded = false;
                }
                return true;
            }
        });

        // Add buttons to stage
        stage.addActor(leftButton);
        stage.addActor(rightButton);
        stage.addActor(jumpButton);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Step the physics world
        world.step(1 / 60f, 6, 2);

        // Update player movement
        player.updateMovement(moveLeft, moveRight);

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
    public void dispose() {
        backgroundTexture.dispose();
        platformTexture.dispose();
        stage.dispose();
        world.dispose();
        debugRenderer.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
