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
   
       
        private int numBases;
 
        private int curBase;
 
        private Point upperLeft;
 
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
         * @param numBases       Number of bases the runner can reach
         *
         * @param container      the Swing component in which this ball is being
         *                       drawn to allow it to call that component's repaint
         *                       method
         */
        public Runner(int numBases, JComponent container) {
            super(container);
   
            this.numBases = numBases;
            this.container = container;
        }
   
        /**
         * Draw the ball at its current location.
         *
         * @param g the Graphics object on which the ball should be drawn
         */
        public void paint(Graphics g) {
       
           
        }
 
        public boolean near(Point s, Point e){
            if(s.x > e.x - 10 && s.x < e.x + 10){
                if(s.y > e.y -10 && s.y < e.y + 10)
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
            while (curBase != 4 && !done) {
   
                try {
                    sleep(DELAY_TIME);
                } catch (InterruptedException e) {
                }
                boolean reached = false;
                if(curBase == 1) {
                    reached = near(upperLeft, firstBase);
                }
                else if(curBase == 2) {
                    reached = near(upperLeft, secondBase);
                }
                if(curBase == 3) {
                    reached = near(upperLeft, thirdBase);
                }
                if(curBase == 4) {
                    reached = near(upperLeft, homePlate);
                }
                if (reached) {
                    xSpeed = 0;
                    ySpeed = 0;
                }
               
                if(numBases !=0) {
                    if(curBase == 0) {
                        int xMove = firstBase.x - homePlate.x;
                        int yMove = firstBase.y - homePlate.y;
 
                        ySpeed = yMove / 50;
                        xSpeed = xMove / 50;
 
                    }
                    else if(curBase == 1) {
                        int xMove = secondBase.x - firstBase.x;
                        int yMove = secondBase.y - firstBase.y;
 
                        ySpeed = yMove / 50;
                        xSpeed = xMove / 50;
 
                    }
 
                    else if(curBase == 2) {
                        int xMove = thirdBase.x - secondBase.x;
                        int yMove = thirdBase.y - secondBase.y;
 
                        ySpeed = yMove / 50;
                        xSpeed = xMove / 50;
                    }
                    else if(curBase == 3) {
                        int xMove = homePlate.x - thirdBase.x;
                        int yMove = homePlate.y - thirdBase.y;
 
                        ySpeed = yMove / 50;
                        xSpeed = xMove / 50;
 
                    }
                    curBase++;
                    numBases--;
                }
   
                // every 30 ms or so, we move the coordinates of the ball down
                // by a pixel
                upperLeft.translate(xSpeed, ySpeed);
   
                // if we want to see the ball move to its new position, we
                // need to schedule a paint event on this container
                container.repaint();
            }
   
            done = true;
        }
 
 
}
