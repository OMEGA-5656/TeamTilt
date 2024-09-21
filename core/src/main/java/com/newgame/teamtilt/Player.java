package com.newgame.teamtilt;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player {
    private Body body;
    private Texture texture;
    private final float PPM = 100f; // Pixels per meter
    private final float speed = 2.5f;
    private final float startX = 150, startY = 320; // Starting position

    public Player(World world, Texture texture) {
        this.texture = texture;
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
        if (moveLeft) {
            body.setLinearVelocity(-speed, body.getLinearVelocity().y);
        }
        if (moveRight) {
            body.setLinearVelocity(speed, body.getLinearVelocity().y);
        }
    }

    public void jump() {
        body.applyLinearImpulse(new Vector2(0, 2f), body.getWorldCenter(), true);
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
}
