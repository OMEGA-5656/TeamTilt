package com.newgame.teamtilt;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

public class MainMenuScreen implements Screen {
    final TeamTiltMain game;
    private BitmapFont titleFont, smallFont, buttonFont;
    private GlyphLayout layoutTitle, layoutSmall, layoutButton;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    // Button dimensions
    private float buttonX, buttonY, buttonWidth, buttonHeight;

    public MainMenuScreen(final TeamTiltMain game) {
        this.game = game;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        layoutTitle = new GlyphLayout();
        layoutSmall = new GlyphLayout();
        layoutButton = new GlyphLayout();

        // Load fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Matemasie-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParam.size = 72;
        titleFont = generator.generateFont(titleParam);

        FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParam.size = 30;
        smallFont = generator.generateFont(smallParam);

        FreeTypeFontGenerator.FreeTypeFontParameter buttonParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonParam.size = 60;
        buttonFont = generator.generateFont(buttonParam);
        buttonFont.setColor(Color.WHITE); // Make text white

        generator.dispose();

        // Set button text
        layoutButton.setText(buttonFont, "ONLINE");

        // Button positioning (top right corner with padding)
        buttonWidth = layoutButton.width + 40;
        buttonHeight = layoutButton.height + 20;
        buttonX = Gdx.graphics.getWidth() - buttonWidth - 30;
        buttonY = Gdx.graphics.getHeight() - 30; // Keep padding from the top
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String titleText = "TeamTilt";
        String startText = "Tap to Start!";

        layoutTitle.setText(titleFont, titleText);
        layoutSmall.setText(smallFont, startText);

        float textWidth = layoutTitle.width;
        float textHeight = layoutTitle.height;

        // Center positions
        float centerX = Gdx.graphics.getWidth() / 2;
        float centerY = Gdx.graphics.getHeight() / 2;

        float titleX = centerX - textWidth / 2;
        float titleY = centerY + textHeight / 2;

        float ribbonWidth = Gdx.graphics.getWidth(); // Full width
        float ribbonHeight = Gdx.graphics.getWidth() / 5;
        float ribbonX = centerX - ribbonWidth / 2;
        float ribbonY = titleY - textHeight / 2 - ribbonHeight / 2;

        // Draw gradient ribbon
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(ribbonX, ribbonY, ribbonWidth, ribbonHeight,
            Color.ORANGE, Color.GOLD, Color.GOLDENROD, Color.FIREBRICK);

        // Draw yellow button background
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(buttonX, buttonY - buttonHeight, buttonWidth, buttonHeight);
        shapeRenderer.end();

        // Increase line thickness
        Gdx.gl.glLineWidth(8); // Adjust the value to make it even thicker

        // Draw green border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        for (int i = 0; i < 3; i++) { // Draw multiple layers for extra thickness
            shapeRenderer.rect(buttonX - i, buttonY - buttonHeight - i, buttonWidth + (i * 2), buttonHeight + (i * 2));
        }
        shapeRenderer.end();

        // Reset line width
        Gdx.gl.glLineWidth(1);
        // Draw text
        batch.begin();
        titleFont.draw(batch, titleText, titleX, titleY);
        smallFont.draw(batch, startText, centerX - layoutSmall.width / 2, titleY - textHeight - 40);

        // Draw "ONLINE" text in white, centered on the button
        buttonFont.draw(batch, "ONLINE", buttonX + (buttonWidth - layoutButton.width) / 2, buttonY - (buttonHeight - layoutButton.height) / 2);
        batch.end();

        // Check touch events
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Check if "Tap to Start" is clicked
            float startX = centerX - layoutSmall.width / 2;
            float startY = titleY - textHeight - 40;
            if (touchX >= startX && touchX <= startX + layoutSmall.width &&
                touchY >= startY - layoutSmall.height && touchY <= startY) {
                game.setScreen(new GameScreen(game));
            }

            // Check if "ONLINE" button is clicked
            if (touchX >= buttonX && touchX <= buttonX + buttonWidth &&
                touchY >= buttonY - buttonHeight && touchY <= buttonY) {
                System.out.println("ONLINE button clicked!");
            }
        }
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
        titleFont.dispose();
        smallFont.dispose();
        buttonFont.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }
}
