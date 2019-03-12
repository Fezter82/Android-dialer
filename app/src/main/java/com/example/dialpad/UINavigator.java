package com.example.dialpad;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;


public class UINavigator implements SensorEventListener {

    private BaseContainer<View> container;
    private View currentElementInFocus;
    private Position currentPos;
    private Context ctx;
    private ObjectAnimator anim;
    private Toast toast;
    // a scrollview with linearlayout as only child that wraps all other views, null if layout don't use scrollview
    private ScrollView scrollView;

    private SensorManager sm;
    private Sensor accelerometer;


    /**
     * Constructor
     */
    public UINavigator(BaseContainer<View> pCont, Context pCtx, Activity parent){
        this.container = pCont;
        this.currentPos = new Position();
        this.ctx = pCtx;
        this.scrollView = null;

        this.sm = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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
     * Setter
     * @param scrollView
     */
    public void setScrollView (ScrollView scrollView) { this.scrollView = scrollView; }

    /**
     * start
     */
    public void start(){
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * stop
     */
    public void stop(){
        sm.unregisterListener(this);
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
     * Called to update the scrollviews position
     * @param view - the view to scroll to
     */
    public void updateScrollView(View view) {
        // Get rect representing scrollview
        Rect scrollViewRect = new Rect();
        scrollView.getDrawingRect(scrollViewRect);


        // Get upper and lower limits of View
        double viewTop = view.getY();
        double viewBottom = viewTop + view.getHeight();

        // if any part of the view is outside of the screen, scroll
        // to it
        if (viewTop < scrollViewRect.top|| viewBottom > scrollViewRect.bottom) {
            Rect viewRect = new Rect();
            view.getHitRect(viewRect);
            scrollView.requestChildRectangleOnScreen(scrollView.getChildAt(0), viewRect, false);
        }
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

            if (scrollView != null) {
                updateScrollView(currentElementInFocus);
            }

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

            if (scrollView != null) {
                updateScrollView(currentElementInFocus);
            }

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

            if (scrollView != null) {
                updateScrollView(currentElementInFocus);
            }

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

            if (scrollView != null) {
                updateScrollView(currentElementInFocus);
            }

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Feeds new X, Y, Z-values when accelerometer/gyro has detected a motion
    @Override
    public void onSensorChanged(SensorEvent event) {


        //CLICK
        if(event.values[2] > 15 && event.values[1] > 4 && event.values[1] < 8){

            System.out.println("CLICK detected");
            System.out.println("x: " + event.values[0] + " Y: " + event.values[1] + " Z:" + event.values[2]);

            sm.unregisterListener(this);

            View clickedView = click();

            if(clickedView != null)
                if(clickedView.isClickable()){
                    clickedView.performClick();
                }

            try{
                Thread.sleep(400);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        //UP
        else if(event.values[1] < 4 && event.values[2] > 10){

            System.out.println("UP detected");
            System.out.println("x: " + event.values[0] + " Y: " + event.values[1] + " Z:" + event.values[2]);

            sm.unregisterListener(this);

            moveUp();

            try{
                Thread.sleep(400);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }
        //DOWN
        else if(event.values[1] > 8 && event.values[2] < 4){

            sm.unregisterListener(this);

            if(event.values[1] > 8){
                System.out.println("DOWN detected");
                System.out.println("x: " + event.values[0] + " Y: " + event.values[1] + " Z:" + event.values[2]);
                moveDown();
            }
            else{
                System.out.println("LONG CLICK detected");
                System.out.println("x: " + event.values[0] + " Y: " + event.values[1] + " Z:" + event.values[2]);

                View clickedView = longClick();

                if(clickedView != null)
                    if(clickedView.isLongClickable()){
                        clickedView.performLongClick();
                    }
            }

            try{
                Thread.sleep(400);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }
        //LEFT
        else if(event.values[0] > 3){

            System.out.println("LEFT detected");
            System.out.println("x: " + event.values[0] + " Y: " + event.values[1] + " Z:" + event.values[2]);

            sm.unregisterListener(this);

            moveLeft();

            try{
                Thread.sleep(400);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }
        //RIGHT
        else if(event.values[0] < -3){

            System.out.println("RIGHT detected");
            System.out.println("x: " + event.values[0] + " Y: " + event.values[1] + " Z:" + event.values[2]);

            sm.unregisterListener(this);

            moveRight();

            try{
                Thread.sleep(400);
            } catch(InterruptedException e){
                e.printStackTrace();
            }

            sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        }

         /*//LONG CLICK
         else if(event.values[2] < 4){

             System.out.println("LONG CLICK detected");
             System.out.println("x: " + event.values[0] + " Y: " + event.values[1] + " Z:" + event.values[2]);

             sm.unregisterListener(this);

             View clickedView = longClick();

             if(clickedView != null)
                 if(clickedView.isLongClickable()){
                     clickedView.performLongClick();
                 }

             try{
                 Thread.sleep(400);
             } catch(InterruptedException e){
                 e.printStackTrace();
             }

             sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
         }*/


    }
}
