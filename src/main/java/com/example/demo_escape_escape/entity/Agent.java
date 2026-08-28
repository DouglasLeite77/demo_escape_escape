package com.example.demo_escape_escape.entity;

import com.example.demo_escape_escape.ai.pathfinding.GridNode;
import com.example.demo_escape_escape.world.TileMap;

import java.util.List;

public class Agent {

    private static final double SIZE = 28;
    private static final double SPEED = 100;

    private double x;
    private double y;

    private int pathIndex = 1;

    public Agent(int startRow, int startColumn) {

        this.x = startColumn * TileMap.TILE_SIZE
                + (TileMap.TILE_SIZE - SIZE) / 2;

        this.y = startRow * TileMap.TILE_SIZE
                + (TileMap.TILE_SIZE - SIZE) / 2;
    }

    public void update(double deltaTime, List<GridNode> path) {

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

        // Chegou neste ponto do caminho
        if (distance <= movement) {

            x = targetX;
            y = targetY;

            pathIndex++;

            return;
        }

        // Move em direção ao próximo ponto
        x += (deltaX / distance) * movement;
        y += (deltaY / distance) * movement;
    }

    public boolean reachedDestination(List<GridNode> path) {
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
}