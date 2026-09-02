package com.example.demo_escape_escape.world;

public enum TileType {

    FLOOR(0, true, 1),
    WALL(1, false, Integer.MAX_VALUE),
    DEBRIS(2, true, 2);

    private final int id;
    private final boolean walkable;
    private final int movementCost;

    TileType(int id, boolean walkable, int movementCost) {
        this.id = id;
        this.walkable = walkable;
        this.movementCost = movementCost;
    }

    public int getId() {
        return id;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public int getMovementCost() {
        return movementCost;
    }

    public static TileType fromId(int id) {
        for (TileType tile : values()) {
            if (tile.getId() == id) {
                return tile;
            }
        }

        return WALL;
    }
}