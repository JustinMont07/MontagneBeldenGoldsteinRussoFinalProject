import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.geom.Point2D;

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

	private Point2D.Double firstBase = new Point2D.Double(480, 475);
	private Point2D.Double secondBase = new Point2D.Double(385, 365);
	private Point2D.Double thirdBase = new Point2D.Double(290, 475);
	private Point2D.Double homePlate = new Point2D.Double(385, 585);

	private static final Point2D.Double LEFT_FIELDER = new Point2D.Double(230, 250);
	private static final Point2D.Double CENTER_FIELDER = new Point2D.Double(390, 200);
	private static final Point2D.Double RIGHT_FIELDER = new Point2D.Double(550, 250);

	private int outs;

	private int strikes;

	private int team;
	private int curInning;

	private int numBases;

	private JLabel labels[][] = new JLabel[3][6];

	private Object lock = new Object();

	// 5 Point2D.Doubles for hits, 5 Point2D.Doubles for outs, 1 for home run
	private Point2D.Double[] leftField = new Point2D.Double[] { new Point2D.Double(175, 200),
			new Point2D.Double(250, 150), new Point2D.Double(200, 325),
			new Point2D.Double(250, 350), new Point2D.Double(200, 250), new Point2D.Double(250, 250),
			new Point2D.Double(125, 115) };

	// 5 Point2D.Doubles for hits, 5 Point2D.Doubles for outs, 1 for home run
	private Point2D.Double[] centerField = new Point2D.Double[] { new Point2D.Double(450, 250),
			new Point2D.Double(340, 250), new Point2D.Double(385, 100),
			new Point2D.Double(385, 150), new Point2D.Double(385, 300), new Point2D.Double(400, 250),
			new Point2D.Double(385, 0) };

	// 5 Point2D.Doubles for hits, 5 Point2D.Doubles for outs, 1 for home run
	private Point2D.Double[] rightField = new Point2D.Double[] { new Point2D.Double(520, 150),
			new Point2D.Double(570, 325), new Point2D.Double(600, 200),
			new Point2D.Double(520, 350), new Point2D.Double(520, 250), new Point2D.Double(570, 250),
			new Point2D.Double(660, 115) };

	private Image field;

	private int clickCount;

	private boolean draw;

	private int location;

	private boolean drawFielders;

	private boolean teamChange;

	private Color fielderColor;
	private Color runnerColor;

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
		for (int j = 0; j < 3; j++) {
			runnerCheck[j] = false;
		}
		numBases = 0;
		draw = false;
		drawFielders = false;
		team = 1;
		curInning = 0;
		teamChange = false;
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

				synchronized (lock) {
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
				}
				if (list.size() == 0) {
					draw = true;
					drawFielders = false;

					if (numBases > 0) {
						numBases--;
						moveRunner(1);
					}
				}

				if (teamChange) {
					if (list.size() == 0) {
						if (team == 1)
							team = 2;
						else
							team = 1;
						teamChange = false;
					}
				}

				if (team == 1) {
					runnerColor = Color.blue;
					fielderColor = Color.red;
				} else {
					runnerColor = Color.red;
					fielderColor = Color.blue;
				}
				g.setColor(fielderColor);
				if (drawFielders) {

					if (location == 0) {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(390, 200, 20, 20);
						g.fillOval(550, 250, 20, 20);
					} else if (location == 1) {
						g.fillOval(390, 200, 20, 20);
						g.fillOval(550, 250, 20, 20);
					} else if (location == 2) {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(550, 250, 20, 20);
					} else {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(390, 200, 20, 20);
					}
				} else {
					g.fillOval(230, 250, 20, 20);
					g.fillOval(390, 200, 20, 20);
					g.fillOval(550, 250, 20, 20);
				}

				g.setColor(runnerColor);
				if (draw) {

					if (runnerCheck[0]) {
						g.fillOval((int) firstBase.x, (int) firstBase.y, 20, 20);
					}
					if (runnerCheck[1]) {
						g.fillOval((int) secondBase.x, (int) secondBase.y, 20, 20);
					}
					if (runnerCheck[2]) {
						g.fillOval((int) thirdBase.x, (int) thirdBase.y, 20, 20);
					}
				}

				// Checks if list is empty and that the user isn't currently making ,a
				// BaseballGame. Displays a message in
				// the center of window telling you how to make a new BaseballGame

				g.setColor(Color.black);
				FontMetrics str = g.getFontMetrics();
				Font newFont = new Font("arial", Font.BOLD, 18);
				int pHeight = this.getHeight();
				int pWidth = this.getWidth();
				int strWidth = str.stringWidth(displayText);
				int ascent = (str.getAscent());
				g.setFont(newFont);
				if (list.size() != 0) {
					g.drawString(displayText, pWidth / 2 - (strWidth / 2), pHeight / 2 -
							ascent);
				} else {
					displayText = "";
				}

				g.drawString("Outs: " + outs, 0, 75);

				for (int i = 0; i < 6; i++) {
					labels[0][i].setBorder(BorderFactory.createLineBorder(Color.black));
					labels[1][i].setBorder(BorderFactory.createLineBorder(Color.black));
					labels[2][i].setBorder(BorderFactory.createLineBorder(Color.black));
				}
				labels[0][curInning + 1].setBorder(BorderFactory.createLineBorder(Color.green, 2));

				if (team == 1) {
					labels[1][curInning + 1].setBorder(BorderFactory.createLineBorder(Color.blue, 2));
				} else {
					labels[2][curInning + 1].setBorder(BorderFactory.createLineBorder(Color.red, 2));
				}

			}
		};
		scoreBoard = new JPanel(new GridLayout(3, 6));
		scoreBoard.setOpaque(true);
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
	 * centered at the press Point2D.Double.
	 * 
	 * @param e mouse event info
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		// ball drops from homebase
		if (list.size() == 0) {

			Ball newBall = new Ball(new Point2D.Double(433, 470), panel, 5);
			list.add(newBall);

			// calling start on the object that extends Thread results in
			// its run method being called once the operating system and
			// JVM have set up the thread
			newBall.start();
			panel.repaint();
			clickCount = 1;

		} else if (list.size() > 0 && clickCount == 1) {
			// have second hit

			Random r = new Random();

			location = contains(((Ball) list.get(0)).getLocation());

			drawFielders = true;
			int hit = r.nextInt(7);

			if (location == 1) {
				Hit newHit = new Hit(new Point2D.Double(385, 600), panel, leftField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				// while(!newHit.done()) {
				// }
				if (hit < 3) {
					displayText = "hit!";
					moveRunner(1);
					numBases = 0;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
					Fielder newFielder = new Fielder(LEFT_FIELDER, panel,
							new Point2D.Double(leftField[hit].x + LEFT_FIELDER.x / 2,
									leftField[hit].y + LEFT_FIELDER.y / 2),
							fielderColor);
					list.add(newFielder);
					newFielder.start();
					

				} else if (hit < 6) {
					incrementOut();
					displayText = "Out!";
					Fielder newFielder = new Fielder(LEFT_FIELDER, panel, leftField[hit], fielderColor);
					list.add(newFielder);
					newFielder.start();
				} else {
					displayText = "Homerun!";
					moveRunner(4);
					numBases = 3;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
					Fielder newFielder = new Fielder(LEFT_FIELDER, panel,
							new Point2D.Double(leftField[hit].x + LEFT_FIELDER.x / 2,
									leftField[hit].y + LEFT_FIELDER.y / 2),
							fielderColor);
					list.add(newFielder);
					newFielder.start();
					
				}
				list.get(0).done = true;

			} else if (location == 2) {

				Hit newHit = new Hit(new Point2D.Double(385, 620), panel, centerField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				// while(!newHit.done()) {
				// }
				if (hit < 3) {
					displayText = "hit!";
					moveRunner(1);
					numBases = 0;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
					Fielder newFielder = new Fielder(CENTER_FIELDER, panel,
							new Point2D.Double(centerField[hit].x + CENTER_FIELDER.x / 2,
									centerField[hit].y + CENTER_FIELDER.y / 2),
							fielderColor);
					list.add(newFielder);
					newFielder.start();
				} else if (hit < 6) {
					incrementOut();
					displayText = "Out!";
					Fielder newFielder = new Fielder(CENTER_FIELDER, panel, centerField[hit], fielderColor);
					list.add(newFielder);
					newFielder.start();
				} else {
					displayText = "Homerun!";
					moveRunner(4);
					numBases = 3;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
					Fielder newFielder = new Fielder(CENTER_FIELDER, panel,
							new Point2D.Double(centerField[hit].x + CENTER_FIELDER.x / 2,
									centerField[hit].y + CENTER_FIELDER.y / 2),
							fielderColor);
					list.add(newFielder);
					newFielder.start();
				}
				list.get(0).done = true;
			} else if (location == 3) {
				Hit newHit = new Hit(new Point2D.Double(385, 660), panel, rightField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				// while(!newHit.done()) {
				// }
				if (hit < 3) {
					displayText = "hit!";
					moveRunner(1);
					numBases = 0;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
					Fielder newFielder = new Fielder(RIGHT_FIELDER, panel,
							new Point2D.Double(rightField[hit].x + RIGHT_FIELDER.x / 2,
									rightField[hit].y + RIGHT_FIELDER.y / 2),
							fielderColor);
					list.add(newFielder);
					newFielder.start();

				} else if (hit < 6) {
					incrementOut();
					displayText = "Out!";
					Fielder newFielder = new Fielder(RIGHT_FIELDER, panel, rightField[hit], fielderColor);
					list.add(newFielder);
					newFielder.start();
				} else {
					displayText = "Homerun!";
					moveRunner(4);
					numBases = 3;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
					Fielder newFielder = new Fielder(RIGHT_FIELDER, panel,
							new Point2D.Double(rightField[hit].x + RIGHT_FIELDER.x / 2,
									rightField[hit].y + RIGHT_FIELDER.y / 2),
							fielderColor);
					list.add(newFielder);
					newFielder.start();
				}

				list.get(0).done = true;
			}

			else {

				list.get(0).done = true;

			}
			clickCount = 0;
		}

	}

	public void moveRunner(int numBases) {
		draw = false;
		boolean temp[] = new boolean[3];
		for (int i = runnerCheck.length - 1; i >= 0; i--) {
			if (runnerCheck[i]) {
				runnerCheck[i] = false;
				temp[i] = false;
				Runner curRunner = new Runner(runnerColor, i + 1, panel);
				list.add(curRunner);
				curRunner.start();
				if (i == 2) {
					if (team == 1) {
						team1Score[curInning]++;
						labels[1][curInning + 1].setText("" + team1Score[curInning]);
					} else {
						team2Score[curInning]++;
						labels[2][curInning + 1].setText("" + team2Score[curInning]);
					}
				}
				if (i != 2) {
					temp[i + 1] = true;
				}
			}
		}
		for (int i = 0; i < runnerCheck.length; i++) {
			if (temp[i]) {
				runnerCheck[i] = true;
				temp[i] = false;
			}
		}

		panel.repaint();
	}

	public int contains(Point2D.Double p) {
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

	private void incrementOut() {
		if (outs != 2) {
			outs++;
		} else {
			teamChange = true;
			if (team == 2) {
				if (curInning < 4) {
					curInning++;
				}

			}
			outs = 0;
			for (int i = 0; i < runnerCheck.length; i++) {
				runnerCheck[i] = false;
			}
		}
	}

	public static void main(String args[]) {

		Ball.loadBallPic();
		Hit.loadBallPic();

		// launch the main thread that will manage the GUI
		javax.swing.SwingUtilities.invokeLater(new BaseballGame());

	}
}
