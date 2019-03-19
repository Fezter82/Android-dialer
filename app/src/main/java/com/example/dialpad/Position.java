package com.example.dialpad;

/**
 * Describes a position in a one or two dimensional system
 */
public class Position {
    private int x;
    private int y;

    public Position() {
        this.x = 0;
        this.y = 0;
    }

    public Position(int pos) {
        this.x = 0;
        this.y = pos;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /*
     * Getter and setter for position in a one dimensional
     * system
     */
    public void setPos(int pos) { setY(pos); }
    public int getPos() { return getY(); }
}