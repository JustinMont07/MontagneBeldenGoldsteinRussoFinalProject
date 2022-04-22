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
	private static final String INSTRUCTION_MESSAGE = "Press and hold the mouse to blow a BaseballGame, release to set it free!";

	// list of BaseballGame objects currently in existence
	private java.util.List<BaseballGame> list;

	private JPanel panel;

	//Checks if mouse is pressed
	private boolean pressed = false;

	//Marks where mouse was pressed
	private Point pressPoint;

	//Size of the BaseballGame
	private int size;

    private JPanel scoreBoard;

    private int[] team1 = new int[5];

    private int[] team2 = new int[5];

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
		JFrame frame = new JFrame("BaseballGameBlower");
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
                /**
				int i = 0;
				while (i < list.size()) {
					BaseballGame b = list.get(i);
					if (b.popped()) {
						list.remove(i);
					} else {
						b.paint(g);
						i++;
					}
				}
                */
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
        
        
        scoreBoard.add(labels[0][0]);
        scoreBoard.add(labels[0][1]);
        scoreBoard.add(labels[0][2]);
        scoreBoard.add(labels[0][3]);
        scoreBoard.add(labels[0][4]);
        scoreBoard.add(labels[0][5]);
        scoreBoard.add(labels[1][0]);
        scoreBoard.add(labels[1][1]);
        scoreBoard.add(labels[1][2]);
        scoreBoard.add(labels[1][3]);
        scoreBoard.add(labels[1][4]);
        scoreBoard.add(labels[1][5]);
        scoreBoard.add(labels[2][0]);
        scoreBoard.add(labels[2][1]);
        scoreBoard.add(labels[2][2]);
        scoreBoard.add(labels[2][3]);
        scoreBoard.add(labels[2][4]);
        scoreBoard.add(labels[2][5]);
        
        scoreBoard.setBorder(BorderFactory.createLineBorder(Color.black));
        labels[0][1].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[0][2].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[0][3].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[0][4].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[0][5].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[1][1].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[1][2].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[1][3].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[1][4].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[1][5].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[2][1].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[2][2].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[2][3].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[2][4].setBorder(BorderFactory.createLineBorder(Color.black));
        labels[2][5].setBorder(BorderFactory.createLineBorder(Color.black));
        
        

		panel.addMouseListener(this);
        

		// construct the list
		list = new ArrayList<BaseballGame>();

		// display the window we've created
		frame.pack();
		frame.setVisible(true);
        frame.setResizable(false);
	}
    public static void main(String args[]) {

		// launch the main thread that will manage the GUI
		javax.swing.SwingUtilities.invokeLater(new BaseballGame());
	}
}
