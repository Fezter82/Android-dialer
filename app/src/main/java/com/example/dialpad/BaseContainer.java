package com.example.dialpad;

import java.util.ArrayList;

public interface BaseContainer<T> {
    /**
     * Check if a position is valid, which means in the grid
     * @param position
     * @return true if valid
     */
    public boolean isValid(Position position);
    /**
     * Removes object at position
     * @param position
     * @return true if successful, false if position is not valid
     */
    public boolean remove(Position position);

    /**
     * Checks if position is empty
     * @param position
     * @return true if empty, false if not empty or not valid
     */
    public boolean isEmpty(Position position);

    /**
     * Return object at position. Returns null if empty or invalid position
     * @param position
     * @return
     */
    public T get(Position position);

    /**
     * Places object at position, overwrites current object
     * @param position
     * @param object
     * @return true if successful
     */
    public boolean set(Position position, T object);

    /**
     *
     * @return x-size of container
     */
    public int getxSize();

    /**
     *
     * @return y-size of container
     */
    public int getySize();
}