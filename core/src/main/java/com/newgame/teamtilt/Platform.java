package com.newgame.teamtilt;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.math.Vector2;

public class Platform {
    private Body body;
    private static final float PPM = 100;  // Pixels per meter for Box2D scaling

    public Platform(World world, float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((x + width / 2) / PPM, (y + height / 2) / PPM);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / PPM, height / 2 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 5f;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }
}
