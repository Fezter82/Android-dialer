package com.example.dialpad;
import java.util.ArrayList;

/**
 *
 * @param <T> object type to be stored in grid, for android ui use android.view.View
 *
 * Grid example: (x,y)
 *
 * |(0,0)|(1,0)|(2,0)|
 * |(0,1)|(1,1)|(2,1)|
 * |(0,2)|(1,2)|(2,2)|
 *
 */
public class UIContainer<T> implements BaseContainer<T> {
    private ArrayList<ArrayList<T>> grid;
    private int xSize;
    private int ySize;
    private boolean scrollable;

    /**
     * Constructor
     * @param xSize
     * @param ySize
     */
    public UIContainer(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        grid = new ArrayList<ArrayList<T>>();

        ArrayList<T> yList = new ArrayList<T>();
        for (int y = 0; y < ySize; y++) {
            yList.add(null);
        }

        for (int x = 0; x < xSize; x++) {
            grid.add(new ArrayList<T>(yList));
        }

        this.scrollable = false;
    }

    /**
     * Check if a position (x,y) is valid, which means in the grid
     * @param position
     * @return true if valid
     */
    @Override
    public boolean isValid(Position position) {
        return position.getX() < xSize && position.getY() < ySize && position.getX() >= 0 && position.getY() >= 0;
    }

    /**
     * Removes object at position
     * @param position
     * @return true if successful, false if position is not valid
     */
    @Override
    public boolean remove(Position position) {
        // if trying to remove object outside grid
        if (!isValid(position))
            return false;

        grid.get(position.getX()).set(position.getY(), null);

        return true;
    }

    /**
     * Checks if position is empty
     * @param position
     * @return true if empty, false if not empty or not valid
     */
    @Override
    public boolean isEmpty(Position position) {
        return isValid(position) && grid.get(position.getX()).get(position.getY()) == null;
    }

    /**
     * Return object at position. Returns null if empty or invalid position
     * @param position
     * @return
     */
    @Override
    public T get(Position position) {
        if (isValid(position))
            return grid.get(position.getX()).get(position.getY());

        return null;
    }

    /**
     * Places object at position, overwrites current object
     * @param position
     * @param object
     * @return true if successful
     */
    @Override
    public boolean set(Position position, T object) {
        if (!isValid(position))
            return false;

        grid.get(position.getX()).set(position.getY(), object);

        return true;
    }

    @Override
    public String toString() {
        String s = new String();

        for (int y= 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                Position position = new Position(x, y);
                if (get(position) == null)
                    s += " -";
                else
                    s += " " + get(position).toString();
            }
            s += "\n";
        }


        return s;
    }

    @Override
    public int getxSize() {
        return xSize;
    }

    @Override
    public int getySize() {
        return ySize;
    }

  }
