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
public class Runner extends AnimatedGraphicsObject {
    // delay time between frames of animation (ms)

    // we don't want to move too quickly, so a delay here of about 33
    // ms will make the loop in run go around about 30 times per
    // second, which is a good enough refresh rate to ensure that the
    // animation looks smooth to the human eye and brain
    public static final int DELAY_TIME = 33;

    protected int numBases;

    private int curBase;

    private Point upperLeft;

    private Point endPoint;

    private int xSpeed;
    private int ySpeed;

    private Point firstBase = new Point(480, 475);
    private Point secondBase = new Point(385, 365);
    private Point thirdBase = new Point(290, 475);
    private Point homePlate = new Point(385, 585);

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
    public Runner(int numBases, int curBase, JComponent container) {
        super(container);

        this.numBases = numBases;
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

        int xMove = endPoint.x - upperLeft.x;
        int yMove = endPoint.y - upperLeft.y;

        ySpeed = -1;
        xSpeed = 1;

    }

    /**
     * Draw the ball at its current location.
     *
     * @param g the Graphics object on which the ball should be drawn
     */
    public void paint(Graphics g) {
        g.fillOval(upperLeft.x, upperLeft.y, 20, 20);
    }

    public boolean near(Point s, Point e) {
        if (s.x > e.x - 15 && s.x < e.x + 15) {
            if (s.y > e.y - 15 && s.y < e.y + 15)
                return true;
        }
        return false;
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
            // if (!near(upperLeft, endPoint)) {
            upperLeft.translate(xSpeed, ySpeed);
            // } else {
            // ySpeed = 0;
            // xSpeed = 0;
            // curBase++;
            // numBases--;
            // }
            if (numBases == 0) {
                done = true;
            }
            // if we want to see the ball move to its new position, we
            // need to schedule a paint event on this container
            container.repaint();
        }

        done = true;
    }

}
