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
import com.newgame.teamtilt.levels.LevelDefinition;
import com.newgame.teamtilt.levels.LevelProgress;
import com.badlogic.gdx.InputProcessor;

public class GameScreen implements Screen, InputProcessor {
    final TeamTiltMain game;
    private Texture backgroundTexture;
    private Texture platformTexture;
    private Texture leftTexture, rightTexture, jumpTexture;
    private Texture pauseIconTexture, resumeIconTexture, sidebarBgTexture;
    private Stage stage;
    private ImageButton leftButton, rightButton, jumpButton;
    private ImageButton pauseIconButton;
    private TextButton quitButton;
    private Group sidebarGroup;
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
    private LevelDefinition levelDefinition;
    private Texture doorTexture;
    private float doorX, doorY, doorWidth = 40f, doorHeight = 80f;
    private boolean levelComplete = false;
    private int currentWorldIndex = 1;
    private int currentLevelIndex = 1;
    private boolean exiting = false;

    public GameScreen(final TeamTiltMain game) {
        this(game, null);
    }

    public GameScreen(final TeamTiltMain game, LevelDefinition levelDefinition) {
        this.game = game;
        this.levelDefinition = levelDefinition;
    }

    public GameScreen(final TeamTiltMain game, LevelDefinition levelDefinition, int worldIndex, int levelIndex) {
        this.game = game;
        this.levelDefinition = levelDefinition;
        this.currentWorldIndex = worldIndex;
        this.currentLevelIndex = levelIndex;

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
        // Set this screen as the input processor for back button handling
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);

        // Initialize pause UI
        setupUiSkin();
        createPauseUI();

        // Build platforms from level definition if provided
        if (this.levelDefinition != null) {
            this.levelDefinition.build(world, platforms);
        } else {
            // default layout if none provided
            platforms.add(new Platform(world, 100, 100, PLATFORM_WIDTH, PLATFORM_HEIGHT));
            platforms.add(new Platform(world, 400, 120, PLATFORM_WIDTH, PLATFORM_HEIGHT));
            platforms.add(new Platform(world, 700, 170, PLATFORM_WIDTH, PLATFORM_HEIGHT));
            platforms.add(new Platform(world, 400, 240, PLATFORM_WIDTH, PLATFORM_HEIGHT));
            platforms.add(new Platform(world, 50, 240, PLATFORM_WIDTH, PLATFORM_HEIGHT));
        }

        // Compute a simple door placement: to the right of the right-most platform
        float maxX = 0f;
        float baseY = 0f;
        for (Platform p : platforms) {
            Vector2 pos = p.getPosition();
            float px = (pos.x * player.getPPM());
            if (px > maxX) {
                maxX = px;
                baseY = (pos.y * player.getPPM());
            }
        }
        doorX = maxX + (PLATFORM_WIDTH / 2f) + 20f;
        doorY = baseY + (PLATFORM_HEIGHT / 2f);
        doorTexture = createColoredTexture(1, 1, 0f, 0f, 0f, 1f);
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

        Texture buttonUp = createColoredTexture(1, 1, 0.2f, 0.2f, 0.2f, 1f);
        Texture buttonDown = createColoredTexture(1, 1, 0.35f, 0.35f, 0.35f, 1f);
        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle style = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(buttonUp));
        style.down = new TextureRegionDrawable(new TextureRegion(buttonDown));
        style.font = font;
        uiSkin.add("default", style);
    }

    private Texture createColoredTexture(int width, int height, float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createPauseIconTexture(int width, int height, float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(r, g, b, a);
        int barWidth = Math.max(8, width / 5);
        int gap = barWidth; // gap between bars
        int barHeight = (int)(height * 0.75f);
        int y = (height - barHeight) / 2;
        int x1 = (width - (2 * barWidth + gap)) / 2;
        int x2 = x1 + barWidth + gap;
        pixmap.fillRectangle(x1, y, barWidth, barHeight);
        pixmap.fillRectangle(x2, y, barWidth, barHeight);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private Texture createResumeIconTexture(int width, int height, float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(r, g, b, a);
        int triangleWidth = (int)(width * 0.45f);
        int triangleHeight = (int)(height * 0.55f);
        int x = (width - triangleWidth) / 2;
        int y = (height - triangleHeight) / 2;
        // Right-pointing triangle
        for (int i = 0; i < triangleWidth; i++) {
            int offset = (triangleHeight * i) / triangleWidth / 2;
            int startY = y + offset;
            int endY = y + triangleHeight - offset;
            pixmap.drawLine(x + i, startY, x + i, endY);
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void createPauseUI() {
        // Create translucent grey pause and resume icons
        pauseIconTexture = createPauseIconTexture(80, 80, 0.6f, 0.6f, 0.6f, 0.6f);
        resumeIconTexture = createResumeIconTexture(80, 80, 0.6f, 0.6f, 0.6f, 0.6f);
        pauseIconButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(pauseIconTexture)));
        pauseIconButton.setSize(80, 80);
        pauseIconButton.setPosition(20, Gdx.graphics.getHeight() - pauseIconButton.getHeight() - 20);
        pauseIconButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setPaused(!isPaused);
                return true;
            }
        });
        stage.addActor(pauseIconButton);

        // Sidebar group
        sidebarGroup = new Group();
        float sidebarWidth = Math.max(260, Gdx.graphics.getWidth() * 0.35f);
        sidebarBgTexture = createColoredTexture(1, 1, 0f, 0f, 0f, 0.6f);
        Image sidebarBg = new Image(new TextureRegionDrawable(new TextureRegion(sidebarBgTexture)));
        sidebarBg.setSize(sidebarWidth, Gdx.graphics.getHeight());
        sidebarGroup.addActor(sidebarBg);

        quitButton = new TextButton("Quit", uiSkin);
        quitButton.setSize(200, 80);
        quitButton.setPosition((sidebarWidth - quitButton.getWidth()) / 2f, Gdx.graphics.getHeight() - 200);
        quitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
                return true;
            }
        });
        sidebarGroup.addActor(quitButton);

        // Start hidden just off-screen to the left
        sidebarGroup.setPosition(-sidebarWidth, 0);
        stage.addActor(sidebarGroup);
        // Keep pause/resume icon above the sidebar so it stays clickable
        pauseIconButton.toFront();
    }

    private void setPaused(boolean paused) {
        isPaused = paused;
        float sidebarWidth = sidebarGroup.getChildren().first().getWidth();
        if (paused) {
            sidebarGroup.setX(0);
            // switch to resume icon when paused
            pauseIconButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(resumeIconTexture));
            pauseIconButton.invalidate();
            pauseIconButton.toFront();
        } else {
            sidebarGroup.setX(-sidebarWidth);
            // switch back to pause icon when resumed
            pauseIconButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(pauseIconTexture));
            pauseIconButton.invalidate();
            pauseIconButton.toFront();
        }
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

        // Step the physics world only when not paused and not exiting
        if (!isPaused && !exiting) {
            world.step(1 / 60f, 6, 2);

            // Update player movement
            inputHandler.updateMovement();
            player.updateMovement(inputHandler.moveLeft, inputHandler.moveRight);

            // Check door overlap (AABB) when not complete
            if (!levelComplete) {
                float px = player.getBody().getPosition().x * player.getPPM();
                float py = player.getBody().getPosition().y * player.getPPM();
                float pw = player.getTexture().getWidth();
                float ph = player.getTexture().getHeight();
                boolean overlap = px + pw/2 > doorX && px - pw/2 < doorX + doorWidth &&
                                   py + ph/2 > doorY && py - ph/2 < doorY + doorHeight;
                if (overlap) {
                    levelComplete = true;
                    // Despawn player by moving off-screen and stopping movement
                    player.getBody().setLinearVelocity(0, 0);
                    player.getBody().setTransform(-1000f, -1000f, 0);
                    // Schedule navigation after this frame to avoid rendering/dispose races
                    if (!exiting) {
                        exiting = true;
                        LevelProgress.markCompleted(currentWorldIndex, currentLevelIndex);
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(new LevelsScreen(game, currentWorldIndex));
                                dispose();
                            }
                        });
                    }
                }
            }
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
        if (!levelComplete) {
            game.batch.draw(player.getTexture(),
                player.getBody().getPosition().x * player.getPPM() - player.getTexture().getWidth() / 2,
                player.getBody().getPosition().y * player.getPPM() - player.getTexture().getHeight() / 2);
        }

        // Draw the door
        game.batch.draw(doorTexture, doorX, doorY, doorWidth, doorHeight);

        game.batch.end();

        // Debug render (optional)
        if (!exiting) {
            debugRenderer.render(world, game.batch.getProjectionMatrix().cpy().scale(player.getPPM(), player.getPPM(), 0));
        }

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
        if (pauseIconTexture != null) pauseIconTexture.dispose();
        if (doorTexture != null) doorTexture.dispose();
        if (sidebarBgTexture != null) sidebarBgTexture.dispose();
    }

    // InputProcessor methods for back button handling
    @Override
    public boolean keyDown(int keycode) {
        // Android back button is keycode 131 (Input.Keys.BACK)
        if (keycode == 131) {
            // From GameScreen, go to LevelsScreen for the current world
            game.setScreen(new LevelsScreen(game, currentWorldIndex));
            dispose();
            return true;
        }
        // Let stage handle other inputs
        return stage.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return stage.keyUp(keycode);
    }

    @Override
    public boolean keyTyped(char character) {
        return stage.keyTyped(character);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return stage.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return stage.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return stage.touchCancelled(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return stage.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return stage.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return stage.scrolled(amountX, amountY);
    }
}
