import java.awt.*;
import javax.swing.*;
import java.awt.geom.Point2D;

/**
 * The FallingBall class is responsible for managing the life of
 * one ball that falls down the screen, stopping when it reaches the
 * bottom of the window.
 * 
 * @author 
 * @version Spring 2022
 */
 class Fielder extends AnimatedGraphicsObject{
      // delay time between frames of animation (ms)

    // we don't want to move too quickly, so a delay here of about 33
    // ms will make the loop in run go around about 30 times per
    // second, which is a good enough refresh rate to ensure that the
    // animation looks smooth to the human eye and brain
    public static final int DELAY_TIME = 33;

    // pixels to move each frame
    public double ySpeed;

    public double xSpeed;

    // latest location of the ball
    private Point2D.Double upperLeft;

    private Point2D.Double endPoint;


    private static final int SIZE = 20;






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
    public Fielder(Point2D.Double upperLeft, JComponent container, Point2D.Double endPoint) {
        super(container);

        this.upperLeft = upperLeft;
        this.container = container;
        this.endPoint= endPoint;
        double xMove = endPoint.x - upperLeft.x;
        double yMove = endPoint.y - upperLeft.y;

        ySpeed = yMove / 50;
        xSpeed = xMove / 50;

    }

    /**
     * Draw the ball at its current location.
     * 
     * @param g the Graphics object on which the ball should be drawn
     */
    public void paint(Graphics g) {

       // g.fillOval(upperLeft.x, upperLeft.y, SIZE, SIZE);

       g.fillOval((int)upperLeft.x, (int)upperLeft.y, SIZE, SIZE);


    }

    /**
     * This object's run method, which manages the life of the ball as it
     * moves down the screen.
     */
    @Override
    public void run() {

        // the run method is what runs in this object's thread for the
        // time it is "alive"

        // this Ball's life as a thread will continue as long as this
        // ball is still located on the visible part of the screen
        while (!near(upperLeft, endPoint)) {
        

            try {
                sleep(DELAY_TIME);
            } catch (InterruptedException e) {
            }

            // every 30 ms or so, we move the coordinates of the ball down
            // by a pixel
            upperLeft.setLocation(upperLeft.x + xSpeed, upperLeft.y + ySpeed);

            // if we want to see the ball move to its new position, we
            // need to schedule a paint event on this container
            container.repaint();
        }

        done = true;
    }

    public boolean near(Point2D.Double s, Point2D.Double e){
        if(s.x > e.x -5 && s.x < e.x + 5){
            if(s.y > e.y -5 && s.y < e.y + 5)
                return true;
        }
        
        return false;
    }
}
