package com.example.dialpad;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;


public class UINavigator {

    private BaseContainer container;
    private View currentElementInFocus;
    private Position currentPos;
    private Context ctx;
    private ObjectAnimator anim;
    private Toast toast;



    /**
     * Constructor
     * @param pCont
     */
    public UINavigator(BaseContainer pCont, Context pCtx){
        this.container = pCont;
        this.currentPos = new Position();
        this.ctx = pCtx;

        boolean isFound = false;

        for(int y=0; y<container.getxSize() && !isFound; y++) {
            for (int x = 0; x < container.getySize() && !isFound; x++) {
                Position position = new Position(x, y);
                if (container.isValid(position)) {
                    if (!container.isEmpty(position)) {
                        currentElementInFocus = (View) container.get(position);
                        currentPos.setX(position.getX());
                        currentPos.setY(position.getY());
                        setCurrentElementAsMarked();
                        isFound = true;
                    }
                }
            }
        }
    }

    /**
     * Simulates a click
     * @return currentElementInFocus
     */
    public View click(){
        return currentElementInFocus;
    }

    /**
     * Simulates a long click
     * @return currentElementInFocus
     */
    public View longClick(){
        return currentElementInFocus;
    }

    /**
     * Simulates a navigation upwards in UI
     * @return true if move was successful
     */
    public boolean moveUp(){

        Position nextPos = new Position(currentPos.getX(), currentPos.getY() - 1);
        if(container.isValid(nextPos) && !container.isEmpty(nextPos)){
            unmarkCurrentElement();
            currentElementInFocus = (View)container.get(nextPos);
            currentPos.setY(currentPos.getY() - 1);
            setCurrentElementAsMarked();
            return true;
        }

        playActionNotValidSound();
        return false;
    }

    /**
     * Simulates a navigation downwards in UI
     * @return true if move was successful
     */
    public boolean moveDown(){

        Position nextPos = new Position(currentPos.getX(), currentPos.getY() + 1);
        if(container.isValid(nextPos) && !container.isEmpty(nextPos)){
            unmarkCurrentElement();
            currentElementInFocus = (View)container.get(nextPos);
            currentPos.setY(currentPos.getY() + 1);
            setCurrentElementAsMarked();
            return true;
        }

        playActionNotValidSound();
        return false;
    }

    /**
     * Simulates a navigation to the left in UI
     * @return true if move was successful
     */
    public boolean moveLeft(){

        Position nextPos = new Position(currentPos.getX() - 1, currentPos.getY());
        if(container.isValid(nextPos) && !container.isEmpty(nextPos)){
            unmarkCurrentElement();
            currentElementInFocus = (View)container.get(nextPos);
            currentPos.setX(currentPos.getX() - 1);
            setCurrentElementAsMarked();
            return true;
        }

        playActionNotValidSound();
        return false;
    }

    /**
     * Simulates a navigation to the right in UI
     * @return true if move was successful
     */
    public boolean moveRight(){

        Position nextPos = new Position(currentPos.getX() + 1, currentPos.getY());
        if(container.isValid(nextPos) && !container.isEmpty(nextPos)){
            unmarkCurrentElement();
            currentElementInFocus = (View)container.get(nextPos);
            currentPos.setX(currentPos.getX() + 1);
            setCurrentElementAsMarked();
            return true;
        }

        playActionNotValidSound();
        return false;
    }

    /**
     * Creates some visual feedback to indicate which
     * element is currently marked
     */
    private void setCurrentElementAsMarked(){
        anim = ObjectAnimator.ofFloat(currentElementInFocus, "alpha", 0.2f);
        anim.setDuration(1);
        anim.start();

        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(ctx, "Element in focus: x=" + currentPos.getX() + " y=" + currentPos.getY(), Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * Resets the visual feedback on the previously chosen element
     */
    private void unmarkCurrentElement(){
        anim = ObjectAnimator.ofFloat(currentElementInFocus, "alpha", 1f);
        anim.setDuration(1);
        anim.start();
    }


    /**
     * Plays a sound to indicate that an action was not valid, or
     * displays a toast
     */
    private void playActionNotValidSound(){

        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(ctx, "Action not valid!", Toast.LENGTH_SHORT);
        toast.show();

        /*try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(ctx, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }
}
