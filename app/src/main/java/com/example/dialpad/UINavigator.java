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

    private UIContainer container;
    private View currentElementInFocus;
    private int currentX, currentY;
    private Context ctx;
    private ObjectAnimator anim;



    /**
     * Constructor
     * @param pCont
     */
    public UINavigator(UIContainer pCont, Context pCtx){
        this.container = pCont;
        this.currentX = 0;
        this.currentY = 0;
        this.ctx = pCtx;

        boolean isFound = false;

        for(int y=0; y<container.getxSize() && !isFound; y++) {
            for (int x = 0; x < container.getySize() && !isFound; x++) {
                if (container.isValid(x, y)) {
                    if (!container.isEmpty(x, y)) {
                        currentElementInFocus = (View) container.get(x, y);
                        currentX = x;
                        currentY = y;
                        setCurrentElementAsMarked();
                        isFound = true;
                    }
                }
            }
        }
    }

    /**
     * Simulates a navigation upwards in UI
     * @return true if move was successful
     */
    public boolean moveUp(){

        if(container.isValid(currentX, currentY-1) && !container.isEmpty(currentX, currentY-1)){
            unmarkCurrentElement();
            currentElementInFocus = (View)container.get(currentX, currentY-1);
            currentY--;
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

        if(container.isValid(currentX, currentY+1) && !container.isEmpty(currentX, currentY+1)){
            unmarkCurrentElement();
            currentElementInFocus = (View)container.get(currentX, currentY+1);
            currentY++;
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

        if(container.isValid(currentX-1, currentY) && !container.isEmpty(currentX-1, currentY)){
            unmarkCurrentElement();
            currentElementInFocus = (View)container.get(currentX-1, currentY);
            currentX--;
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

        if(container.isValid(currentX+1, currentY) && !container.isEmpty(currentX+1, currentY)){
            unmarkCurrentElement();
            currentElementInFocus = (View)container.get(currentX+1, currentY);
            currentX++;
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

        Toast toast;
        toast = Toast.makeText(ctx, "Element in focus: x=" + currentX + " y=" + currentY, Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * Resets the visual feedback on the previously chosen element
     */
    private void unmarkCurrentElement(){
        anim.cancel();
    }


    /**
     * Plays a sound to indicate that an action was not valid, or
     * displays a toast
     */
    private void playActionNotValidSound(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(ctx, notification);
            r.play();
        } catch (Exception e) {
            Toast errorToast;
            errorToast = Toast.makeText(ctx, "Action not valid!", Toast.LENGTH_SHORT);
            errorToast.show();
            e.printStackTrace();
        }
    }
}
