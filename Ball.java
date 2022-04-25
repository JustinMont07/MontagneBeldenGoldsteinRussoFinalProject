import java.awt.*;
import javax.swing.*;

/**
 * The FallingBall class is responsible for managing the life of
 * one ball that falls down the screen, stopping when it reaches the
 * bottom of the window.
 * 
 * @author 
 * @version Spring 2022
 */
 class Ball extends AnimatedGraphicsObject{
      // delay time between frames of animation (ms)

    // we don't want to move too quickly, so a delay here of about 33
    // ms will make the loop in run go around about 30 times per
    // second, which is a good enough refresh rate to ensure that the
    // animation looks smooth to the human eye and brain
    public static final int DELAY_TIME = 33;

    // pixels to move each frame
    public int ySpeed= 4;

    // latest location of the ball
    private Point upperLeft;

    // how far to fall?
    private int bottom;

   
    // who do we live in so we can repaint?
    private JComponent container;

    /**
     * Construct a new FallingBall object.
     * 
     * @param startTopCenter the initial point at which the top of the
     *                       ball should be drawn
     * @param container      the Swing component in which this ball is being
     *                       drawn to allow it to call that component's repaint
     *                       method
     */
    public Ball(Point startTopCenter, JComponent container, int ySpeed) {
        super(container);

        upperLeft = new Point(startTopCenter.x  / 2, startTopCenter.y);
        this.bottom = container.getHeight();
        this.container = container;
        this.ySpeed = ySpeed;
    }

    /**
     * Draw the ball at its current location.
     * 
     * @param g the Graphics object on which the ball should be drawn
     */
    public void paint(Graphics g) {

       // g.fillOval(upperLeft.x, upperLeft.y, SIZE, SIZE);


    }

    /**
     * This object's run method, which manages the life of the ball as it
     * moves down the screen.
     */
    @Override
    public void run() {

        // the run method is what runs in this object's thread for the
        // time it is "alive"

        // this FallingBall's life as a thread will continue as long as this
        // ball is still located on the visible part of the screen
        while (upperLeft.y < bottom) {

            try {
                sleep(DELAY_TIME);
            } catch (InterruptedException e) {
            }

            // every 30 ms or so, we move the coordinates of the ball down
            // by a pixel
            upperLeft.translate(0, ySpeed);

            // if we want to see the ball move to its new position, we
            // need to schedule a paint event on this container
            container.repaint();
        }

        done = true;
    }


}