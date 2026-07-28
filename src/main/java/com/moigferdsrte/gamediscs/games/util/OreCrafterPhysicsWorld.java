package com.moigferdsrte.gamediscs.games.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class OreCrafterPhysicsWorld {
    private static final float GRAVITY = 0.28F;
    private static final float MAX_FALL_SPEED = 5.0F;
    private static final float MAX_ANGULAR_SPEED = 0.18F;
    private static final float RESTITUTION = 0.08F;
    private static final float FRICTION = 0.45F;
    private static final int SOLVER_ITERATIONS = 10;
    private static final int POSITION_ITERATIONS = 16;
    private static final float POSITION_SLOP = 0.02F;

    private final float left;
    private final float right;
    private final float floor;
    private final List<OreBlockBody> bodies = new ArrayList<>();
    private final List<OreBlockBody> bodyView = Collections.unmodifiableList(bodies);
    private int nextId;

    public OreCrafterPhysicsWorld(float left, float right, float floor) {
        this.left = left;
        this.right = right;
        this.floor = floor;
    }

    public void clear() {
        bodies.clear();
        nextId = 0;
    }

    public OreBlockBody spawn(int tier, float size, float x, float y) {
        OreBlockBody body = new OreBlockBody(nextId++, tier, size, clampX(x, size), y);
        bodies.add(body);
        return body;
    }

    public List<Collision> step() {
        for (OreBlockBody body : bodies) {
            body.velocityY = Math.min(MAX_FALL_SPEED, body.velocityY + GRAVITY);
            body.velocityX *= 0.995F;
            body.angularVelocity *= 0.985F;
            body.angularVelocity = Math.max(-MAX_ANGULAR_SPEED, Math.min(MAX_ANGULAR_SPEED, body.angularVelocity));
            body.x += body.velocityX;
            body.y += body.velocityY;
            body.angle = normalizeAngle(body.angle + body.angularVelocity);
            body.age++;
            resolveBounds(body);
        }

        List<Collision> contacts = new ArrayList<>();
        for (int iteration = 0; iteration < SOLVER_ITERATIONS; iteration++) {
            for (int i = 0; i < bodies.size(); i++) {
                for (int j = i + 1; j < bodies.size(); j++) {
                    Collision collision = detect(bodies.get(i), bodies.get(j));
                    if (collision != null) {
                        if (iteration == 0) {
                            contacts.add(collision);
                        }
                        resolve(collision);
                    }
                }
            }
            for (OreBlockBody body : bodies) {
                resolveBounds(body);
            }
        }
        separateResidualOverlaps();
        return contacts;
    }

    public OreBlockBody merge(Collision collision, int newTier, float newSize) {
        OreBlockBody first = collision.first();
        OreBlockBody second = collision.second();
        if (!bodies.contains(first) || !bodies.contains(second)) {
            return null;
        }

        float x = (first.x + second.x) * 0.5F;
        float y = (first.y + second.y) * 0.5F;
        float velocityX = (first.velocityX + second.velocityX) * 0.45F;
        float velocityY = Math.min(-0.35F, (first.velocityY + second.velocityY) * 0.35F);
        float angle = normalizeAngle((first.angle + second.angle) * 0.5F);
        float angularVelocity = (first.angularVelocity + second.angularVelocity) * 0.5F;

        bodies.remove(first);
        bodies.remove(second);
        OreBlockBody merged = spawn(newTier, newSize, x, y);
        merged.velocityX = velocityX;
        merged.velocityY = velocityY;
        merged.angle = angle;
        merged.angularVelocity = angularVelocity;
        return merged;
    }

    public List<OreBlockBody> bodies() {
        return bodyView;
    }

    public int bodyCount() {
        return bodies.size();
    }

    public boolean hasBodyAbove(float y, int minimumAge) {
        for (OreBlockBody body : bodies) {
            if (body.age >= minimumAge && body.y - body.extent() < y) {
                return true;
            }
        }
        return false;
    }

    public float clampX(float x, float size) {
        float radius = size * 0.5F;
        return Math.max(left + radius, Math.min(right - radius, x));
    }

    private void resolveBounds(OreBlockBody body) {
        float extent = body.extent();
        if (body.x - extent < left) {
            body.x = left + extent;
            if (body.velocityX < 0.0F) {
                body.velocityX *= -RESTITUTION;
                body.angularVelocity -= body.velocityY * 0.012F;
            }
        } else if (body.x + extent > right) {
            body.x = right - extent;
            if (body.velocityX > 0.0F) {
                body.velocityX *= -RESTITUTION;
                body.angularVelocity += body.velocityY * 0.012F;
            }
        }

        if (body.y + extent > floor) {
            body.y = floor - extent;
            if (body.velocityY > 0.0F) {
                body.velocityY = body.velocityY < 0.35F ? 0.0F : -body.velocityY * RESTITUTION;
                body.velocityX *= 0.84F;
                body.angularVelocity += body.velocityX * 0.008F;
            }
            if (Math.abs(body.velocityX) < 0.02F) {
                body.velocityX = 0.0F;
            }
            if (Math.abs(body.angularVelocity) < 0.002F) {
                body.angularVelocity = 0.0F;
            }
        }
    }

    private static Collision detect(OreBlockBody first, OreBlockBody second) {
        float deltaX = second.x - first.x;
        float deltaY = second.y - first.y;
        float maxDistance = first.extent() + second.extent();
        if (Math.abs(deltaX) > maxDistance || Math.abs(deltaY) > maxDistance) {
            return null;
        }

        float firstCos = (float) Math.cos(first.angle);
        float firstSin = (float) Math.sin(first.angle);
        float secondCos = (float) Math.cos(second.angle);
        float secondSin = (float) Math.sin(second.angle);
        float[][] axes = {
                {firstCos, firstSin}, {-firstSin, firstCos},
                {secondCos, secondSin}, {-secondSin, secondCos}
        };

        float minimumOverlap = Float.MAX_VALUE;
        float normalX = 0.0F;
        float normalY = 0.0F;
        for (float[] axis : axes) {
            float distance = deltaX * axis[0] + deltaY * axis[1];
            float overlap = first.projectedRadius(axis[0], axis[1])
                    + second.projectedRadius(axis[0], axis[1]) - Math.abs(distance);
            if (overlap <= 0.0F) {
                return null;
            }
            if (overlap < minimumOverlap) {
                minimumOverlap = overlap;
                float direction = distance < 0.0F ? -1.0F : 1.0F;
                normalX = axis[0] * direction;
                normalY = axis[1] * direction;
            }
        }
        return new Collision(first, second, normalX, normalY, minimumOverlap);
    }

    private static void resolve(Collision collision) {
        OreBlockBody first = collision.first();
        OreBlockBody second = collision.second();
        float inverseMassSum = first.inverseMass() + second.inverseMass();
        float correction = Math.max(collision.penetration() - 0.01F, 0.0F) * 0.68F / inverseMassSum;
        first.x -= collision.normalX() * correction * first.inverseMass();
        first.y -= collision.normalY() * correction * first.inverseMass();
        second.x += collision.normalX() * correction * second.inverseMass();
        second.y += collision.normalY() * correction * second.inverseMass();

        float relativeX = second.velocityX - first.velocityX;
        float relativeY = second.velocityY - first.velocityY;
        float normalSpeed = relativeX * collision.normalX() + relativeY * collision.normalY();
        if (normalSpeed >= 0.0F) {
            return;
        }

        float impulse = -(1.0F + RESTITUTION) * normalSpeed / inverseMassSum;
        applyImpulse(first, second, collision.normalX() * impulse, collision.normalY() * impulse);

        float deltaX = second.x - first.x;
        float deltaY = second.y - first.y;
        float tangentOffset = deltaX * -collision.normalY() + deltaY * collision.normalX();
        float impactSpin = tangentOffset * impulse * inverseMassSum * 0.008F;
        first.angularVelocity += impactSpin;
        second.angularVelocity += impactSpin;

        relativeX = second.velocityX - first.velocityX;
        relativeY = second.velocityY - first.velocityY;
        float tangentX = relativeX - (relativeX * collision.normalX() + relativeY * collision.normalY()) * collision.normalX();
        float tangentY = relativeY - (relativeX * collision.normalX() + relativeY * collision.normalY()) * collision.normalY();
        float tangentLength = (float) Math.sqrt(tangentX * tangentX + tangentY * tangentY);
        if (tangentLength > 1.0E-4F) {
            tangentX /= tangentLength;
            tangentY /= tangentLength;
            float tangentImpulse = -(relativeX * tangentX + relativeY * tangentY) / inverseMassSum;
            float limit = impulse * FRICTION;
            tangentImpulse = Math.max(-limit, Math.min(limit, tangentImpulse));
            applyImpulse(first, second, tangentX * tangentImpulse, tangentY * tangentImpulse);
            float spin = tangentImpulse * 0.0035F;
            first.angularVelocity -= spin;
            second.angularVelocity += spin;
        }
    }

    private void separateResidualOverlaps() {
        for (int iteration = 0; iteration < POSITION_ITERATIONS; iteration++) {
            boolean corrected = false;
            for (int i = 0; i < bodies.size(); i++) {
                for (int j = i + 1; j < bodies.size(); j++) {
                    Collision collision = detect(bodies.get(i), bodies.get(j));
                    if (collision == null || collision.penetration() <= POSITION_SLOP) {
                        continue;
                    }
                    separate(collision);
                    corrected = true;
                }
            }
            if (!corrected) {
                return;
            }
        }
    }

    private void separate(Collision collision) {
        OreBlockBody first = collision.first();
        OreBlockBody second = collision.second();
        float distance = collision.penetration() + POSITION_SLOP;
        boolean firstBlocked = blockedByBoundary(first, -collision.normalX(), -collision.normalY());
        boolean secondBlocked = blockedByBoundary(second, collision.normalX(), collision.normalY());

        float firstShare = firstBlocked && !secondBlocked ? 0.0F : secondBlocked && !firstBlocked ? 1.0F : 0.5F;
        float secondShare = 1.0F - firstShare;
        first.x -= collision.normalX() * distance * firstShare;
        first.y -= collision.normalY() * distance * firstShare;
        second.x += collision.normalX() * distance * secondShare;
        second.y += collision.normalY() * distance * secondShare;
        resolveBounds(first);
        resolveBounds(second);
    }

    private boolean blockedByBoundary(OreBlockBody body, float directionX, float directionY) {
        float extent = body.extent();
        return directionX < 0.0F && body.x - extent <= left + POSITION_SLOP
                || directionX > 0.0F && body.x + extent >= right - POSITION_SLOP
                || directionY > 0.0F && body.y + extent >= floor - POSITION_SLOP;
    }

    private static void applyImpulse(OreBlockBody first, OreBlockBody second, float impulseX, float impulseY) {
        first.velocityX -= impulseX * first.inverseMass();
        first.velocityY -= impulseY * first.inverseMass();
        second.velocityX += impulseX * second.inverseMass();
        second.velocityY += impulseY * second.inverseMass();
    }

    private static float normalizeAngle(float angle) {
        float fullTurn = (float) (Math.PI * 2.0);
        angle %= fullTurn;
        return angle < -Math.PI ? angle + fullTurn : angle > Math.PI ? angle - fullTurn : angle;
    }

    public record Collision(OreBlockBody first, OreBlockBody second, float normalX, float normalY, float penetration) {
    }
}
