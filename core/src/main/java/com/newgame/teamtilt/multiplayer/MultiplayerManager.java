package com.newgame.teamtilt.multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Manages multiplayer functionality and player synchronization
 */
public class MultiplayerManager implements MultiplayerService.MultiplayerListener, Disposable {
    
    private MultiplayerService multiplayerService;
    private MultiplayerService.MultiplayerListener listener;
    private Array<MultiplayerPlayer> remotePlayers = new Array<>();
    private String localPlayerId;
    private boolean isHost = false;
    private long lastSyncTime = 0;
    private static final long SYNC_INTERVAL = 100; // Send updates every 100ms
    
    public MultiplayerManager(MultiplayerService service) {
        this.multiplayerService = service;
        multiplayerService.initialize(this);
    }
    
    /**
     * Set the listener for multiplayer events
     */
    public void setListener(MultiplayerService.MultiplayerListener listener) {
        this.listener = listener;
    }
    
    /**
     * Initialize multiplayer (sign in)
     */
    public void initialize() {
        multiplayerService.signIn();
    }
    
    /**
     * Create a new multiplayer room
     */
    public void createRoom(int maxPlayers) {
        isHost = true;
        multiplayerService.createRoom(maxPlayers);
    }
    
    /**
     * Join an existing room
     */
    public void joinRoom(String roomId) {
        isHost = false;
        multiplayerService.joinRoom(roomId);
    }
    
    /**
     * Leave current room
     */
    public void leaveRoom() {
        isHost = false;
        multiplayerService.leaveRoom();
        remotePlayers.clear();
    }
    
    /**
     * Update local player data and sync with other players
     */
    public void updatePlayerData(float x, float y, float velocityX, float velocityY, 
                                boolean isJumping, boolean isMovingLeft, boolean isMovingRight) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSyncTime >= SYNC_INTERVAL && multiplayerService.isInRoom()) {
            MultiplayerService.PlayerData playerData = new MultiplayerService.PlayerData(
                localPlayerId, x, y, velocityX, velocityY, isJumping, isMovingLeft, isMovingRight
            );
            multiplayerService.sendPlayerData(playerData);
            lastSyncTime = currentTime;
        }
    }
    
    /**
     * Get all remote players
     */
    public Array<MultiplayerPlayer> getRemotePlayers() {
        return remotePlayers;
    }
    
    /**
     * Check if currently in multiplayer mode
     */
    public boolean isMultiplayerActive() {
        return multiplayerService.isInRoom();
    }
    
    /**
     * Check if this player is the host
     */
    public boolean isHost() {
        return isHost;
    }
    
    /**
     * Get local player ID
     */
    public String getLocalPlayerId() {
        return localPlayerId;
    }
    
    /**
     * Get current room code
     */
    public String getCurrentRoomCode() {
        if (multiplayerService != null) {
            return multiplayerService.generateRoomCode();
        }
        return null;
    }
    
    // MultiplayerListener implementation
    @Override
    public void onSignInSuccess() {
        localPlayerId = multiplayerService.getPlayerId();
        Gdx.app.log("MultiplayerManager", "Signed in as: " + multiplayerService.getPlayerName());
    }
    
    @Override
    public void onSignInFailed(String error) {
        Gdx.app.error("MultiplayerManager", "Sign in failed: " + error);
    }
    
    @Override
    public void onSignOut() {
        localPlayerId = null;
        isHost = false;
        remotePlayers.clear();
        Gdx.app.log("MultiplayerManager", "Signed out");
    }
    
    @Override
    public void onRoomCreated(String roomId) {
        Gdx.app.log("MultiplayerManager", "Room created: " + roomId);
    }
    
    @Override
    public void onRoomJoined(String roomId) {
        Gdx.app.log("MultiplayerManager", "Joined room: " + roomId);
    }
    
    @Override
    public void onRoomLeft() {
        isHost = false;
        remotePlayers.clear();
        Gdx.app.log("MultiplayerManager", "Left room");
    }
    
    @Override
    public void onPlayerJoined(MultiplayerService.PlayerInfo player) {
        Gdx.app.log("MultiplayerManager", "Player joined: " + player.name);
        MultiplayerPlayer remotePlayer = new MultiplayerPlayer(player.id, player.name);
        remotePlayers.add(remotePlayer);
        
        // Show toast message
        if (listener != null) {
            listener.onToastMessage(player.name + " joined the game!", MultiplayerService.ToastType.SUCCESS);
        }
    }
    
    @Override
    public void onPlayerLeft(MultiplayerService.PlayerInfo player) {
        Gdx.app.log("MultiplayerManager", "Player left: " + player.name);
        remotePlayers.removeValue(new MultiplayerPlayer(player.id, player.name), false);
        
        // Show toast message
        if (listener != null) {
            listener.onToastMessage(player.name + " left the game", MultiplayerService.ToastType.INFO);
        }
    }
    
    @Override
    public void onMessageReceived(String playerId, String message) {
        Gdx.app.log("MultiplayerManager", "Message from " + playerId + ": " + message);
    }
    
    @Override
    public void onPlayerDataReceived(String playerId, MultiplayerService.PlayerData data) {
        // Update remote player data
        for (MultiplayerPlayer remotePlayer : remotePlayers) {
            if (remotePlayer.getId().equals(playerId)) {
                remotePlayer.updateFromData(data);
                break;
            }
        }
    }
    
    @Override
    public void onRoomCreationFailed(String error) {
        Gdx.app.error("MultiplayerManager", "Room creation failed: " + error);
    }
    
    @Override
    public void onJoinRoomFailed(String error) {
        Gdx.app.error("MultiplayerManager", "Join room failed: " + error);
        if (listener != null) {
            listener.onToastMessage("Failed to join room: " + error, MultiplayerService.ToastType.ERROR);
        }
    }
    
    @Override
    public void onRoomCodeGenerated(String roomCode) {
        Gdx.app.log("MultiplayerManager", "Room code generated: " + roomCode);
        if (listener != null) {
            listener.onRoomCodeGenerated(roomCode);
        }
    }
    
    @Override
    public void onToastMessage(String message, MultiplayerService.ToastType type) {
        Gdx.app.log("MultiplayerManager", "Toast: " + message + " (" + type + ")");
        if (listener != null) {
            listener.onToastMessage(message, type);
        }
    }
    
    @Override
    public void dispose() {
        if (multiplayerService != null) {
            multiplayerService.dispose();
        }
    }
    
    /**
     * Represents a remote player in multiplayer mode
     */
    public static class MultiplayerPlayer {
        private String id;
        private String name;
        private float x, y;
        private float velocityX, velocityY;
        private boolean isJumping;
        private boolean isMovingLeft, isMovingRight;
        private long lastUpdateTime;
        
        public MultiplayerPlayer(String id, String name) {
            this.id = id;
            this.name = name;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public void updateFromData(MultiplayerService.PlayerData data) {
            this.x = data.x;
            this.y = data.y;
            this.velocityX = data.velocityX;
            this.velocityY = data.velocityY;
            this.isJumping = data.isJumping;
            this.isMovingLeft = data.isMovingLeft;
            this.isMovingRight = data.isMovingRight;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public float getX() { return x; }
        public float getY() { return y; }
        public float getVelocityX() { return velocityX; }
        public float getVelocityY() { return velocityY; }
        public boolean isJumping() { return isJumping; }
        public boolean isMovingLeft() { return isMovingLeft; }
        public boolean isMovingRight() { return isMovingRight; }
        public long getLastUpdateTime() { return lastUpdateTime; }
    }
}
