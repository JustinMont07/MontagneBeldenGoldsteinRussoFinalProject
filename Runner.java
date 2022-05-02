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
public class Runner extends AnimatedGraphicsObject {
    // delay time between frames of animation (ms)

    // we don't want to move too quickly, so a delay here of about 33
    // ms will make the loop in run go around about 30 times per
    // second, which is a good enough refresh rate to ensure that the
    // animation looks smooth to the human eye and brain
    public static final int DELAY_TIME = 33;

    private Color color;

    private int curBase;

    private Point2D.Double upperLeft;

    private Point2D.Double endPoint;

    private double xSpeed;
    private double ySpeed;

    private Point2D.Double firstBase = new Point2D.Double(480, 475);
    private Point2D.Double secondBase = new Point2D.Double(385, 365);
    private Point2D.Double thirdBase = new Point2D.Double(290, 475);
    private Point2D.Double homePlate = new Point2D.Double(385, 585);

    // who do we live in so we can repaint?
    private JComponent container;

    /**
     * Construct a new FallingBall object.
     *
     * @param numBases  Number of bases the runner can reach
     *
     * @param container the Swing component in which this ball is being
     *                  drawn to allow it to call that component's repaint
     *                  method
     */
    public Runner(Color color, int curBase, JComponent container) {
        super(container);

        this.color = color;
        this.container = container;
        this.curBase = curBase;

        if (curBase == 1) {
            upperLeft = firstBase;
            endPoint = secondBase;
            
        } else if (curBase == 2) {
            upperLeft = secondBase;
            endPoint = thirdBase;
            
        } else if (curBase == 3) {
            upperLeft = thirdBase;
            endPoint = homePlate;
            
        } else if (curBase == 0) {
            upperLeft = homePlate;
            endPoint = firstBase;
            
        }

        double xMove = endPoint.x - upperLeft.x;
        double yMove = endPoint.y - upperLeft.y;

        ySpeed = yMove / 50;
        xSpeed = xMove / 50;



    }

    /**
     * Draw the runner at its current location.
     *
     * @param g the Graphics object on which the ball should be drawn
     */
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillOval((int)upperLeft.x, (int)upperLeft.y, 20, 20);
    }
    /**
     * Checks if the runner is within 5 pixels of the base
     * @param s
     * @param e
     * @return
     */
    public boolean near(Point2D.Double s, Point2D.Double e) {
        if (s.x > e.x - 5 && s.x < e.x + 5) {
            if (s.y > e.y - 5 && s.y < e.y + 5)
                return true;
        }
        return false;
    }

    /**
     * This object's run method, which manages the life of the runner
     */
    @Override
    public void run() {

        // the run method is what runs in this object's thread for the
        // time it is "alive"

        // this runners life as a thread will continue as long
        //as the runner isnt near the base (within 5 pixels)
        while (!near(upperLeft, endPoint)) {

            try {
                sleep(DELAY_TIME);
            } catch (InterruptedException e) {
            }

            // every 30 ms or so, we move the coordinates of the runner toward the next base
            
            upperLeft.setLocation(upperLeft.x + xSpeed, upperLeft.y + ySpeed);
            
            // if we want to see the ball move to its new position, we
            // need to schedule a paint event on this container
            container.repaint();
        }

        done = true;
    }

}
