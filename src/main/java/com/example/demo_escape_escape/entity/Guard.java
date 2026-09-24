package com.example.demo_escape_escape.entity;

import com.example.demo_escape_escape.ai.pathfinding.AStarPathfinder;
import com.example.demo_escape_escape.ai.pathfinding.GridNode;
import com.example.demo_escape_escape.ai.state.EnemyState;
import com.example.demo_escape_escape.world.TileMap;
import com.example.demo_escape_escape.ai.Noise;

import java.util.Collections;
import java.util.List;

public class Guard {

    private static final double SIZE = 50;
    private static final double SPEED = 80;
    private static final double VISION_RANGE = 160;
    private static final double INVESTIGATE_DURATION = 1.5;

    private double investigateTimer = 0;
    private double x = 20 * TileMap.TILE_SIZE;
    private double y = 2 * TileMap.TILE_SIZE;

    private int lastPlayerRow = -1;
    private int lastPlayerColumn = -1;
    private boolean playerVisible = false;

    private static final double SEARCH_DURATION = 2.0;

    private int lastSeenRow = -1;
    private int lastSeenColumn = -1;

    private double searchTimer = 0;

    private EnemyState state = EnemyState.PATROL;

    private final AStarPathfinder pathfinder = new AStarPathfinder();

    private List<GridNode> path = Collections.emptyList();
    private int pathIndex = 1;


    private final int[][] patrolPoints = {
            {2, 2},
            {2, 20},
            {14, 23},
            {16, 2}
    };

    private int patrolIndex = 1;

    public Guard(int startRow, int startColumn) {

        this.x = startColumn * TileMap.TILE_SIZE
                + (TileMap.TILE_SIZE - SIZE) / 2;

        this.y = startRow * TileMap.TILE_SIZE
                + (TileMap.TILE_SIZE - SIZE) / 2;
    }

    public void update(
            double deltaTime,
            TileMap tileMap,
            double playerX,
            double playerY,
            double playerSize,
            Noise noise
    ) {

        playerVisible = canSeePlayer(
                playerX,
                playerY,
                playerSize,
                tileMap
        );

        if (playerVisible) {

            lastSeenRow =
                    (int) (
                            (playerY + playerSize / 2)
                                    / TileMap.TILE_SIZE
                    );

            lastSeenColumn =
                    (int) (
                            (playerX + playerSize / 2)
                                    / TileMap.TILE_SIZE
                    );

            state = EnemyState.CHASE;

            searchTimer = 0;
            investigateTimer = 0;

        } else if (state == EnemyState.CHASE) {

            state = EnemyState.SEARCH;

            path = pathfinder.findPath(
                    tileMap,
                    getCurrentRow(),
                    getCurrentColumn(),
                    lastSeenRow,
                    lastSeenColumn
            );

            pathIndex = 1;

        } else if (state == EnemyState.PATROL
                && canHearNoise(noise)) {

            int noiseRow =
                    (int) (
                            noise.getY()
                                    / TileMap.TILE_SIZE
                    );

            int noiseColumn =
                    (int) (
                            noise.getX()
                                    / TileMap.TILE_SIZE
                    );

            path = pathfinder.findPath(
                    tileMap,
                    getCurrentRow(),
                    getCurrentColumn(),
                    noiseRow,
                    noiseColumn
            );

            pathIndex = 1;

            state = EnemyState.INVESTIGATE;
            investigateTimer = 0;
        }

        if (state == EnemyState.PATROL) {

            updatePatrol(
                    deltaTime,
                    tileMap
            );

        } else if (state == EnemyState.CHASE) {

            updateChase(
                    deltaTime,
                    tileMap,
                    playerX,
                    playerY,
                    playerSize
            );

        } else if (state == EnemyState.SEARCH) {

            updateSearch(
                    deltaTime
            );

        } else if (state == EnemyState.INVESTIGATE) {

            updateInvestigate(
                    deltaTime
            );
        }
    }

    private boolean canSeePlayer(
            double playerX,
            double playerY,
            double playerSize,
            TileMap tileMap
    ) {

        double guardCenterX = x + SIZE / 2;
        double guardCenterY = y + SIZE / 2;

        double playerCenterX = playerX + playerSize / 2;
        double playerCenterY = playerY + playerSize / 2;

        double deltaX = playerCenterX - guardCenterX;
        double deltaY = playerCenterY - guardCenterY;

        double distance =
                Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distance > VISION_RANGE) {
            return false;
        }

        int steps = (int) (distance / 8);

        if (steps <= 0) {
            return true;
        }

        for (int i = 1; i < steps; i++) {

            double progress =
                    (double) i / steps;

            double checkX =
                    guardCenterX + deltaX * progress;

            double checkY =
                    guardCenterY + deltaY * progress;

            int column =
                    (int) (checkX / TileMap.TILE_SIZE);

            int row =
                    (int) (checkY / TileMap.TILE_SIZE);

            if (!tileMap.isWalkable(row, column)) {
                return false;
            }
        }

        return true;
    }

    private void updatePatrol(double deltaTime, TileMap tileMap) {


        if (path.isEmpty() || pathIndex >= path.size()) {

            int targetRow = patrolPoints[patrolIndex][0];
            int targetColumn = patrolPoints[patrolIndex][1];

            path = pathfinder.findPath(
                    tileMap,
                    getCurrentRow(),
                    getCurrentColumn(),
                    targetRow,
                    targetColumn
            );

            pathIndex = 1;

            patrolIndex++;

            if (patrolIndex >= patrolPoints.length) {
                patrolIndex = 0;
            }
        }

        if (path.isEmpty() || pathIndex >= path.size()) {
            return;
        }

        moveAlongPath(deltaTime);
    }

    private void updateChase(
            double deltaTime,
            TileMap tileMap,
            double playerX,
            double playerY,
            double playerSize
    ) {

        int playerRow =
                (int) (
                        (playerY + playerSize / 2)
                                / TileMap.TILE_SIZE
                );

        int playerColumn =
                (int) (
                        (playerX + playerSize / 2)
                                / TileMap.TILE_SIZE
                );

        if (playerRow != lastPlayerRow
                || playerColumn != lastPlayerColumn
                || path.isEmpty()) {

            path = pathfinder.findPath(
                    tileMap,
                    getCurrentRow(),
                    getCurrentColumn(),
                    playerRow,
                    playerColumn
            );

            pathIndex = 1;

            lastPlayerRow = playerRow;
            lastPlayerColumn = playerColumn;
        }

        if (path.isEmpty()
                || pathIndex >= path.size()) {
            return;
        }

        moveAlongPath(deltaTime);
    }

    private void updateSearch(double deltaTime) {

        if (!path.isEmpty()
                && pathIndex < path.size()) {

            moveAlongPath(deltaTime);
            return;
        }

        searchTimer += deltaTime;

        if (searchTimer >= SEARCH_DURATION) {

            state = EnemyState.PATROL;

            path = Collections.emptyList();
            pathIndex = 1;

            searchTimer = 0;

            lastPlayerRow = -1;
            lastPlayerColumn = -1;
        }
    }

    private boolean canHearNoise(Noise noise) {

        if (!noise.isActive()) {
            return false;
        }

        double guardCenterX = x + SIZE / 2;
        double guardCenterY = y + SIZE / 2;

        double deltaX = noise.getX() - guardCenterX;
        double deltaY = noise.getY() - guardCenterY;

        double distance =
                Math.sqrt(
                        deltaX * deltaX
                                + deltaY * deltaY
                );

        return distance <= noise.getRadius();
    }

    private void updateInvestigate(double deltaTime) {

        if (!path.isEmpty()
                && pathIndex < path.size()) {

            moveAlongPath(deltaTime);
            return;
        }

        investigateTimer += deltaTime;

        if (investigateTimer >= INVESTIGATE_DURATION) {

            state = EnemyState.PATROL;

            path = Collections.emptyList();
            pathIndex = 1;

            investigateTimer = 0;
        }
    }

    private void moveAlongPath(double deltaTime) {

        GridNode targetNode = path.get(pathIndex);

        double targetX =
                targetNode.getColumn() * TileMap.TILE_SIZE
                        + (TileMap.TILE_SIZE - SIZE) / 2;

        double targetY =
                targetNode.getRow() * TileMap.TILE_SIZE
                        + (TileMap.TILE_SIZE - SIZE) / 2;

        double deltaX = targetX - x;
        double deltaY = targetY - y;

        double distance =
                Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double movement = SPEED * deltaTime;

        if (distance <= movement) {

            x = targetX;
            y = targetY;

            pathIndex++;

            return;
        }

        if (distance > 0) {

            x += (deltaX / distance) * movement;
            y += (deltaY / distance) * movement;
        }
    }

    public void reset() {

        x = 20 * TileMap.TILE_SIZE;
        y = 2 * TileMap.TILE_SIZE;

        state = EnemyState.PATROL;

        path.clear();
        pathIndex = 0;

        patrolIndex = 0;

        lastPlayerRow = -1;
        lastPlayerColumn = -1;
    }

    public int getCurrentRow() {

        return (int) (
                (y + SIZE / 2)
                        / TileMap.TILE_SIZE
        );
    }

    public int getCurrentColumn() {

        return (int) (
                (x + SIZE / 2)
                        / TileMap.TILE_SIZE
        );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return SIZE;
    }

    public EnemyState getState() {
        return state;
    }

    public List<GridNode> getPath() {
        return path;
    }

    public boolean isPlayerVisible() {
        return playerVisible;
    }
}