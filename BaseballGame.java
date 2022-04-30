import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * A program to create BaseballGames wherever you press and hold
 * 
 * @author Justin Montagne
 * @version Spring 2022
 */

public class BaseballGame extends MouseAdapter implements Runnable {

	// The message to be displayed if no BaseballGames on screen
	private String displayText = "";

	// list of BaseballGame objects currently in existence
	private java.util.List<AnimatedGraphicsObject> list;

	private JPanel panel;

	// Checks if mouse is pressed
	private boolean pressed = false;

	// Marks where mouse was pressed
	private Point pressPoint;

	// Size of the BaseballGame
	private int size;

	private JPanel scoreBoard;

	private int[] team1Score = new int[5];

	private int[] team2Score = new int[5];

	private boolean[] runnerCheck = new boolean[3];

	private Point firstBase = new Point(480, 475);
	private Point secondBase = new Point(385, 365);
	private Point thirdBase = new Point(290, 475);
	private Point homePlate = new Point(385, 585);

	private ArrayList<AnimatedGraphicsObject> runners = new ArrayList<AnimatedGraphicsObject>();

	private int outs;

	private int strikes;

	private int team;
	private int curInning;

	private JLabel labels[][] = new JLabel[3][6];

	// 5 points for hits, 5 points for outs, 1 for home run
	private Point[] leftField = new Point[] { new Point(175, 200), new Point(250, 150), new Point(200, 325),
			new Point(250, 350), new Point(200, 250), new Point(250, 250), new Point(125, 115) };

	// 5 points for hits, 5 points for outs, 1 for home run
	private Point[] centerField = new Point[] { new Point(450, 250), new Point(340, 250), new Point(385, 100),
			new Point(385, 150), new Point(385, 300), new Point(400, 250), new Point(385, 0) };

	// 5 points for hits, 5 points for outs, 1 for home run
	private Point[] rightField = new Point[] { new Point(520, 150), new Point(570, 325), new Point(600, 200),
			new Point(520, 350), new Point(520, 250), new Point(570, 250), new Point(660, 115) };

	private Image field;

	private int clickCount;

	// Start BaseballGame object which will draw the BaseballGame as it grows and
	// allow us to get the final size of it
	// private Baseball b;

	public BaseballGame() {
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				labels[j][i] = new JLabel();
			}
		}
		try {
			field = ImageIO.read(new File("Field.jpg")).getScaledInstance(800, 800, Image.SCALE_DEFAULT);
			;
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

				// g.fillOval(250, 150, 30, 30);
				// g.drawOval(200, 250, 30, 30);
				// g.drawOval(250, 250, 30, 30);
				// g.fillOval(200, 325, 30, 30);

				// border
				g.setColor(Color.BLACK);
				g.drawRect(369, 600, 60, 120);

				// red - early -
				g.setColor(new Color(230, 0, 0, 100));
				g.fillRect(369, 600, 60, 40);

				// Green - middle -
				g.setColor(new Color(0, 234, 0, 100));
				g.fillRect(369, 640, 60, 40);

				// blue - late -
				g.setColor(new Color(0, 0, 222, 100));
				g.fillRect(369, 680, 60, 40);

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

				if (team == 1) {
					g.setColor(Color.blue);
				} else {
					g.setColor(Color.red);
				}

				if (runnerCheck[0]) {
					g.fillOval(firstBase.x, firstBase.y, 20, 20);
				}
				if (runnerCheck[1]) {
					g.fillOval(secondBase.x, secondBase.y, 20, 20);
				}
				if (runnerCheck[2]) {
					g.fillOval(thirdBase.x, thirdBase.y, 20, 20);
				}

				// Checks if list is empty and that the user isn't currently making a
				// BaseballGame. Displays a message in
				// the center of window telling you how to make a new BaseballGame

				if (list.size() == 0 && !pressed) {
					g.setColor(Color.black);
					FontMetrics str = g.getFontMetrics();
					int pHeight = this.getHeight();
					int pWidth = this.getWidth();
					int strWidth = str.stringWidth(displayText);
					int ascent = (str.getAscent());
					g.drawString(displayText, pWidth / 2 - (strWidth / 2), pHeight / 2 -
							ascent);
				}

			}
		};
		scoreBoard = new JPanel(new GridLayout(3, 6));
		frame.add(panel);
		panel.add(scoreBoard, BorderLayout.NORTH);

		for (int i = 1; i < 6; i++) {
			labels[0][i].setText("" + i);
		}
		for (int i = 1; i < 6; i++) {
			labels[1][i].setText("" + 0);
		}
		for (int i = 1; i < 6; i++) {
			labels[2][i].setText("" + 0);
		}
		labels[0][0].setText("Inning");
		labels[1][0].setText("Team1");
		labels[2][0].setText("Team2");

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 6; j++) {
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

		// ball drops from homebase
		if (list.size() == 0) {

			Ball newBall = new Ball(new Point(433, 470), panel, 5);
			list.add(newBall);

			// calling start on the object that extends Thread results in
			// its run method being called once the operating system and
			// JVM have set up the thread
			newBall.start();
			panel.repaint();
			clickCount++;

		} else if (list.size() > 0 && clickCount == 1) {
			// have second hit

			Random r = new Random();

			int location = contains(((Ball) list.get(0)).getLocation());

			int hit = r.nextInt(7);

			if (location == 1) {
				Hit newHit = new Hit(new Point(385, 600), panel, leftField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				// while(!newHit.done()) {
				// }
				if (hit < 3) {
					displayText = "hit!";
					moveRunner(1);

				} else if (hit < 6) {
					displayText = "Out!";
				} else {
					displayText = "Homerun!";
					moveRunner(4);
				}
				list.get(0).done = true;
				// if (hit == 4) {
				// Hit newHit = new Hit(e.getPoint(), panel, leftField[10]);
				// } else if (hit < 4) {
				// Hit newHit = new Hit(e.getPoint(), panel, leftField[hit]);
				// } else {
				// Hit newHit = new Hit(e.getPoint(), panel, leftField[hit]);
				// }
				System.out.println("1");

			} else if (location == 2) {

				System.out.println("2");
				Hit newHit = new Hit(new Point(385, 620), panel, centerField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				// while(!newHit.done()) {
				// }
				if (hit < 3) {
					displayText = "hit!";
					moveRunner(1);
				} else if (hit < 6) {
					displayText = "Out!";
				} else {
					displayText = "Homerun!";
					moveRunner(4);
				}
				list.get(0).done = true;
			} else if (location == 3) {
				Hit newHit = new Hit(new Point(385, 660), panel, rightField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				// while(!newHit.done()) {
				// }
				if (hit < 3) {
					displayText = "hit!";
					moveRunner(1);
				} else if (hit < 6) {
					displayText = "Out!";
				} else {
					displayText = "Homerun!";
					moveRunner(4);
				}
				System.out.println("3");
				list.get(0).done = true;
			}

			else {
				System.out.print("missed");
				list.get(0).done = true;

			}
			clickCount = 0;
		}

	}

	public void moveRunner(int numBases) {
		Runner newRunner = new Runner(1, 0, panel);
		list.add(newRunner);
		newRunner.start();
		Runner curRunner;
		for (int i = 0; i < runnerCheck.length; i++) {
			if (runnerCheck[i]) {
				runnerCheck[i] = false;
				curRunner = new Runner(1, i + 1, panel);
				list.add(curRunner);
				curRunner.start();
				if (i == 2) {
					if (team == 1) {
						team1Score[curInning]++;
					} else {
						team2Score[curInning]++;
					}
				}
				if (i != 2) {
					runnerCheck[i + 1] = true;
				}
			}
		}
		runnerCheck[0] = true;
		panel.repaint();
	}

	public int contains(Point p) {
		if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 600 && p.y <= 600 + 40) {

			return 1;

		} else if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 640 && p.y <= 640 + 40) {
			return 2;

		} else if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 680 && p.y <= 680 + 40) {

			return 3;

		}
		return 0;

	}

	public static void main(String args[]) {

		Ball.loadBallPic();
		Hit.loadBallPic();

		// launch the main thread that will manage the GUI
		javax.swing.SwingUtilities.invokeLater(new BaseballGame());

	}
}
