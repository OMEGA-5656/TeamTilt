package com.newgame.teamtilt;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class InputHandler implements InputProcessor {
    private final Body playerBody;
    private final float speed;
    boolean moveLeft = false;
    boolean moveRight = false;
    private boolean isGrounded = false;

    public InputHandler(Body playerBody, float speed) {
        this.playerBody = playerBody;
        this.speed = speed;
    }

    public void updateMovement() {
        if (moveLeft) {
            playerBody.setLinearVelocity(-speed, playerBody.getLinearVelocity().y);
        }
        if (moveRight) {
            playerBody.setLinearVelocity(speed, playerBody.getLinearVelocity().y);
        }
    }

    public void jump() {
        if (isGrounded) {
            playerBody.applyLinearImpulse(new Vector2(0, 2f), playerBody.getWorldCenter(), true);
            isGrounded = false;
        }
    }

    public void setGrounded(boolean grounded) {
        this.isGrounded = grounded;
    }

    // Implement InputProcessor methods
    @Override
    public boolean keyDown(int keycode) {
        // Handle keyboard events if needed
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Detect touch on buttons
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

    public boolean scrolled(int amount) {
        return false;
    }

    public void stopMovement() {
        moveLeft = false;
        moveRight = false;
    }

    public void moveLeft() {
        moveLeft = true;
    }

    public void moveRight() {
        moveRight = true;
    }
}
