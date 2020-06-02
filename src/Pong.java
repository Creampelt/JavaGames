import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class Pong extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final double APPLICATION_WIDTH = 800;
	public static final double APPLICATION_HEIGHT = 600;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 10;
	private static final int PADDLE_HEIGHT = 60;

	/** Radius of the ball in pixels */
	private static final int BALL_DIAMETER = 10;

	private static final int PAUSE_TIME = 10;
	
	private static final int RESET_PAUSE_TIME = 500;
	
	private static final int PADDLE_X_OFFSET = 50;
	
	private static final int SCORE_X_OFFSET = 10;
	
	private static final int SCORE_Y_OFFSET = 30 + SCORE_X_OFFSET;
		
	private static final double BOUNCING_ERROR = 0.25;
	
	private static final int DOTTED_LINE_WIDTH = 5;
	
	private static final int DOTTED_LINE_HEIGHT = 10;
	
	private static final int SPACE_BETWEEN_DOTS = 5;
	
	private static final double ORIGINAL_SPEED = 3.0;
	
	private static final int MAX_SCORE = 8;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	boolean running = false;
	boolean paddleOneMoving = false;
	boolean paddleTwoMoving = false;
	
	int paddleSpeed = 5;
	int paddleOneDirection = 1;
	int paddleTwoDirection = 1;
	
	int winner;
	
	int playerOneScore = 0;
	int playerTwoScore = 0;
	
	GRect paddleOne, paddleTwo;
	GOval ball;
	GImage trophy = new GImage("../drawables/rotating_trophy.gif");
	GObject collidingObjectTopLeft, collidingObjectTopRight, collidingObjectBottomLeft,
			collidingObjectBottomRight;
	
	GLabel startGameLabel, scoreOne, scoreTwo, replayMenuLabel;
	
	HashMap<Integer, Boolean> keyStatus = new HashMap<Integer, Boolean>();
	
	double xSpeed = ORIGINAL_SPEED;
	double ySpeed = 0;
	double originalSpeed = ORIGINAL_SPEED;
	double bouncingError = xSpeed / 2;
	
	int xDirection = -1;
	int yDirection = 1;
	
	public void run() {
		addKeyListeners();
		addMouseListeners();
		setUp();
		while (!running) {
			pause(PAUSE_TIME);
		}
		while (true) {
			runGame();
			endSetUp();
			while (!running) {
				pause(PAUSE_TIME);
			}
			removeAll();
			reSetUp();
		}
	}
	
	public void reSetUp() {
		setUp();
		remove(startGameLabel);
	}
	
	public void runGame() {
		runningSetUp();
		while (running) {
			movePaddle();
			moveBall();
		}
	}
	
	public void endSetUp() {
		if (ball != null) {
			remove(ball);
		}
		createTrophy();
		createReplayLabel();
	}
	
	public void createTrophy() {
		if (winner == 1) {
			trophy.setLocation(APPLICATION_WIDTH / 4 - trophy.getWidth() / 2, APPLICATION_HEIGHT / 2
					- trophy.getHeight() / 2);
		} else {
			trophy.setLocation(APPLICATION_WIDTH - APPLICATION_WIDTH / 4 - trophy.getWidth() / 2,
					APPLICATION_HEIGHT / 2 - trophy.getHeight() / 2);
		}
		add(trophy);
	}
	
	public void createReplayLabel() {
		replayMenuLabel = new GLabel("Replay Game");
		
		replayMenuLabel.setFont(new Font("Serif Plain", 0, 50));
		replayMenuLabel.setLocation(APPLICATION_WIDTH / 2 - replayMenuLabel.getWidth() / 2,
				APPLICATION_HEIGHT / 2 + replayMenuLabel.getAscent() / 2);
		
		replayMenuLabel.setColor(Color.WHITE);
		
		add(replayMenuLabel);
	}
	
	public void keyPressed(KeyEvent e) {
		if (keyStatus.get(e.getKeyCode()) != null) {
			keyStatus.put(e.getKeyCode(), true);
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if (keyStatus.get(e.getKeyCode()) != null) {
			keyStatus.put(e.getKeyCode(), false);
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		if (startGameLabel != null) {
			if (getElementAt(e.getX(), e.getY()) == startGameLabel) {
				startGameLabel.setColor(new Color(215, 215, 215));
			} else {
				startGameLabel.setColor(Color.WHITE);
			}
		}
		if (replayMenuLabel != null) {
			if (getElementAt(e.getX(), e.getY()) == replayMenuLabel) {
				replayMenuLabel.setColor(new Color(215, 215, 215));
			} else {
				replayMenuLabel.setColor(Color.WHITE);
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if (startGameLabel != null && getElementAt(e.getX(), e.getY()) == startGameLabel) {
			running = true;
			remove(startGameLabel);
		} else if (replayMenuLabel != null && getElementAt(e.getX(), e.getY()) == replayMenuLabel) {
			running = true;
			remove(replayMenuLabel);
			remove(trophy);
		}
	}
	
	private void setUp() {
		setBackground(Color.BLACK);
		keySetUp();
		createDottedLine();
		createPaddles();
		createStartGameLabel();
	}
	
	private void runningSetUp() {
		createBall();
		createScoreLabels();
		playerOneScore = 0;
		playerTwoScore = 0;
		scoreOne.setLabel("" + playerOneScore);
		scoreTwo.setLabel("" + playerTwoScore);
	}
	
	private void createStartGameLabel() {
		startGameLabel = new GLabel("Start Game");
		
		startGameLabel.setFont(new Font("Serif Plain", 0, 64));
		startGameLabel.setLocation(APPLICATION_WIDTH / 2 - startGameLabel.getWidth() / 2,
				APPLICATION_HEIGHT / 2 + startGameLabel.getAscent() / 2);
		
		startGameLabel.setColor(Color.WHITE);
		
		add(startGameLabel);
	}
	
	private void keySetUp() {
		keyStatus.put(87, false); // adds 'w' key to keyStatus
		keyStatus.put(83, false); // adds 's' key to keyStatus
		keyStatus.put(38, false); // adds up arrow key to keyStatus
		keyStatus.put(40, false); // adds down arrow key to keyStatus
	}
	
	private void createDottedLine() {
		for (int i = 0; i < APPLICATION_HEIGHT / (DOTTED_LINE_HEIGHT + SPACE_BETWEEN_DOTS); i++) {
			GRect dot = new GRect(APPLICATION_WIDTH / 2 - DOTTED_LINE_WIDTH / 2, i *
					(DOTTED_LINE_HEIGHT + SPACE_BETWEEN_DOTS), DOTTED_LINE_WIDTH, DOTTED_LINE_HEIGHT);
			
			dot.setFilled(true);
			dot.setFillColor(Color.WHITE);
			
			add(dot);
		}
	}
	
	private void createPaddles() {
		paddleOne = new GRect(PADDLE_X_OFFSET, APPLICATION_HEIGHT / 2 - PADDLE_HEIGHT / 2,
				PADDLE_WIDTH, PADDLE_HEIGHT);
		
		paddleTwo = new GRect(APPLICATION_WIDTH - PADDLE_X_OFFSET - PADDLE_WIDTH, APPLICATION_HEIGHT /
				2 - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
		
		paddleOne.setFilled(true);
		paddleTwo.setFilled(true);
		
		paddleOne.setFillColor(Color.WHITE);
		paddleTwo.setFillColor(Color.WHITE);
		
		add(paddleOne);
		add(paddleTwo);
	}
	
	private void createBall() {
		ball = new GOval(APPLICATION_WIDTH / 2 - BALL_DIAMETER / 2, APPLICATION_HEIGHT / 2 -
				BALL_DIAMETER / 2, BALL_DIAMETER, BALL_DIAMETER);
		
		ball.setFilled(true);
		ball.setFillColor(Color.WHITE);
		
		add(ball);
	}
	
	private void createScoreLabels() {
		scoreOne = new GLabel("" + playerOneScore);
		scoreTwo = new GLabel("" + playerTwoScore);
		
		scoreOne.setFont(new Font("Serif Plain", 0, 40));
		scoreTwo.setFont(new Font("Serif Plain", 0, 40));
		
		scoreOne.setLocation(SCORE_X_OFFSET, SCORE_Y_OFFSET);
		scoreTwo.setLocation(APPLICATION_WIDTH - SCORE_X_OFFSET - scoreTwo.getWidth(),
				SCORE_Y_OFFSET);
		
		scoreOne.setColor(Color.WHITE);
		scoreTwo.setColor(Color.WHITE);
		
		add(scoreOne);
		add(scoreTwo);
	}
	
	private void movePaddle() {
		if (paddleOne.getY() >= paddleSpeed && keyStatus.get(87) == true && keyStatus.get(83) ==
				false) {
			paddleOneDirection = -1;
			paddleOne.move(0, paddleSpeed * paddleOneDirection);
		}
		if (paddleOne.getY() + PADDLE_HEIGHT <= APPLICATION_HEIGHT - paddleSpeed && keyStatus.get(83)
				== true && keyStatus.get(87) == false) {
			paddleOneDirection = 1;
			paddleOne.move(0, paddleSpeed * paddleOneDirection);
		}
		if (paddleTwo.getY() >= paddleSpeed && keyStatus.get(38) == true && keyStatus.get(40) ==
				false) {
			paddleTwoDirection = -1;
			paddleTwo.move(0, paddleSpeed * paddleTwoDirection);
		}
		if (paddleTwo.getY() + PADDLE_HEIGHT <= APPLICATION_HEIGHT - paddleSpeed && keyStatus.get(40)
				== true && keyStatus.get(38) == false) {
			paddleTwoDirection = 1;
			paddleTwo.move(0, paddleSpeed * paddleTwoDirection);
		}
	}
	
	private void moveBall() {
		ball.move(xSpeed * xDirection, ySpeed * yDirection);
		if (ball.getY() <= paddleSpeed) {
			yDirection = 1;
		} else if (ball.getY() + BALL_DIAMETER >= APPLICATION_HEIGHT - paddleSpeed) {
			yDirection = -1;
		}
		if (ball.getX() <= 0) {
			if (playerTwoScore < 8) {
				playerTwoScore += 1;
				scoreTwo.setLabel("" + playerTwoScore);
				resetToMiddle();
			} else {
				playerTwoScore += 1;
				scoreTwo.setLabel("" + playerTwoScore);
				running = false;
				winner = 2;
			}
		} else if (ball.getX() + BALL_DIAMETER >= APPLICATION_WIDTH) {
			if (playerOneScore < MAX_SCORE) {
				playerOneScore += 1;
				scoreOne.setLabel("" + playerOneScore);
				resetToMiddle();
			} else {
				playerOneScore += 1;
				scoreOne.setLabel("" + playerOneScore);
				running = false;
				winner = 1;
			}
		}
		
		checkCorners();
		
		pause(PAUSE_TIME);
	}
	
	private void resetToMiddle() {
		ball.setLocation(APPLICATION_WIDTH / 2 - BALL_DIAMETER / 2, APPLICATION_HEIGHT / 2 -
				BALL_DIAMETER / 2);
		
		xSpeed = ORIGINAL_SPEED;
		ySpeed = 0.0;
		
		pause(RESET_PAUSE_TIME);
	}
	
	private void checkCorners() {
		collidingObjectTopLeft = getElementAt(ball.getX(), ball.getY());
		collidingObjectTopRight = getElementAt(ball.getX() + BALL_DIAMETER, ball.getY());
		collidingObjectBottomLeft = getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER);
		collidingObjectBottomRight = getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() +
				BALL_DIAMETER);
		
		if (collidingObjectTopLeft == paddleOne || collidingObjectBottomLeft == paddleOne) {
			collidingX(1);
		} else if (collidingObjectTopRight == paddleTwo || collidingObjectBottomRight == paddleTwo) {
			collidingX(-1);
		}
	}
	
	private void collidingX(int direction) {
		xDirection = direction;
		ySpeed = rgen.nextDouble(xSpeed - BOUNCING_ERROR, xSpeed + BOUNCING_ERROR);
		if (ySpeed < 0) {
			ySpeed *= -1;
			yDirection *= -1;
		}
		if (xSpeed < 0) {
			xSpeed *= -1;
			xSpeed *= -1;
		}
		double speedEquilizer = Math.pow(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2), 0.5) /
				originalSpeed;
		xSpeed /= speedEquilizer;
		ySpeed /= speedEquilizer;
	}
}