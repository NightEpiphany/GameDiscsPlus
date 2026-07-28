package com.moigferdsrte.gamediscs.games.util;

import com.moigferdsrte.gamediscs.games.math.Vec2f;

public class PacmanActor {
    private Vec2f center = Vec2f.ZERO;
    private PacmanDirection direction = PacmanDirection.RIGHT;
    private PacmanDirection requestedDirection = PacmanDirection.RIGHT;
    private final float size;
    private final float speed;

    public PacmanActor(float size, float speed) {
        this.size = size;
        this.speed = speed;
    }

    public void reset(Vec2f center, PacmanDirection direction) {
        this.center = center;
        this.direction = direction;
        this.requestedDirection = direction;
    }

    public void requestDirection(PacmanDirection direction) {
        this.requestedDirection = direction;
    }

    public void tick(PacmanMaze maze) {
        if (tryMove(maze, requestedDirection)) {
            direction = requestedDirection;
        } else if (!tryMove(maze, direction)) {
            alignToTile(maze);
        }
    }

    protected boolean tryMove(PacmanMaze maze, PacmanDirection moveDirection) {
        Vec2f next = center.add(new Vec2f(moveDirection.x() * speed, moveDirection.y() * speed));
        if (!maze.canMove(center, next, size)) {
            return false;
        }
        center = next;
        return true;
    }

    protected void alignToTile(PacmanMaze maze) {
        Vec2f tileCenter = maze.tileCenter(maze.centerTile(center));
        if (direction.x() == 0) {
            center = new Vec2f(tileCenter.x, center.y);
        }
        if (direction.y() == 0) {
            center = new Vec2f(center.x, tileCenter.y);
        }
    }

    public boolean intersects(PacmanActor other) {
        float radius = (size + other.size) * 0.35F;
        return center.distanceSquared(other.center) <= radius * radius;
    }

    public Vec2f center() {
        return center;
    }

    protected void setCenter(Vec2f center) {
        this.center = center;
    }

    public PacmanDirection direction() {
        return direction;
    }

    protected void setDirection(PacmanDirection direction) {
        this.direction = direction;
    }

    public float size() {
        return size;
    }
}
