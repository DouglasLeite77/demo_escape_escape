	package com.example.demo_escape_escape.entity;

import com.example.demo_escape_escape.ai.pathfinding.AStarPathfinder;
import com.example.demo_escape_escape.ai.pathfinding.GridNode;
import com.example.demo_escape_escape.world.TileMap;

import java.util.Collections;
import java.util.List;

public class Agent {

    private static final double SIZE = 28;
    private static final double SPEED = 100;

    private double x;
    private double y;

    private int pathIndex = 1;

    private int goalRow = -1;
    private int goalColumn = -1;

    private int lastGoalRow = -1;
    private int lastGoalColumn = -1;

    private List<GridNode> path = Collections.emptyList();

    private final AStarPathfinder pathfinder = new AStarPathfinder();

    public Agent(int startRow, int startColumn) {

        this.x = startColumn * TileMap.TILE_SIZE
                + (TileMap.TILE_SIZE - SIZE) / 2;

        this.y = startRow * TileMap.TILE_SIZE
                + (TileMap.TILE_SIZE - SIZE) / 2;
    }

    public void setDestination(int row, int column) {

        this.goalRow = row;
        this.goalColumn = column;
    }

    public void update(double deltaTime, TileMap tileMap) {

        if (goalRow < 0 || goalColumn < 0) {
            return;
        }

        // Se o destino mudou, calcula uma nova rota
        if (goalRow != lastGoalRow
                || goalColumn != lastGoalColumn) {

            int currentRow = getCurrentRow();
            int currentColumn = getCurrentColumn();

            path = pathfinder.findPath(
                    tileMap,
                    currentRow,
                    currentColumn,
                    goalRow,
                    goalColumn
            );

            pathIndex = 1;

            lastGoalRow = goalRow;
            lastGoalColumn = goalColumn;
        }

        if (path == null || path.isEmpty()) {
            return;
        }

        if (pathIndex >= path.size()) {
            return;
        }

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

    public boolean reachedDestination() {

        return path != null
                && !path.isEmpty()
                && pathIndex >= path.size();
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

    public List<GridNode> getPath() {
        return path;
    }

    public int getGoalRow() {
        return goalRow;
    }

    public int getGoalColumn() {
        return goalColumn;
    }
}	