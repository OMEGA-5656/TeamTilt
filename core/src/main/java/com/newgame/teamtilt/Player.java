package com.newgame.teamtilt;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.newgame.teamtilt.multiplayer.MultiplayerService;

public class Player {
    private Body body;
    private Texture texture;
    private final float PPM = 100f; // Pixels per meter
    private final float speed = 2.5f;
    private final float startX = 150, startY = 320; // Starting position
    
    // Multiplayer support
    private String playerId;
    private boolean isLocalPlayer;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean isJumping = false;

    public Player(World world, Texture texture) {
        this.texture = texture;
        this.isLocalPlayer = true;
        createPlayerBody(world);
    }
    
    public Player(World world, Texture texture, String playerId) {
        this.texture = texture;
        this.playerId = playerId;
        this.isLocalPlayer = false;
        createPlayerBody(world);
    }

    private void createPlayerBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX / PPM, startY / PPM);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(texture.getWidth() / 2 / PPM, texture.getHeight() / 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 5f;

        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        shape.dispose();
    }

    public void updateMovement(boolean moveLeft, boolean moveRight) {
        this.isMovingLeft = moveLeft;
        this.isMovingRight = moveRight;
        
        if (moveLeft) {
            body.setLinearVelocity(-speed, body.getLinearVelocity().y);
        }
        if (moveRight) {
            body.setLinearVelocity(speed, body.getLinearVelocity().y);
        }
    }

    public void jump() {
        this.isJumping = true;
        body.applyLinearImpulse(new Vector2(0, 2f), body.getWorldCenter(), true);
    }
    
    /**
     * Update player from multiplayer data (for remote players)
     */
    public void updateFromMultiplayerData(MultiplayerService.PlayerData data) {
        if (isLocalPlayer) return; // Don't update local player from network data
        
        body.setTransform(data.x / PPM, data.y / PPM, body.getAngle());
        body.setLinearVelocity(data.velocityX, data.velocityY);
        this.isMovingLeft = data.isMovingLeft;
        this.isMovingRight = data.isMovingRight;
        this.isJumping = data.isJumping;
    }
    
    /**
     * Get current player data for multiplayer synchronization
     */
    public MultiplayerService.PlayerData getPlayerData() {
        Vector2 position = body.getPosition();
        Vector2 velocity = body.getLinearVelocity();
        
        return new MultiplayerService.PlayerData(
            playerId != null ? playerId : "local",
            position.x * PPM,
            position.y * PPM,
            velocity.x,
            velocity.y,
            isJumping,
            isMovingLeft,
            isMovingRight
        );
    }

    public boolean isFalling() {
        return body.getPosition().y < 0;
    }

    public void respawn() {
        body.setTransform(startX / PPM, startY / PPM, 0);
    }

    public Body getBody() {
        return body;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getPPM() {
        return PPM;
    }

    public float getSpeed() {
        return speed;
    }
    
    // Getters for multiplayer
    public String getPlayerId() {
        return playerId;
    }
    
    public boolean isLocalPlayer() {
        return isLocalPlayer;
    }
    
    public boolean isMovingLeft() {
        return isMovingLeft;
    }
    
    public boolean isMovingRight() {
        return isMovingRight;
    }
    
    public boolean isJumping() {
        return isJumping;
    }
    
    public void setJumping(boolean jumping) {
        this.isJumping = jumping;
    }
}
