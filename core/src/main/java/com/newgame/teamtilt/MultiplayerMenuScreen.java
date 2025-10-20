package com.newgame.teamtilt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.InputProcessor;
import com.newgame.teamtilt.multiplayer.MultiplayerManager;
import com.newgame.teamtilt.multiplayer.MultiplayerService;
import com.newgame.teamtilt.ui.ToastManager;

/**
 * Multiplayer menu screen for creating/joining rooms
 */
public class MultiplayerMenuScreen implements Screen, InputProcessor, MultiplayerService.MultiplayerListener {
    final TeamTiltMain game;
    private BitmapFont titleFont, buttonFont;
    private GlyphLayout layoutTitle, layoutButton;
    private ShapeRenderer shapeRenderer;
    
    // Button properties
    private float createRoomButtonX, createRoomButtonY, createRoomButtonWidth, createRoomButtonHeight;
    private float joinRoomButtonX, joinRoomButtonY, joinRoomButtonWidth, joinRoomButtonHeight;
    private float backButtonX, backButtonY, backButtonWidth, backButtonHeight;
    
    // Room code input
    private String roomCodeInput = "";
    private boolean isTypingRoomCode = false;
    private boolean waitingForKeyboardInput = false;
    private float roomCodeInputX, roomCodeInputY, roomCodeInputWidth, roomCodeInputHeight;
    
    private MultiplayerManager multiplayerManager;
    private ToastManager toastManager;
    private String currentRoomCode;

    public MultiplayerMenuScreen(TeamTiltMain game, MultiplayerManager multiplayerManager) {
        this.game = game;
        this.multiplayerManager = multiplayerManager;
        
        // Initialize fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Matemasie-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        
        // Title font
        parameter.size = 48;
        titleFont = generator.generateFont(parameter);
        titleFont.setColor(Color.BLACK);
        
        // Button font
        parameter.size = 24;
        buttonFont = generator.generateFont(parameter);
        buttonFont.setColor(Color.WHITE);
        
        generator.dispose();
        
        // Initialize layout objects
        layoutTitle = new GlyphLayout();
        layoutButton = new GlyphLayout();
        
        // Initialize shape renderer
        shapeRenderer = new ShapeRenderer();
        
        // Initialize toast manager
        toastManager = new ToastManager();
        
        // Set button text and calculate positions
        layoutTitle.setText(titleFont, "Multiplayer");
        layoutButton.setText(buttonFont, "Create Room");
        
        float centerX = Gdx.graphics.getWidth() / 2;
        float centerY = Gdx.graphics.getHeight() / 2;
        
        // Button dimensions
        createRoomButtonWidth = layoutButton.width + 40;
        createRoomButtonHeight = layoutButton.height + 20;
        createRoomButtonX = centerX - createRoomButtonWidth / 2;
        createRoomButtonY = centerY + 50;
        
        layoutButton.setText(buttonFont, "Join Room");
        joinRoomButtonWidth = layoutButton.width + 40;
        joinRoomButtonHeight = layoutButton.height + 20;
        joinRoomButtonX = centerX - joinRoomButtonWidth / 2;
        joinRoomButtonY = centerY - 30;
        
        // Room code input field
        roomCodeInputWidth = 200;
        roomCodeInputHeight = 40;
        roomCodeInputX = centerX - roomCodeInputWidth / 2;
        roomCodeInputY = centerY - 80;
        
        layoutButton.setText(buttonFont, "Back");
        backButtonWidth = layoutButton.width + 40;
        backButtonHeight = layoutButton.height + 20;
        backButtonX = 30;
        backButtonY = Gdx.graphics.getHeight() - 30;
        
        // Set this screen as the input processor for back button handling
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
        
        // Initialize multiplayer
        if (multiplayerManager != null) {
            multiplayerManager.initialize();
            multiplayerManager.setListener(this);
        }
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        String titleText = "Multiplayer";
        String createRoomText = "Create Room";
        String joinRoomText = "Join Room";
        String backText = "Back";

        layoutTitle.setText(titleFont, titleText);
        layoutButton.setText(buttonFont, createRoomText);

        // Center positions
        float centerX = Gdx.graphics.getWidth() / 2;
        float centerY = Gdx.graphics.getHeight() / 2;

        float titleY = centerY + 100;

        // Draw title
        game.batch.begin();
        titleFont.draw(game.batch, titleText, centerX - layoutTitle.width / 2, titleY);
        game.batch.end();

        // Draw buttons
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        // Create Room button
        shapeRenderer.setColor(0.2f, 0.6f, 1.0f, 1.0f); // Blue
        shapeRenderer.rect(createRoomButtonX, createRoomButtonY - createRoomButtonHeight, 
                          createRoomButtonWidth, createRoomButtonHeight);
        
        // Join Room button
        shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1.0f); // Green
        shapeRenderer.rect(joinRoomButtonX, joinRoomButtonY - joinRoomButtonHeight, 
                          joinRoomButtonWidth, joinRoomButtonHeight);
        
        // Room code input field
        if (waitingForKeyboardInput) {
            shapeRenderer.setColor(0.7f, 0.9f, 1.0f, 1.0f); // Light blue when active
        } else {
            shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 1.0f); // Light gray when inactive
        }
        shapeRenderer.rect(roomCodeInputX, roomCodeInputY - roomCodeInputHeight, 
                          roomCodeInputWidth, roomCodeInputHeight);
        
        // Back button
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1.0f); // Red
        shapeRenderer.rect(backButtonX, backButtonY - backButtonHeight, 
                          backButtonWidth, backButtonHeight);
        
        shapeRenderer.end();

        // Draw button text
        game.batch.begin();
        
        // Create Room text
        layoutButton.setText(buttonFont, createRoomText);
        buttonFont.draw(game.batch, createRoomText, 
                       createRoomButtonX + (createRoomButtonWidth - layoutButton.width) / 2,
                       createRoomButtonY - (createRoomButtonHeight - layoutButton.height) / 2);
        
        // Join Room text
        layoutButton.setText(buttonFont, joinRoomText);
        buttonFont.draw(game.batch, joinRoomText,
                       joinRoomButtonX + (joinRoomButtonWidth - layoutButton.width) / 2,
                       joinRoomButtonY - (joinRoomButtonHeight - layoutButton.height) / 2);
        
        // Back text
        layoutButton.setText(buttonFont, backText);
        buttonFont.draw(game.batch, backText,
                       backButtonX + (backButtonWidth - layoutButton.width) / 2,
                       backButtonY - (backButtonHeight - layoutButton.height) / 2);
        
        // Room code input text
        String displayText = roomCodeInput.isEmpty() ? "Enter 6-digit code" : roomCodeInput;
        layoutButton.setText(buttonFont, displayText);
        if (waitingForKeyboardInput) {
            buttonFont.setColor(Color.BLACK);
        } else {
            buttonFont.setColor(roomCodeInput.isEmpty() ? Color.GRAY : Color.BLACK);
        }
        buttonFont.draw(game.batch, displayText,
                       roomCodeInputX + (roomCodeInputWidth - layoutButton.width) / 2,
                       roomCodeInputY - (roomCodeInputHeight - layoutButton.height) / 2);
        
        // Add cursor when typing
        if (waitingForKeyboardInput) {
            float cursorX = roomCodeInputX + (roomCodeInputWidth - layoutButton.width) / 2 + layoutButton.width + 5;
            float cursorY = roomCodeInputY - (roomCodeInputHeight - layoutButton.height) / 2;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(cursorX, cursorY - 20, 2, 20);
            shapeRenderer.end();
        }
        
        buttonFont.setColor(Color.WHITE); // Reset to white
        
        // Room code display (if host)
        if (currentRoomCode != null) {
            String roomCodeText = "Room Code: " + currentRoomCode;
            layoutButton.setText(buttonFont, roomCodeText);
            buttonFont.setColor(Color.BLACK);
            buttonFont.draw(game.batch, roomCodeText,
                           centerX - layoutButton.width / 2,
                           centerY + 120);
            buttonFont.setColor(Color.WHITE); // Reset to white
        }
        
        game.batch.end();
        
        // No custom keypad - using native keyboard
        
        // Render toast messages
        toastManager.render();

        // Check touch events
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
            
            // No custom keypad handling - using native keyboard

            // Check if "Create Room" is clicked
            if (touchX >= createRoomButtonX && touchX <= createRoomButtonX + createRoomButtonWidth &&
                touchY >= createRoomButtonY - createRoomButtonHeight && touchY <= createRoomButtonY) {
                if (multiplayerManager != null) {
                    multiplayerManager.createRoom(2); // Create room for 2 players
                    // Wait for room creation callback before navigating
                }
            }

            // Check if room code input field is clicked
            if (touchX >= roomCodeInputX && touchX <= roomCodeInputX + roomCodeInputWidth &&
                touchY >= roomCodeInputY - roomCodeInputHeight && touchY <= roomCodeInputY) {
                // Show native Android keyboard
                showNativeKeyboard();
                return;
            } else if (waitingForKeyboardInput) {
                // Clicked outside input field, hide keyboard
                hideNativeKeyboard();
            }

            // Check if "Join Room" is clicked
            if (touchX >= joinRoomButtonX && touchX <= joinRoomButtonX + joinRoomButtonWidth &&
                touchY >= joinRoomButtonY - joinRoomButtonHeight && touchY <= joinRoomButtonY) {
                if (multiplayerManager != null) {
                    if (roomCodeInput.length() == 6) {
                        multiplayerManager.joinRoom(roomCodeInput);
                        game.setScreen(new GameScreen(game, null, 1, 1, multiplayerManager));
                        dispose();
                    } else {
                        toastManager.showToast("Please enter a 6-digit room code", MultiplayerService.ToastType.WARNING);
                    }
                }
            }

            // Check if "Back" button is clicked
            if (touchX >= backButtonX && touchX <= backButtonX + backButtonWidth &&
                touchY >= backButtonY - backButtonHeight && touchY <= backButtonY) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
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
        buttonFont.dispose();
        shapeRenderer.dispose();
        if (toastManager != null) {
            toastManager.dispose();
        }
    }

    // InputProcessor methods for back button handling
    
    private void showNativeKeyboard() {
        waitingForKeyboardInput = true;
        isTypingRoomCode = true;
        // On Android, this will show the native keyboard
        Gdx.input.setOnscreenKeyboardVisible(true);
    }
    
    private void hideNativeKeyboard() {
        waitingForKeyboardInput = false;
        isTypingRoomCode = false;
        Gdx.input.setOnscreenKeyboardVisible(false);
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (waitingForKeyboardInput) {
            if (character == '\b') {
                // Backspace
                if (roomCodeInput.length() > 0) {
                    roomCodeInput = roomCodeInput.substring(0, roomCodeInput.length() - 1);
                }
                return true;
            } else if (character == '\r' || character == '\n') {
                // Enter - hide keyboard
                hideNativeKeyboard();
                return true;
            } else if (Character.isDigit(character) && roomCodeInput.length() < 6) {
                // Add digit
                roomCodeInput += character;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean keyDown(int keycode) {
        if (waitingForKeyboardInput) {
            // Handle numeric keys (0-9)
            if (keycode >= 7 && keycode <= 16) { // Keys 0-9
                if (roomCodeInput.length() < 6) {
                    roomCodeInput += (char)('0' + (keycode - 7));
                }
                return true;
            } else if (keycode == 67) { // Backspace key
                if (roomCodeInput.length() > 0) {
                    roomCodeInput = roomCodeInput.substring(0, roomCodeInput.length() - 1);
                }
                return true;
            } else if (keycode == 66) { // Enter key
                hideNativeKeyboard();
                return true;
            }
        }
        
        // Android back button is keycode 131 (Input.Keys.BACK)
        if (keycode == 131) {
            if (waitingForKeyboardInput) {
                hideNativeKeyboard();
                return true;
            }
            // From multiplayer menu, go to MainMenuScreen
            game.setScreen(new MainMenuScreen(game));
            dispose();
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
    // MultiplayerService.MultiplayerListener implementation
    @Override
    public void onSignInSuccess() {
        toastManager.showToast("Signed in successfully", MultiplayerService.ToastType.SUCCESS);
    }
    
    @Override
    public void onSignInFailed(String error) {
        toastManager.showToast("Sign in failed: " + error, MultiplayerService.ToastType.ERROR);
    }
    
    @Override
    public void onSignOut() {
        toastManager.showToast("Signed out", MultiplayerService.ToastType.INFO);
    }
    
    @Override
    public void onRoomCreated(String roomId) {
        // Room creation handled by onRoomCodeGenerated
    }
    
    @Override
    public void onRoomJoined(String roomId) {
        toastManager.showToast("Joined room successfully", MultiplayerService.ToastType.SUCCESS);
    }
    
    @Override
    public void onRoomLeft() {
        toastManager.showToast("Left room", MultiplayerService.ToastType.INFO);
    }
    
    @Override
    public void onPlayerJoined(MultiplayerService.PlayerInfo player) {
        toastManager.showToast(player.name + " joined the game!", MultiplayerService.ToastType.SUCCESS);
    }
    
    @Override
    public void onPlayerLeft(MultiplayerService.PlayerInfo player) {
        toastManager.showToast(player.name + " left the game", MultiplayerService.ToastType.INFO);
    }
    
    @Override
    public void onMessageReceived(String playerId, String message) {
        // Not used in this screen
    }
    
    @Override
    public void onPlayerDataReceived(String playerId, MultiplayerService.PlayerData data) {
        // Not used in this screen
    }
    
    @Override
    public void onRoomCreationFailed(String error) {
        toastManager.showToast("Room creation failed: " + error, MultiplayerService.ToastType.ERROR);
    }
    
    @Override
    public void onJoinRoomFailed(String error) {
        toastManager.showToast("Failed to join room: " + error, MultiplayerService.ToastType.ERROR);
    }
    
    @Override
    public void onRoomCodeGenerated(String roomCode) {
        currentRoomCode = roomCode;
        toastManager.showToast("Room created! Code: " + roomCode, MultiplayerService.ToastType.SUCCESS);
        
        // Navigate to level selection screen for multiplayer
        game.setScreen(new LevelsScreen(game, 1, multiplayerManager));
        dispose();
    }
    
    @Override
    public void onToastMessage(String message, MultiplayerService.ToastType type) {
        toastManager.showToast(message, type);
    }
}
