package com.newgame.teamtilt.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.newgame.teamtilt.multiplayer.MultiplayerService;

/**
 * Manages toast messages for the game
 */
public class ToastManager implements Disposable {
    
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Array<ToastMessage> activeToasts = new Array<>();
    private float screenWidth, screenHeight;
    
    public ToastManager() {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        
        // Create font for toast messages
        com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator generator = 
            new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator(Gdx.files.internal("fonts/Matemasie-Regular.ttf"));
        com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter parameter = 
            new com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.WHITE;
        this.font = generator.generateFont(parameter);
        generator.dispose();
        
        updateScreenSize();
    }
    
    public void updateScreenSize() {
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
    }
    
    public void showToast(String message, MultiplayerService.ToastType type) {
        ToastMessage toast = new ToastMessage(message, type);
        activeToasts.add(toast);
        
        // Auto-remove after 3 seconds
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                activeToasts.removeValue(toast, true);
            }
        }, 3.0f);
    }
    
    public void render() {
        if (activeToasts.size == 0) return;
        
        // Update screen size if changed
        if (screenWidth != Gdx.graphics.getWidth() || screenHeight != Gdx.graphics.getHeight()) {
            updateScreenSize();
        }
        
        // Render toasts from top to bottom
        float yOffset = screenHeight - 100; // Start from top
        
        for (int i = 0; i < activeToasts.size; i++) {
            ToastMessage toast = activeToasts.get(i);
            renderToast(toast, yOffset);
            yOffset -= 60; // Space between toasts
        }
    }
    
    private void renderToast(ToastMessage toast, float y) {
        // Calculate text dimensions
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, toast.message);
        
        float padding = 20;
        float width = layout.width + padding * 2;
        float height = layout.height + padding * 2;
        float x = (screenWidth - width) / 2;
        
        // Get color based on type
        Color bgColor = getBackgroundColor(toast.type);
        
        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(bgColor.r, bgColor.g, bgColor.b, 0.8f);
        shapeRenderer.rect(x, y - height, width, height);
        shapeRenderer.end();
        
        // Draw text
        batch.begin();
        font.draw(batch, toast.message, x + padding, y - padding);
        batch.end();
    }
    
    private Color getBackgroundColor(MultiplayerService.ToastType type) {
        switch (type) {
            case SUCCESS:
                return new Color(0.2f, 0.8f, 0.2f, 1.0f); // Green
            case ERROR:
                return new Color(0.8f, 0.2f, 0.2f, 1.0f); // Red
            case WARNING:
                return new Color(0.8f, 0.6f, 0.2f, 1.0f); // Orange
            case INFO:
            default:
                return new Color(0.2f, 0.4f, 0.8f, 1.0f); // Blue
        }
    }
    
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
    
    private static class ToastMessage {
        String message;
        MultiplayerService.ToastType type;
        
        ToastMessage(String message, MultiplayerService.ToastType type) {
            this.message = message;
            this.type = type;
        }
    }
}
