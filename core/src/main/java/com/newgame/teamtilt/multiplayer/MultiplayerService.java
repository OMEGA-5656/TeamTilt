package com.newgame.teamtilt.multiplayer;

import com.badlogic.gdx.utils.Array;

/**
 * Interface for multiplayer services (GPGS, etc.)
 * This allows the core game to work with different multiplayer backends
 */
public interface MultiplayerService {
    
    /**
     * Initialize the multiplayer service
     * @param listener Callback for multiplayer events
     */
    void initialize(MultiplayerListener listener);
    
    /**
     * Sign in to the service
     */
    void signIn();
    
    /**
     * Sign out of the service
     */
    void signOut();
    
    /**
     * Check if player is signed in
     * @return true if signed in
     */
    boolean isSignedIn();
    
    /**
     * Get the player's display name
     * @return Player name or null if not signed in
     */
    String getPlayerName();
    
    /**
     * Get the player's ID
     * @return Player ID or null if not signed in
     */
    String getPlayerId();
    
    /**
     * Create a new multiplayer room
     * @param maxPlayers Maximum number of players (2-4)
     */
    void createRoom(int maxPlayers);
    
    /**
     * Join an existing room by room code
     * @param roomCode Room code to join (6-digit code)
     */
    void joinRoom(String roomCode);
    
    /**
     * Generate a room code for the current room
     * @return 6-digit room code or null if not in room
     */
    String generateRoomCode();
    
    /**
     * Leave the current room
     */
    void leaveRoom();
    
    /**
     * Send a message to all players in the room
     * @param message Message to send
     */
    void sendMessage(String message);
    
    /**
     * Send player state data to other players
     * @param playerData Player state data
     */
    void sendPlayerData(PlayerData playerData);
    
    /**
     * Get list of players in current room
     * @return Array of player information
     */
    Array<PlayerInfo> getPlayers();
    
    /**
     * Check if currently in a room
     * @return true if in a room
     */
    boolean isInRoom();
    
    /**
     * Get the current room ID
     * @return Room ID or null if not in room
     */
    String getCurrentRoomId();
    
    /**
     * Dispose resources
     */
    void dispose();
    
    /**
     * Listener interface for multiplayer events
     */
    interface MultiplayerListener {
        void onSignInSuccess();
        void onSignInFailed(String error);
        void onSignOut();
        void onRoomCreated(String roomId);
        void onRoomJoined(String roomId);
        void onRoomLeft();
        void onPlayerJoined(PlayerInfo player);
        void onPlayerLeft(PlayerInfo player);
        void onMessageReceived(String playerId, String message);
        void onPlayerDataReceived(String playerId, PlayerData data);
        void onRoomCreationFailed(String error);
        void onJoinRoomFailed(String error);
        void onRoomCodeGenerated(String roomCode);
        void onToastMessage(String message, ToastType type);
    }
    
    /**
     * Types of toast messages
     */
    enum ToastType {
        INFO, SUCCESS, ERROR, WARNING
    }
    
    /**
     * Information about a player
     */
    class PlayerInfo {
        public String id;
        public String name;
        public boolean isHost;
        
        public PlayerInfo(String id, String name, boolean isHost) {
            this.id = id;
            this.name = name;
            this.isHost = isHost;
        }
    }
    
    /**
     * Player state data for synchronization
     */
    class PlayerData {
        public String playerId;
        public float x, y;
        public float velocityX, velocityY;
        public boolean isJumping;
        public boolean isMovingLeft, isMovingRight;
        public long timestamp;
        
        public PlayerData(String playerId) {
            this.playerId = playerId;
            this.timestamp = System.currentTimeMillis();
        }
        
        public PlayerData(String playerId, float x, float y, float velocityX, float velocityY, 
                         boolean isJumping, boolean isMovingLeft, boolean isMovingRight) {
            this.playerId = playerId;
            this.x = x;
            this.y = y;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.isJumping = isJumping;
            this.isMovingLeft = isMovingLeft;
            this.isMovingRight = isMovingRight;
            this.timestamp = System.currentTimeMillis();
        }
    }
}
