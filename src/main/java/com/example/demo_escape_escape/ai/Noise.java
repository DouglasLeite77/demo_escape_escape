package com.example.demo_escape_escape.ai;

public class Noise {

    private double x;
    private double y;
    private double radius;
    private boolean active;

    public void emit(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.active = true;
    }

    public void clear() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }
}