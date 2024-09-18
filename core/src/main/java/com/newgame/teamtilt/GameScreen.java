package com.newgame.teamtilt;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
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
    private Texture characterTexture;
    private Texture platformTexture;
    private Stage stage;
    private ImageButton leftButton, rightButton, jumpButton;
    private boolean moveLeft = false, moveRight = false;

    private final float speed = 5f;
    private float PPM = 100f; // Pixels per meter

    // Box2D variables
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body characterBody;
    private Array<Body> platformBodies;

    // Starting position of the character
    private final float startX = 150, startY = 320;

    public GameScreen(final TeamTiltMain game) {
        this.game = game;

        // Load textures
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/background.png"));
        characterTexture = new Texture(Gdx.files.internal("characters/character.png"));
        platformTexture = new Texture(Gdx.files.internal("platforms/platform.png"));

        // Initialize Box2D world
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        platformBodies = new Array<>();

        // Create Box2D bodies
        createCharacterBody();
        addPlatform(100, 100, 300, 20);
        addPlatform(400, 120, 300, 20);
        addPlatform(700, 200, 300, 20);
        addPlatform(400, 240, 300, 20);
        addPlatform(50, 240, 300, 20);

        // Initialize stage and touch controls
        stage = new Stage(new ScreenViewport());
        createTouchControls();
        Gdx.input.setInputProcessor(stage);
    }

    private void createCharacterBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX / PPM, startY / PPM);

        characterBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(characterTexture.getWidth() / 2 / PPM, characterTexture.getHeight() / 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        characterBody.createFixture(fixtureDef);
        shape.dispose();
    }

    private void addPlatform(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((x + width / 2) / PPM, (y + height / 2) / PPM);

        Body platformBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.7f;

        platformBody.createFixture(fixtureDef);
        platformBodies.add(platformBody);
        shape.dispose();
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
                if(!characterBody.isAwake())
                    characterBody.applyLinearImpulse(new Vector2(0, 0.5f), characterBody.getWorldCenter(), true);
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

        // Sync character position with the Box2D body
        float characterX = characterBody.getPosition().x * PPM - characterTexture.getWidth() / 2;
        float characterY = characterBody.getPosition().y * PPM - characterTexture.getHeight() / 2;

        // Apply movement
        if (moveLeft) {
            characterBody.setLinearVelocity(-speed, characterBody.getLinearVelocity().y);
        }
        if (moveRight) {
            characterBody.setLinearVelocity(speed, characterBody.getLinearVelocity().y);
        }

        if (characterBody.getPosition().y < 0) {//respawning statement
            characterBody.setTransform(startX / PPM, startY / PPM, 0);
        }

        // Draw background, platforms, and character
        game.batch.begin();
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        for (Body platformBody : platformBodies) {
            Vector2 position = platformBody.getPosition();
            game.batch.draw(platformTexture, (position.x * PPM) - (300 / 2), (position.y * PPM) - (20 / 2), 300, 20);
        }
        game.batch.draw(characterTexture, characterX, characterY);
        game.batch.end();

        // Debug render (optional)
        debugRenderer.render(world, game.batch.getProjectionMatrix().cpy().scale(PPM, PPM, 0));

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
        characterTexture.dispose();
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
