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

public class BaseballGame extends MouseAdapter implements Runnable, ActionListener, KeyListener {

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

	private int[] team1Score = new int[6];

	private int[] team2Score = new int[6];

	private boolean[] runnerCheck = new boolean[3];

	private Point2D.Double firstBase = new Point2D.Double(480, 475);
	private Point2D.Double secondBase = new Point2D.Double(390, 370);
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

	private JLabel labels[][] = new JLabel[3][7];

	private Object lock = new Object();

	private boolean power;

	// 4 hits
	// 2 singles , 2 doubles, 1 triple, 5 outs , 1 hr
	// 5 Point2D.Doubles for hits, 5 Point2D.Doubles for outs, 1 for home run
	private Point2D.Double[] leftField = new Point2D.Double[] {
			new Point2D.Double(175, 200), new Point2D.Double(200, 325),
			new Point2D.Double(270, 200), new Point2D.Double(150, 250),
			new Point2D.Double(250, 150),
			new Point2D.Double(250, 350), new Point2D.Double(200, 250), new Point2D.Double(250, 250),
			new Point2D.Double(280, 300), new Point2D.Double(225, 225), new Point2D.Double(275, 225),
			new Point2D.Double(200, 300),
			new Point2D.Double(125, 115) };

	// 5 Point2D.Doubles for hits, 5 Point2D.Doubles for outs, 1 for home run
	private Point2D.Double[] centerField = new Point2D.Double[] {
			new Point2D.Double(450, 250), new Point2D.Double(325, 225),
			new Point2D.Double(350, 175), new Point2D.Double(455, 165),
			new Point2D.Double(425, 100),
			new Point2D.Double(435, 240), new Point2D.Double(365, 230), new Point2D.Double(425, 165),
			new Point2D.Double(400, 200), new Point2D.Double(375, 300), new Point2D.Double(400, 250),
			new Point2D.Double(400, 150),
			new Point2D.Double(385, 0) };

	// 5 Point2D.Doubles for hits, 5 Point2D.Doubles for outs, 1 for home run
	private Point2D.Double[] rightField = new Point2D.Double[] {
			new Point2D.Double(570, 325), new Point2D.Double(500, 325),
			new Point2D.Double(600, 250), new Point2D.Double(500, 225),
			new Point2D.Double(520, 150),
			new Point2D.Double(520, 350), new Point2D.Double(520, 250), new Point2D.Double(570, 250),
			new Point2D.Double(530, 275), new Point2D.Double(550, 250), new Point2D.Double(535, 300),
			new Point2D.Double(500, 250),
			new Point2D.Double(660, 115) };

	private Image field;

	private int clickCount;

	private boolean draw;

	private int location;

	private boolean drawFielders;

	private boolean teamChange;

	private Color fielderColor;
	private Color runnerColor;

	private JPanel startMenu;
	private JComboBox colors;
	private JComboBox colors2;
	private JTextField team1Name;
	private JTextField team2Name;
	private JPanel startButton;
	private JButton start;
	private JPanel startPanel;

	private CardLayout layout;

	private JFrame frame;

	private JFrame startFrame;

	private JFrame optionFrame;

	private JButton continueGame;

	private JPanel optionPanel;

	private JButton skipInning;

	private JButton endGame;

	private String teamName1;
	private String teamName2;

	private Color team1Color;
	private Color team2Color;

	private boolean isOut;

	// Start BaseballGame object which will draw the BaseballGame as it grows and
	// allow us to get the final size of it
	// private Baseball b;

	public BaseballGame() {
		for (int i = 0; i < 7; i++) {
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
		layout = new CardLayout();

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
		frame = new JFrame("BaseballGame");
		frame.setPreferredSize(new Dimension(800, 800));

		startFrame = new JFrame("Baseball Options");
		startFrame.setPreferredSize(new Dimension(600, 600));
		startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		optionFrame = new JFrame("Game Paused");
		optionFrame.setPreferredSize(new Dimension(400, 400));
		optionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// tell the JFrame that when someone closes the
		// window, the application should terminate
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		team1Name = new JTextField("Enter Away Team Name");
		team2Name = new JTextField("Enter Home Team Name");
		String[] colorOptions = new String[] { "BLUE", "RED", "MAGENTA", "PINK", "GREEN", "ORANGE", "YELLOW" };
		colors = new JComboBox(colorOptions);
		colors2 = new JComboBox(colorOptions);

		startPanel = new JPanel(new GridLayout(2, 1));

		startMenu = new JPanel(new GridLayout(4, 2));
		startMenu.add(new JLabel("Away Team Options"));
		startMenu.add(new JLabel("Home Team Options"));
		startMenu.add(team1Name);
		startMenu.add(team2Name);
		startMenu.add(new JLabel("Select Color")).setPreferredSize(new Dimension(400, 20));
		startMenu.add(new JLabel("Select Color")).setPreferredSize(new Dimension(400, 20));

		startMenu.add(colors);
		startMenu.add(colors2);
		startFrame.add(startPanel);
		startButton = new JPanel();
		start = new JButton("Play Ball!");
		startPanel.add(startMenu);
		startPanel.add(startButton);

		startButton.add(start);

		start.addActionListener(this);
		team1Name.addActionListener(this);
		team2Name.addActionListener(this);
		colors2.addActionListener(this);
		colors.addActionListener(this);
		colors.setSelectedItem("BLUE");
		colors.setSelectedItem("RED");

		optionPanel = new JPanel(new GridLayout(4, 1));
		continueGame = new JButton("Resume");
		endGame = new JButton("End Game");
		skipInning = new JButton("Advance Inning");
		optionFrame.add(optionPanel);
		JLabel pause = new JLabel("Game Paused");
		pause.setHorizontalAlignment(JLabel.CENTER);
		optionPanel.add(pause);
		optionPanel.add(skipInning);
		optionPanel.add(endGame);
		optionPanel.add(continueGame);
		skipInning.addActionListener(this);
		endGame.addActionListener(this);
		continueGame.addActionListener(this);

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
				if (power == false) {
					// red - early - left
					g.setColor(new Color(230, 0, 0, 100));
					g.fillRect(369, 600, 60, 40);

					g.setColor(new Color(0, 0, 222, 100));
					g.fillRect(369, 680, 60, 40);
				}
				// Green - middle -
				g.setColor(new Color(0, 234, 0, 100));
				g.fillRect(369, 640, 60, 40);

				// blue - late - right

				// redraw each BaseballGame's contents, and along the
				// way, remove the ones that are done

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
				// checks if the list is empty, redraws the fielders and runner
				if (list.size() == 0) {
					draw = true;
					drawFielders = false;
					isOut = false;
					// checks if the hit required the runners to move more
					if (numBases > 0) {
						numBases--;
						moveRunner(1);
					}
					if (clickCount == 1) {
						clickCount = 0;	
						incrementStrike();				
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
					runnerColor = team1Color;
					fielderColor = team2Color;
				} else {
					runnerColor = team2Color;
					fielderColor = team1Color;
				}
				g.setColor(fielderColor);
				if (drawFielders) {

					if (location == 1 && isOut) {
						g.fillOval(390, 200, 20, 20);
						g.fillOval(550, 250, 20, 20);
					} else if (location == 2 & isOut) {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(550, 250, 20, 20);
					} else if (location == 3 & isOut) {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(390, 200, 20, 20);
					} else {
						g.fillOval(230, 250, 20, 20);
						g.fillOval(390, 200, 20, 20);
						g.fillOval(550, 250, 20, 20);
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
				g.drawString("Strikes: " + strikes, 0, 100);
				g.drawString("Controls", 0, 675);
				g.drawString("Pause - E", 0, 700);
				g.drawString("Power - P", 0, 725);
				g.drawString("Steal - S", 0, 750);
				

				for (int i = 0; i < 7; i++) {
					labels[0][i].setBackground(Color.white);
					labels[1][i].setBorder(BorderFactory.createLineBorder(Color.black));
					labels[2][i].setBorder(BorderFactory.createLineBorder(Color.black));
				}

				labels[0][curInning + 1].setBackground(Color.green);

				if (!teamChange) {
					if (team == 1) {
						labels[1][curInning + 1].setBorder(BorderFactory.createLineBorder(team1Color, 2));
					} else {
						labels[2][curInning + 1].setBorder(BorderFactory.createLineBorder(team2Color, 2));
					}
				} else {
					labels[1][curInning + 1].setBorder(BorderFactory.createLineBorder(team1Color, 2));
				}

			}
		};
		scoreBoard = new JPanel(new GridLayout(3, 7));
		scoreBoard.setOpaque(true);
		frame.add(panel);
		panel.add(scoreBoard, BorderLayout.NORTH);

		for (int i = 1; i < 6; i++) {
			labels[0][i].setText("" + i);
		}
		labels[0][6].setText("Runs");
		for (int i = 1; i < 7; i++) {
			labels[1][i].setText("" + 0);
		}
		for (int i = 1; i < 7; i++) {
			labels[2][i].setText("" + 0);
		}
		labels[0][0].setText("Inning");
		labels[1][0].setText("" + teamName1);
		labels[2][0].setText("" + teamName2);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 7; j++) {
				scoreBoard.add(labels[i][j]);
				labels[i][j].setBorder(BorderFactory.createLineBorder(Color.black));
				labels[i][j].setOpaque(true);
				labels[i][j].setBackground(Color.white);

			}
		}

		scoreBoard.setBorder(BorderFactory.createLineBorder(Color.black));

		panel.addMouseListener(this);
		frame.addKeyListener(this);

		// construct the list
		list = new ArrayList<AnimatedGraphicsObject>();

		// display the window we've created
		frame.pack();

		frame.setResizable(false);
		startFrame.pack();
		startFrame.setVisible(true);
		optionFrame.pack();

	}

	/**
	 * First click pitches the ball and the second click simulates a hit if you
	 * click while the ball is in the strike zone
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
			// mouse clicked for second time while ball is in screen

			Random r = new Random();

			location = contains(((Ball) list.get(0)).getLocation());

			int hit;
			drawFielders = true;
			if (power == true) {
				hit = r.nextInt(2) + 11;
			} else {
				hit = r.nextInt(13);
			}

			if (location == 1) {
				strikes = 0;
				Hit newHit = new Hit(new Point2D.Double(385, 600), panel, leftField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				if (hit < 2) {
					displayText = "Single!";
					moveRunner(1);
					numBases = 0;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				} else if (hit < 4) {
					displayText = "Double!";
					moveRunner(1);
					numBases = 1;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				} else if (hit == 4) {
					displayText = "Triple!";
					moveRunner(1);
					numBases = 2;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
				} else if (hit < 12) {
					incrementOut();
					displayText = "Out!";
					Fielder newFielder = new Fielder(LEFT_FIELDER, panel, leftField[hit], fielderColor);
					list.add(newFielder);
					newFielder.start();
					isOut = true;
				} else {
					displayText = "Homerun!";
					moveRunner(4);
					numBases = 3;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				}
				list.get(0).done = true;
				panel.repaint();

			} else if (location == 2) {
				strikes = 0;
				Hit newHit = new Hit(new Point2D.Double(385, 620), panel, centerField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				if (hit < 2) {
					displayText = "Single!";
					moveRunner(1);
					numBases = 0;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				} else if (hit < 4) {
					displayText = "Double!";
					moveRunner(1);
					numBases = 1;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				} else if (hit == 4) {
					displayText = "Triple!";
					moveRunner(1);
					numBases = 2;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
				} else if (hit < 12) {
					incrementOut();
					displayText = "Out!";
					Fielder newFielder = new Fielder(CENTER_FIELDER, panel, centerField[hit], fielderColor);
					list.add(newFielder);
					newFielder.start();
					isOut = true;
				} else {
					displayText = "Homerun!";
					moveRunner(4);
					numBases = 3;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				}
				list.get(0).done = true;
			} else if (location == 3) {
				strikes = 0;
				Hit newHit = new Hit(new Point2D.Double(385, 660), panel, rightField[hit]);

				list.add(newHit);
				newHit.start();
				panel.repaint();

				if (hit < 2) {
					displayText = "Single!";
					moveRunner(1);
					numBases = 0;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				} else if (hit < 4) {
					displayText = "Double!";
					moveRunner(1);
					numBases = 1;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				} else if (hit == 4) {
					displayText = "Triple!";
					moveRunner(1);
					numBases = 2;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;
				} else if (hit < 12) {
					incrementOut();
					displayText = "Out!";
					Fielder newFielder = new Fielder(RIGHT_FIELDER, panel, rightField[hit], fielderColor);
					list.add(newFielder);
					newFielder.start();
					isOut = true;
				} else {
					displayText = "Homerun!";
					moveRunner(4);
					numBases = 3;
					Runner newRunner = new Runner(runnerColor, 0, panel);
					list.add(newRunner);
					newRunner.start();
					runnerCheck[0] = true;

				}

				list.get(0).done = true;
			}

			else {

				list.get(0).done = true;
				incrementStrike();
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
						if (curInning != 5)
							team1Score[curInning]++;

						team1Score[5]++;
						labels[1][curInning + 1].setText("" + team1Score[curInning]);
						labels[1][6].setText("" + team1Score[5]);
					} else {
						if (curInning != 5)
							team2Score[curInning]++;

						team2Score[5]++;
						labels[2][curInning + 1].setText("" + team2Score[curInning]);
						labels[2][6].setText("" + team2Score[5]);

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

		int c = 0;

		if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 600 && p.y <= 600 + 40) {

			c = 1;

		} else if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 640 && p.y <= 640 + 40) {
			c = 2;

		} else if (p.x >= 369 && p.x <= 369 + 60 &&
				p.y >= 680 && p.y <= 680 + 40) {

			c = 3;

		}
		if (power) {
			if (c != 2) {
				c = 0;
			}
			else{
				Random r = new Random();
				c = r.nextInt(3) + 1;
			}
		}
		return c;

	}

	private void incrementOut() {
		if (outs != 2) {
			outs++;
		} else {
			teamChange = true;
			if (team == 2) {
				if (curInning < 4) {
					curInning++;
				} else {
					if (team1Score[5] > team2Score[5])
						displayText = teamName1 + "wins!";
					else if (team2Score[5] > team1Score[5])
						displayText = teamName2 + "wins!";
					else {
						displayText = "Game is tied! We're going to extras!";
						curInning = 5;
					}
				}

			} else {
				if (curInning == 4) {
					if (team2Score[5] > team1Score[5])
						displayText = teamName2 + "wins!";
				}
			}
			outs = 0;
			for (int i = 0; i < runnerCheck.length; i++) {
				runnerCheck[i] = false;
			}
		}
	}

	private void incrementStrike() {
		if (strikes != 2) {
			strikes++;
		} else {
			incrementOut();
			strikes = 0;

		}

	}

	public static void main(String args[]) {

		Ball.loadBallPic();
		Hit.loadBallPic();

		// launch the main thread that will manage the GUI
		javax.swing.SwingUtilities.invokeLater(new BaseballGame());

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(start)) {

			startFrame.setVisible(false);
			frame.requestFocus();
			frame.setVisible(true);

			String c1 = (String) colors.getSelectedItem();
			String c2 = (String) colors2.getSelectedItem();

			switch (c1) {
				case "BLUE":
					team1Color = Color.blue;
					break;
				case "RED":
					team1Color = Color.red;
					break;
				case "MAGENTA":
					team1Color = Color.magenta;
					break;
				case "PINK":
					team1Color = Color.pink;
					break;
				case "YELLOW":
					team1Color = Color.yellow;
					break;
				case "ORANGE":
					team1Color = Color.orange;
					break;
				case "GREEN":
					team1Color = Color.green;
					break;

			}
			switch (c2) {
				case "BLUE":
					team2Color = Color.blue;
					break;
				case "RED":
					team2Color = Color.red;
					break;
				case "MAGENTA":
					team2Color = Color.magenta;
					break;
				case "PINK":
					team2Color = Color.pink;
					break;
				case "YELLOW":
					team2Color = Color.yellow;
					break;
				case "ORANGE":
					team2Color = Color.orange;
					break;
				case "GREEN":
					team2Color = Color.green;
					break;

			}

			if (c1.equals(c2)) {
				team2Color = Color.cyan;
			}

			teamName1 = team1Name.getText();
			if (teamName1.equals("Enter Away Team Name"))
				teamName1 = "Away Team";
			labels[1][0].setText(teamName1);

			teamName2 = team2Name.getText();
			if (teamName2.equals("Enter Home Team Name"))
				teamName2 = "Home Team";
			labels[2][0].setText(teamName2);
		}

		if (e.getSource().equals(continueGame)) {
			frame.setVisible(true);
			optionFrame.setVisible(false);
		}
		if (e.getSource().equals(endGame)) {

		}
		if (e.getSource().equals(skipInning)) {
			outs = 2;
			incrementOut();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_S) {
			Random rand = new Random();
			int chance = rand.nextInt(10);

			if (runnerCheck[2]) {
				chance = 5;
			}
			if (list.size() == 0) {
				if (chance < 4) {
					numBases = 1;
				} else {
					for (int i = 2; i > 0; i--) {
						if (runnerCheck[i]) {
							incrementOut();
							runnerCheck[i] = false;
							break;
						}
						numBases = 1;
					}
				}
			}

			panel.repaint();

		} else if (e.getKeyCode() == KeyEvent.VK_E) {
			optionFrame.setVisible(true);
			frame.setVisible(false);

		}

		else if (e.getKeyCode() == KeyEvent.VK_P && list.size() == 0) {
			if (power == true) {
				power = false;
			} else {
				power = true;
			}
			panel.repaint();

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
