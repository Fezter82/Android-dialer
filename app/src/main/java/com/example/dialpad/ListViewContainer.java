package com.example.dialpad;

import android.widget.ListView;

public class ListViewContainer<View> implements BaseContainer<View> {
    ListView listView;
    boolean scrollable;

    /**
     * Constructor
     * @param listView
     */
    public ListViewContainer(ListView listView) {
        super();
        this.listView = listView;
        this.scrollable = true;
    }

    /**
     * Check if a position is valid, which means in the grid
     *
     * @param position
     * @return true if valid
     */
    @Override
    public boolean isValid(Position position) {
        return position.getPos() >= 0 && position.getPos() < listView.getChildCount();
    }

    /**
     * Removes object at position
     *
     * @param position
     * @return true if successful, false if position is not valid
     * @TODO Update implementation
     */
    @Override
    public boolean remove(Position position) {
        return false;
    }

    /**
     * Checks if position is empty
     *
     * @param position
     * @return true if empty, false if not empty or not valid
     * @TODO Update implementation
     */
    @Override
    public boolean isEmpty(Position position) {
        return false;
    }

    /**
     * Return object at position. Returns null if empty or invalid position
     *
     * @param position
     * @return
     */
    @Override
    public View get(Position position) {
        if (isValid(position))
            return (View) listView.getChildAt(position.getPos());

        return null;
    }

    /**
     * Places object at position, overwrites current object
     *
     * @param position
     * @param object
     * @return true if successful
     * @TODO Update implementation
     */
    @Override
    public boolean set(Position position, Object object) {
        return false;
    }

    /**
     * @return true if ui is scrollable
     */
    @Override
    public boolean isScrollable() {
        return scrollable;
    }

    /**
     * Sets the scrollable flag
     *
     * @param scrollable
     */
    @Override
    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    /**
     * @return x-size of container
     * @ TODO Implement method
     */
    @Override
    public int getxSize() {
        return 0;
    }

    /**
     * @return y-size of container
     * @ TODO Implement method
     */
    @Override
    public int getySize() {
        return 0;
    }

    /**
     * @return true if scrolling succeded
     */
    @Override
    public boolean scrollUp() {
        if(isScrollable()) {
            listView.smoothScrollByOffset(-1);
            return true;}
        return false;
    }

    /**
     * @return true if scrolling succeded
     */
    @Override
    public boolean scrollDown() {
        if(isScrollable()) {
            listView.smoothScrollByOffset(1);
            return true;}
        return false;
    }

    /**
     * @return true if scrolling succeded
     */
    @Override
    public boolean scrollLeft() {
        return false;
    }

    /**
     * @return true if scrolling succeded
     */
    @Override
    public boolean scrollRight() {
        return false;
    }
}
