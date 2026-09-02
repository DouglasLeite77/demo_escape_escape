package com.example.demo_escape_escape.world;

public class TileMap {

    public static final int TILE_SIZE = 32;

    private final int[][] map = {

            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,1,0,0,0,0,1,0,0,1,1,1,1,1,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
            {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1},
            {1,1,1,0,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,1,1,1,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,1},
            {1,0,0,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1},
            {1,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
            {1,0,0,1,0,0,0,1,0,0,2,2,2,2,0,0,0,0,0,0,1,0,0,0,1},
            {1,0,0,1,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,1,1,1,0,1},
            {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
            {1,0,0,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,1,0,0,0,1,0,0,0,1,1,1,1,1,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}

    };

    public int getRows() {
        return map.length;
    }

    public int getColumns() {
        return map[0].length;
    }

    public boolean isInside(int row, int column) {
        return row >= 0
                && row < getRows()
                && column >= 0
                && column < getColumns();
    }

    public TileType getTile(int row, int column) {
        if (!isInside(row, column)) {
            return TileType.WALL;
        }

        return TileType.fromId(map[row][column]);
    }

    public boolean isWalkable(int row, int column) {
        return getTile(row, column).isWalkable();
    }

    public int getMovementCost(int row, int column) {
        return getTile(row, column).getMovementCost();
    }
}