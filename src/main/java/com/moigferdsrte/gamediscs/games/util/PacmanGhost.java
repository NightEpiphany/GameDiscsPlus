package com.moigferdsrte.gamediscs.games.util;

import com.moigferdsrte.gamediscs.games.math.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class PacmanGhost extends PacmanActor {
    private final int imageIndex;
    private final Vec2f home;
    private int respawnTicks;

    public PacmanGhost(int imageIndex, Vec2f home, float size, float speed) {
        super(size, speed);
        this.imageIndex = imageIndex;
        this.home = home;
        reset(home, PacmanDirection.RIGHT);
    }

    public void resetHome() {
        respawnTicks = 0;
        reset(home, PacmanDirection.RIGHT);
    }

    public void sendHome(int invulnerableTicks) {
        respawnTicks = Math.max(1, invulnerableTicks);
        reset(home, PacmanDirection.RIGHT);
    }

    public void tick(PacmanMaze maze, PacmanDistanceField distanceField, boolean weak, Random random) {
        if (respawnTicks > 0) {
            respawnTicks--;
            return;
        }
        if (maze.isNearTileCenter(center(), 0.45F)) {
            requestDirection(selectDirection(maze, distanceField, weak, random));
        }
        super.tick(maze);
    }

    public boolean isRespawning() {
        return respawnTicks > 0;
    }

    public int imageIndex() {
        return imageIndex;
    }

    private PacmanDirection selectDirection(PacmanMaze maze, PacmanDistanceField distanceField, boolean weak, Random random) {
        PacmanMaze.Tile current = maze.centerTile(center());
        List<PacmanDirection> choices = new ArrayList<>();
        for (PacmanDirection candidate : PacmanDirection.values()) {
            PacmanMaze.Tile next = nextTile(current, candidate);
            if (maze.canTravel(current, next)) {
                choices.add(candidate);
            }
        }
        if (choices.isEmpty()) {
            return direction().opposite();
        }
        if (choices.size() > 1) {
            choices.remove(direction().opposite());
        }

        int bestDistance = weak ? Integer.MIN_VALUE : PacmanDistanceField.UNREACHABLE;
        List<PacmanDirection> bestChoices = new ArrayList<>(choices.size());
        for (PacmanDirection choice : choices) {
            int distance = distanceField.distance(nextTile(current, choice));
            if (distance == PacmanDistanceField.UNREACHABLE) {
                continue;
            }
            boolean better = weak ? distance > bestDistance : distance < bestDistance;
            if (better) {
                bestDistance = distance;
                bestChoices.clear();
            }
            if (distance == bestDistance) {
                bestChoices.add(choice);
            }
        }
        if (bestChoices.isEmpty()) {
            return choices.get(random.nextInt(choices.size()));
        }
        return bestChoices.get(random.nextInt(bestChoices.size()));
    }

    private static PacmanMaze.Tile nextTile(PacmanMaze.Tile tile, PacmanDirection direction) {
        return new PacmanMaze.Tile(tile.x() + direction.x(), tile.y() + direction.y());
    }
}
