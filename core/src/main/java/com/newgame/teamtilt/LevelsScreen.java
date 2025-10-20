package com.newgame.teamtilt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.newgame.teamtilt.levels.LevelDefinition;
import com.newgame.teamtilt.levels.LevelFactory;
import com.newgame.teamtilt.levels.LevelProgress;
import com.newgame.teamtilt.multiplayer.MultiplayerManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.InputProcessor;

public class LevelsScreen implements Screen, InputProcessor {
    private final TeamTiltMain game;
    private final int worldIndex;
    private final MultiplayerManager multiplayerManager;
    private final Stage stage;
    private final Skin skin;
    private Texture backIconTexture;
    private Texture greenUpTexture, greenDownTexture;

    public LevelsScreen(TeamTiltMain game, int worldIndex) {
        this(game, worldIndex, null);
    }
    
    public LevelsScreen(TeamTiltMain game, int worldIndex, MultiplayerManager multiplayerManager) {
        this.game = game;
        this.worldIndex = worldIndex;
        this.multiplayerManager = multiplayerManager;
        this.stage = new Stage(new ScreenViewport());
        this.skin = createSkin();
        buildUi();
        // Set this screen as the input processor for back button handling
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
    }

    private Skin createSkin() {
        Skin s = new Skin();
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2f);
        s.add("default-font", font, BitmapFont.class);

        Texture up = makeTex(1,1, 0.2f,0.2f,0.2f,1f);
        Texture down = makeTex(1,1, 0.35f,0.35f,0.35f,1f);
        // Precreate green styles for completed levels
        greenUpTexture = makeTex(1,1, 0.2f,0.6f,0.2f,1f);
        greenDownTexture = makeTex(1,1, 0.25f,0.7f,0.25f,1f);
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
        tbs.up = new TextureRegionDrawable(new TextureRegion(up));
        tbs.down = new TextureRegionDrawable(new TextureRegion(down));
        tbs.font = font;
        s.add("default", tbs);

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = font;
        s.add("default", ls);
        return s;
    }

    private Texture makeTex(int w,int h,float r,float g,float b,float a){
        Pixmap p = new Pixmap(w,h, Pixmap.Format.RGBA8888);
        p.setColor(r,g,b,a);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    private void buildUi() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label title = new Label("Levels", skin);
        root.add(title).pad(20f).colspan(3);
        root.row();

        // 3x2 grid = 6 levels
        int cols = 3;
        int rows = 2;
        int level = 1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                final int levelIndex = level;
                TextButton btn = new TextButton("Level " + level, skin);
                // Ensure each button has its own style instance to avoid mutating the shared Skin style
                TextButton.TextButtonStyle styleCopy = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
                if (LevelProgress.isCompleted(worldIndex, levelIndex)) {
                    btn.getLabel().setColor(Color.WHITE);
                    styleCopy.up = new TextureRegionDrawable(new TextureRegion(greenUpTexture));
                    styleCopy.down = new TextureRegionDrawable(new TextureRegion(greenDownTexture));
                }
                btn.setStyle(styleCopy);
                btn.addListener(new InputListener(){
                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        LevelDefinition def = LevelFactory.getLevel(worldIndex, levelIndex);
                        game.setScreen(new GameScreen(game, def, worldIndex, levelIndex, multiplayerManager));
                        dispose();
                        return true;
                    }
                });
                root.add(btn).width(200).height(110).pad(12f);
                level++;
            }
            root.row();
        }

        // Add translucent grey back chevron icon at top-left
        backIconTexture = createBackIconTexture(80, 80, 0.6f, 0.6f, 0.6f, 0.6f);
        ImageButton backIcon = new ImageButton(new TextureRegionDrawable(new TextureRegion(backIconTexture)));
        backIcon.setSize(80, 80);
        backIcon.setPosition(20, Gdx.graphics.getHeight() - backIcon.getHeight() - 20);
        backIcon.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new WorldsScreen(game));
                dispose();
                return true;
            }
        });
        stage.addActor(backIcon);
    }

    @Override public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width,height,true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backIconTexture != null) backIconTexture.dispose();
        if (greenUpTexture != null) greenUpTexture.dispose();
        if (greenDownTexture != null) greenDownTexture.dispose();
    }

    // Local helper to generate chevron '<' icon
    private Texture createBackIconTexture(int width, int height, float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0f);
        pixmap.fill();
        pixmap.setColor(r, g, b, a);
        int thickness = Math.max(10, width / 8);
        int margin = Math.max(8, width / 10);
        int xRight = width - margin;
        int xLeft = margin;
        int yTop = margin;
        int yMid = height / 2;
        int yBot = height - margin;
        for (int t = -thickness/2; t <= thickness/2; t++) {
            pixmap.drawLine(xRight, yTop + t, xLeft, yMid + t);
            pixmap.drawLine(xLeft, yMid + t, xRight, yBot + t);
        }
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    // InputProcessor methods for back button handling
    @Override
    public boolean keyDown(int keycode) {
        // Android back button is keycode 131 (Input.Keys.BACK)
        if (keycode == 131) {
            // From LevelsScreen, go to WorldsScreen
            game.setScreen(new WorldsScreen(game));
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


