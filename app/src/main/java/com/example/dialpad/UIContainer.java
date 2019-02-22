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
public class UIContainer<T> {
    private ArrayList<ArrayList<T>> grid;
    private int xSize;
    private int ySize;

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
    }

    /**
     * Check if a position (x,y) is valid, which means in the grid
     * @param x
     * @param y
     * @return true if valid
     */
    public boolean isValid(int x, int y) {
        return x < xSize && y < ySize;
    }

    /**
     * Removes object at position
     * @param x
     * @param y
     * @return true if successful, false if position is not valid
     */
    public boolean remove(int x, int y) {
        // if trying to remove object outside grid
        if (!isValid(x, y))
            return false;

        grid.get(x).set(y, null);

        return true;
    }

    /**
     * Checks if position is empty
     * @param x
     * @param y
     * @return true if empty, false if not empty or not valid
     */
    public boolean isEmpty(int x, int y) {
        return isValid(x, y) && grid.get(x).get(y) == null;
    }

    /**
     * Return object at position. Returns null if empty or invalid position
     * @param x
     * @param y
     * @return
     */
    public T get(int x, int y) {
        if (isValid(x, y))
            return grid.get(x).get(y);

        return null;
    }

    /**
     * Places object at position, overwrites current object
     * @param x
     * @param y
     * @param object
     * @return true if successful
     */
    public boolean set(int x, int y, T object) {
        if (!isValid(x, y))
            return false;

        grid.get(x).set(y, object);

        return true;
    }

    @Override
    public String toString() {
        String s = new String();

        for (int y= 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                if (get(x, y) == null)
                    s += " -";
                else
                    s += " " + get(x, y).toString();
            }
            s += "\n";
        }


        return s;
    }
}
