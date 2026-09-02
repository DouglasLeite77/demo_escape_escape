package com.example.demo_escape_escape.ai.pathfinding;

import com.example.demo_escape_escape.world.TileMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class AStarPathfinder {

    // Movimentos permitidos: cima, baixo, esquerda e direita
    private static final int[][] DIRECTIONS = {
            {-1, 0},
            {1, 0},
            {0, -1},
            {0, 1}
    };

    public List<GridNode> findPath(
            TileMap tileMap,
            int startRow,
            int startColumn,
            int goalRow,
            int goalColumn
    ) {

        // Verifica se início e destino são posições válidas
        if (!tileMap.isWalkable(startRow, startColumn)
                || !tileMap.isWalkable(goalRow, goalColumn)) {

            return Collections.emptyList();
        }

        GridNode[][] nodes =
                new GridNode[tileMap.getRows()][tileMap.getColumns()];

        for (int row = 0; row < tileMap.getRows(); row++) {
            for (int column = 0; column < tileMap.getColumns(); column++) {
                nodes[row][column] = new GridNode(row, column);
            }
        }

        PriorityQueue<GridNode> openSet =
                new PriorityQueue<>(
                        Comparator
                                .comparingInt(GridNode::getFCost)
                                .thenComparingInt(GridNode::getHCost)
                );

        boolean[][] closed =
                new boolean[tileMap.getRows()][tileMap.getColumns()];

        GridNode startNode = nodes[startRow][startColumn];

        startNode.setGCost(0);
        startNode.setHCost(
                heuristic(
                        startRow,
                        startColumn,
                        goalRow,
                        goalColumn
                )
        );

        openSet.add(startNode);

        while (!openSet.isEmpty()) {

            GridNode current = openSet.poll();

            // Encontrou o destino
            if (current.getRow() == goalRow
                    && current.getColumn() == goalColumn) {

                return reconstructPath(current);
            }

            closed[current.getRow()][current.getColumn()] = true;

            for (int[] direction : DIRECTIONS) {

                int neighborRow =
                        current.getRow() + direction[0];

                int neighborColumn =
                        current.getColumn() + direction[1];

                // Não permite sair do mapa
                if (!tileMap.isInside(neighborRow, neighborColumn)) {
                    continue;
                }

                // Não permite atravessar paredes
                if (!tileMap.isWalkable(neighborRow, neighborColumn)) {
                    continue;
                }

                // Nó já processado
                if (closed[neighborRow][neighborColumn]) {
                    continue;
                }

                GridNode neighbor =
                        nodes[neighborRow][neighborColumn];

                int movementCost =
                        tileMap.getMovementCost(
                                neighborRow,
                                neighborColumn
                        );

                int tentativeGCost =
                        current.getGCost() + movementCost;

                if (tentativeGCost < neighbor.getGCost()) {

                    neighbor.setParent(current);

                    neighbor.setGCost(tentativeGCost);

                    neighbor.setHCost(
                            heuristic(
                                    neighborRow,
                                    neighborColumn,
                                    goalRow,
                                    goalColumn
                            )
                    );

                    // Atualiza a prioridade do nó
                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
            }
        }

        // Nenhum caminho encontrado
        return Collections.emptyList();
    }

    private int heuristic(
            int row,
            int column,
            int goalRow,
            int goalColumn
    ) {

        // Distância de Manhattan
        return Math.abs(row - goalRow)
                + Math.abs(column - goalColumn);
    }

    private List<GridNode> reconstructPath(GridNode goalNode) {

        List<GridNode> path = new ArrayList<>();

        GridNode current = goalNode;

        while (current != null) {
            path.add(current);
            current = current.getParent();
        }

        Collections.reverse(path);

        return path;
    }
}