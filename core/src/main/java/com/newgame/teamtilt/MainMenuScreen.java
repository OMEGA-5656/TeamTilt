package com.newgame.teamtilt;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class MainMenuScreen implements Screen {
    final TeamTiltMain game;
    private BitmapFont font;
    private GlyphLayout layout;
    private SpriteBatch batch;

    public MainMenuScreen(final TeamTiltMain game) {
        this.game = game;
        batch = new SpriteBatch();
        layout = new GlyphLayout();

        // Load custom font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Matemasie-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 36; // Set the desired font size
        font = generator.generateFont(parameter); // Create the font
        generator.dispose(); // Dispose generator when done
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        String text = "TeamTilt \nTap to Start!";

        layout.setText(font, text);
        float textWidth = layout.width;
        float textHeight = layout.height;


        //centering text horizontally and veritcally
        // Calculate position with padding
        float x = (Gdx.graphics.getWidth() - textWidth) / 2;
        float y = (Gdx.graphics.getHeight() + textHeight) / 2;

        //test
        System.out.println("text height is- "+textHeight);
        System.out.println("text width is- "+textWidth);
        System.out.println("Height of your phone is- "+Gdx.graphics.getHeight());
        System.out.println("Width of your phone is- "+Gdx.graphics.getWidth());

        font.draw(batch, text, x, y);
        batch.end();

        if (Gdx.input.isTouched()) {
            // Switch to the game screen
            game.setScreen(new GameScreen(game));
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
        font.dispose();
    }
}
