package com.moigferdsrte.gamediscs.games.math;

/**
 * 小游戏使用的不可变二维向量，避免依赖版本易变的客户端数学 API。
 */
public final class Vec2f {
    public static final Vec2f ZERO = new Vec2f(0.0F, 0.0F);
    public final float x;
    public final float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f multiply(float value) {
        return new Vec2f(x * value, y * value);
    }

    public float dot(Vec2f other) {
        return x * other.x + y * other.y;
    }

    public Vec2f add(Vec2f other) {
        return new Vec2f(x + other.x, y + other.y);
    }

    public Vec2f add(float value) {
        return new Vec2f(x + value, y + value);
    }

    public Vec2f normalize() {
        float length = length();
        return length < 1.0E-4F ? ZERO : new Vec2f(x / length, y / length);
    }

    public float length() {
        return (float) Math.sqrt(lengthSquared());
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public float distanceSquared(Vec2f other) {
        float dx = other.x - x;
        float dy = other.y - y;
        return dx * dx + dy * dy;
    }

    public Vec2f negate() {
        return new Vec2f(-x, -y);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Vec2f vec && x == vec.x && y == vec.y;
    }

    @Override
    public int hashCode() {
        return 31 * Float.hashCode(x) + Float.hashCode(y);
    }
}
