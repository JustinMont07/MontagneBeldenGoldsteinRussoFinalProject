import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * A program to create BaseballGames wherever you press and hold
 * 
 * @author Justin Montagne
 * @version Spring 2022
 */

public class BaseballGame extends MouseAdapter implements Runnable {

	//The message to be displayed if no BaseballGames on screen
	private static final String INSTRUCTION_MESSAGE = "";

	// list of BaseballGame objects currently in existence
	private java.util.List<AnimatedGraphicsObject> list;

	private JPanel panel;

	//Checks if mouse is pressed
	private boolean pressed = false;

	//Marks where mouse was pressed
	private Point pressPoint;

	//Size of the BaseballGame
	private int size;

    private JPanel scoreBoard;

    private int[] team1Score = new int[5];

    private int[] team2Score = new int[5];

    private JLabel labels[][] = new JLabel[3][6];

    private Image field;

	//Start BaseballGame object which will draw the BaseballGame as it grows and allow us to get the final size of it
	//private Baseball b;

    public BaseballGame(){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j< 3; j++){
                labels[j][i] = new JLabel();
            }
        }
        try{
        field = ImageIO.read(new File("Field.jpg")).getScaledInstance(800, 800, Image.SCALE_DEFAULT);;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            }
    }
	/**
	 * The run method to set up the graphical user interface
	 */
	@Override
	public void run() {

		// set up the GUI "look and feel" which should match
		// the OS on which we are running
		JFrame.setDefaultLookAndFeelDecorated(true);

		// create a JFrame in which we will build our very
		// tiny GUI, and give the window a name
		JFrame frame = new JFrame("BaseballGame");
		frame.setPreferredSize(new Dimension(800, 800));

		// tell the JFrame that when someone closes the
		// window, the application should terminate
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// JPanel with a paintComponent method
		panel = new JPanel(new BorderLayout()) {
			@Override
			public void paintComponent(Graphics g) {

				// first, we should call the paintComponent method we are
				// overriding in JPanel
				super.paintComponent(g);
                g.drawImage(field, 0, 0, null);

				// redraw each BaseballGame's contents, and along the
				// way, remove the ones that are popped
                
				int i = 0;
				while (i < list.size()) {
					AnimatedGraphicsObject b = list.get(i);
					if (b.done()) {
						list.remove(i);
					} else {
						b.paint(g);
						i++;
					}
				}
                
				//Checks if list is empty and that the user isn't currently making a BaseballGame. Displays a message in 
				//the center of window telling you how to make a new BaseballGame
                /**
				if (list.size() == 0 && !pressed) {
					g.setColor(Color.black);
					FontMetrics str = g.getFontMetrics();
					int pHeight = this.getHeight();
					int pWidth = this.getWidth();
					int strWidth = str.stringWidth(INSTRUCTION_MESSAGE);
					int ascent = (str.getAscent());
					g.drawString(INSTRUCTION_MESSAGE, pWidth / 2 - (strWidth / 2), pHeight / 2 - ascent);
				}

				if (pressed) {
					b.paint(g);
				}
                */

			}
		};
        scoreBoard = new JPanel(new GridLayout(3,6));
		frame.add(panel);
        panel.add(scoreBoard, BorderLayout.NORTH);
        
        
        for(int i = 1; i < 6; i++){
            labels[0][i].setText("" + i);
        }
        for(int i = 1; i < 6; i++){
            labels[1][i].setText("" + 0);
        }
        for(int i = 1; i < 6; i++){
            labels[2][i].setText("" + 0);
        }
        labels[0][0].setText("Inning");
        labels[1][0].setText("Team1");
        labels[2][0].setText("Team2");
        
        for(int i = 0; i < 3; i++){
			for(int j = 0; j < 6 ; j++){
            	scoreBoard.add(labels[i][j]);
				labels[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
			}
		}
		
        scoreBoard.setBorder(BorderFactory.createLineBorder(Color.black));
		
		panel.addMouseListener(this);
    
		// construct the list
		list = new ArrayList<AnimatedGraphicsObject>();

		// display the window we've created
		frame.pack();
		frame.setVisible(true);
        frame.setResizable(false);
	}


	 /**
	 * Mouse press event handler to create a new FallingBall with its top
	 * centered at the press point.
	 * 
	 * @param e mouse event info
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		Ball newBall = new Ball(e.getPoint(), panel,5);
		list.add(newBall);

		// calling start on the object that extends Thread results in
		// its run method being called once the operating system and
		// JVM have set up the thread
		newBall.start();
		panel.repaint();
	}


    public static void main(String args[]) {

		Ball.loadBallPic();

		// launch the main thread that will manage the GUI
		javax.swing.SwingUtilities.invokeLater(new BaseballGame());

	}
}
