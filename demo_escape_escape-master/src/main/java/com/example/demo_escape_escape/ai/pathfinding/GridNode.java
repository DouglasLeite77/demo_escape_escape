package com.example.demo_escape_escape.ai.pathfinding;

import java.util.Objects;

public class GridNode {

    private final int row;
    private final int column;

    private int gCost;
    private int hCost;

    private GridNode parent;

    public GridNode(int row, int column) {
        this.row = row;
        this.column = column;
        this.gCost = Integer.MAX_VALUE;
        this.hCost = 0;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getGCost() {
        return gCost;
    }

    public void setGCost(int gCost) {
        this.gCost = gCost;
    }

    public int getHCost() {
        return hCost;
    }

    public void setHCost(int hCost) {
        this.hCost = hCost;
    }

    public int getFCost() {
        if (gCost == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return gCost + hCost;
    }

    public GridNode getParent() {
        return parent;
    }

    public void setParent(GridNode parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof GridNode)) {
            return false;
        }

        GridNode other = (GridNode) obj;

        return row == other.row
                && column == other.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}