package com.newgame.teamtilt.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import java.util.Random;

/**
 * Desktop stub implementation of MultiplayerService for development
 * This provides mock functionality when running on desktop
 */
public class DesktopMultiplayerService implements MultiplayerService {
    
    private MultiplayerListener listener;
    private boolean isSignedIn = false;
    private String playerId;
    private String playerName;
    private String currentRoomId;
    private String currentRoomCode;
    private Array<PlayerInfo> players = new Array<>();
    private Random random = new Random();
    
    @Override
    public void initialize(MultiplayerListener listener) {
        this.listener = listener;
        Gdx.app.log("DesktopMultiplayer", "Multiplayer service initialized (desktop stub)");
    }
    
    @Override
    public void signIn() {
        Gdx.app.postRunnable(() -> {
            isSignedIn = true;
            playerId = "desktop_player_" + System.currentTimeMillis();
            playerName = "Desktop Player";
            
            if (listener != null) {
                listener.onSignInSuccess();
            }
        });
    }
    
    @Override
    public void signOut() {
        isSignedIn = false;
        playerId = null;
        playerName = null;
        currentRoomId = null;
        players.clear();
        
        if (listener != null) {
            listener.onSignOut();
        }
    }
    
    @Override
    public boolean isSignedIn() {
        return isSignedIn;
    }
    
    @Override
    public String getPlayerName() {
        return playerName;
    }
    
    @Override
    public String getPlayerId() {
        return playerId;
    }
    
    @Override
    public void createRoom(int maxPlayers) {
        if (!isSignedIn) {
            if (listener != null) {
                listener.onRoomCreationFailed("Not signed in");
                listener.onToastMessage("Failed to create room: Not signed in", MultiplayerService.ToastType.ERROR);
            }
            return;
        }
        
        Gdx.app.postRunnable(() -> {
            currentRoomId = "desktop_room_" + System.currentTimeMillis();
            currentRoomCode = generateRoomCode();
            players.clear();
            players.add(new PlayerInfo(playerId, playerName, true));
            
            if (listener != null) {
                listener.onRoomCreated(currentRoomId);
                listener.onRoomJoined(currentRoomId);
                listener.onRoomCodeGenerated(currentRoomCode);
                listener.onToastMessage("Room created! Code: " + currentRoomCode, MultiplayerService.ToastType.SUCCESS);
            }
        });
    }
    
    @Override
    public void joinRoom(String roomCode) {
        if (!isSignedIn) {
            if (listener != null) {
                listener.onJoinRoomFailed("Not signed in");
                listener.onToastMessage("Failed to join room: Not signed in", MultiplayerService.ToastType.ERROR);
            }
            return;
        }
        
        if (roomCode == null || roomCode.length() != 6) {
            if (listener != null) {
                listener.onJoinRoomFailed("Invalid room code");
                listener.onToastMessage("Invalid room code format", MultiplayerService.ToastType.ERROR);
            }
            return;
        }
        
        Gdx.app.postRunnable(() -> {
            // For demo purposes, accept any 6-digit code
            currentRoomId = "room_" + roomCode;
            currentRoomCode = roomCode;
            players.clear();
            players.add(new PlayerInfo(playerId, playerName, false));
            
            if (listener != null) {
                listener.onRoomJoined(currentRoomId);
                listener.onToastMessage("Joined room with code: " + roomCode, MultiplayerService.ToastType.SUCCESS);
            }
        });
    }
    
    @Override
    public void leaveRoom() {
        currentRoomId = null;
        currentRoomCode = null;
        players.clear();
        
        if (listener != null) {
            listener.onRoomLeft();
            listener.onToastMessage("Left room", MultiplayerService.ToastType.INFO);
        }
    }
    
    @Override
    public String generateRoomCode() {
        if (currentRoomCode != null) {
            return currentRoomCode;
        }
        // Generate a 6-digit room code
        return String.format("%06d", random.nextInt(1000000));
    }
    
    @Override
    public void sendMessage(String message) {
        Gdx.app.log("DesktopMultiplayer", "Mock message sent: " + message);
        // Simulate receiving the message
        if (listener != null && currentRoomId != null) {
            Gdx.app.postRunnable(() -> {
                listener.onMessageReceived(playerId, message);
            });
        }
    }
    
    @Override
    public void sendPlayerData(PlayerData playerData) {
        Gdx.app.log("DesktopMultiplayer", "Mock player data sent for: " + playerData.playerId);
        // Simulate receiving the data
        if (listener != null && currentRoomId != null) {
            Gdx.app.postRunnable(() -> {
                listener.onPlayerDataReceived(playerData.playerId, playerData);
            });
        }
    }
    
    @Override
    public Array<PlayerInfo> getPlayers() {
        return new Array<>(players);
    }
    
    @Override
    public boolean isInRoom() {
        return currentRoomId != null;
    }
    
    @Override
    public String getCurrentRoomId() {
        return currentRoomId;
    }
    
    @Override
    public void dispose() {
        leaveRoom();
        signOut();
    }
}
