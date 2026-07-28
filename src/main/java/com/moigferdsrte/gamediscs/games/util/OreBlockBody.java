package com.moigferdsrte.gamediscs.games.util;

public final class OreBlockBody {
    private final int id;
    private final int tier;
    private final float halfSize;
    private final float inverseMass;
    float x;
    float y;
    float velocityX;
    float velocityY;
    float angle;
    float angularVelocity;
    int age;

    OreBlockBody(int id, int tier, float size, float x, float y) {
        this.id = id;
        this.tier = tier;
        this.halfSize = size * 0.5F;
        this.inverseMass = 1.0F / (size * size);
        this.x = x;
        this.y = y;
    }

    public int id() {
        return id;
    }

    public int tier() {
        return tier;
    }

    public float size() {
        return halfSize * 2.0F;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float angle() {
        return angle;
    }

    float inverseMass() {
        return inverseMass;
    }

    float extent() {
        return halfSize * (Math.abs((float) Math.cos(angle)) + Math.abs((float) Math.sin(angle)));
    }

    float projectedRadius(float axisX, float axisY) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float localX = Math.abs(axisX * cos + axisY * sin);
        float localY = Math.abs(axisX * -sin + axisY * cos);
        return halfSize * (localX + localY);
    }
}
