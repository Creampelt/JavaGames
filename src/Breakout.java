import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Breakout extends GraphicsProgram {
	
	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 8;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 8;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
		(APPLICATION_WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_DIAMETER = 6;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	private static final int PAUSE_TIME = 1;
	
	private static final int BASE_PAUSE = 30;
	
	private static final double SPEED_INCREMENT = 1.1;
	
	private static final int X_PADDING = 10;
	
	private static final int Y_PADDING = 20;
	
	private static final double BOUNCING_ERROR = 0.25;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	GLabel titleLabel;
	GLabel startGame;
	GLabel scoreLabel;
	GLabel livesLabel;
	GLabel highScoreLabel;
	GLabel currentScoreLabel;
	GLabel replayLabel;
	
	GOval ball = null;
	GRect paddle = null;
	
	boolean running = false;
	int paddleHits = 0;
	int fullScore = 0;
	int highScore = 0;
	int lives = NTURNS;
	
	ArrayList<GRect> bricks = new ArrayList<GRect>();
	
	public void run() {
		addMouseListeners();
		setUp();
		while (true) {
			while (!running) {
				pause(PAUSE_TIME);
			}
			runProgram();
		}
	}
	
	private void setUp() {
		createStartLabel();
		createTitle();
		createBricks();
		createPaddle();
	}
	
	private void endSetUp() {
		createReplayLabel();
		createHighScoreLabel();
		createScoreLabel();
		remove(currentScoreLabel);
		remove(livesLabel);
		if (ball != null) {
			remove(ball);
		}
		resetVariables();
		paddle.setLocation(APPLICATION_WIDTH / 2 - paddle.getWidth() / 2, paddle.getY());
	}
	
	private void createTitle() {
		titleLabel = new GLabel("Breakout");
		
		titleLabel.setFont(new Font("Serif Plain", 0, 32));
		titleLabel.setLocation(APPLICATION_WIDTH / 2 - titleLabel.getWidth() / 2, startGame.getY()
				- startGame.getHeight() - X_PADDING);
		
		add(titleLabel);
	}
	
	private void removeBricks() {
		removeAll();
		createPaddle();
	}
	
	private void resetVariables() {
		running = false;
		fullScore = 0;
		paddleHits = 0;
		lives = NTURNS;
		brickIndex = 0;
	}
	
	private void createScoreLabel() {
		scoreLabel = new GLabel("" + fullScore);
		
		scoreLabel.setFont(new Font("Serif Plain", 0, 32));
		scoreLabel.setLocation(APPLICATION_WIDTH / 2 - scoreLabel.getWidth() / 2,
				highScoreLabel.getY() - highScoreLabel.getHeight() - X_PADDING);
		
		add(scoreLabel);
	}
	
	private void createHighScoreLabel() {
		highScoreLabel = new GLabel("High Score: " + highScore);
		
		highScoreLabel.setFont(new Font("Serif Plain", 0, 16));
		highScoreLabel.setLocation(APPLICATION_WIDTH / 2 - highScoreLabel.getWidth() / 2,
				replayLabel.getY() - replayLabel.getHeight() - X_PADDING);
		
		add(highScoreLabel);
	}
	
	private void createReplayLabel() {
		replayLabel = new GLabel("Replay");
		
		replayLabel.setFont(new Font("Serif Plain", 0, 16));
		replayLabel.setLocation(APPLICATION_WIDTH / 2 - replayLabel.getWidth() / 2,
				APPLICATION_HEIGHT / 2 + replayLabel.getHeight() / 2);
		
		add(replayLabel);
	}
	
	private void createStartLabel() {
		startGame = new GLabel("Start Game");
		
		startGame.setFont(new Font("Serif Plain", 0, 16));
		startGame.setLocation(APPLICATION_WIDTH / 2 - startGame.getWidth() / 2, APPLICATION_HEIGHT
				/ 2 + startGame.getHeight() / 2);
		
		add(startGame);
	}
	
	private void createBricks() {
		for (int i = 0; i < NBRICK_ROWS; i++) {
			for (int x = 0; x < NBRICKS_PER_ROW; x++) {
				GRect brick = new GRect(x * (BRICK_WIDTH + BRICK_SEP), i * (BRICK_HEIGHT +
						BRICK_SEP) + BRICK_Y_OFFSET, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				brick.setFillColor(new Color (i * 255 / NBRICK_ROWS, i * 255 / NBRICK_ROWS, i * 255
						/ NBRICK_ROWS));
				
				bricks.add(brick);
				add(brick);
			}
		}
	}
	
	private void createPaddle() {
		paddle = new GRect(APPLICATION_WIDTH / 2 - PADDLE_WIDTH / 2, APPLICATION_HEIGHT -
				PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		
		paddle.setFilled(true);
		paddle.setFillColor(Color.BLACK);
		
		add(paddle);
	}
	
	public void mouseMoved(MouseEvent e) {
		if (getElementAt(e.getX(), e.getY()) == startGame) {
			if (startGame != null) {
				startGame.setColor(Color.LIGHT_GRAY);
			}
		} else if (startGame != null) {
			startGame.setColor(Color.BLACK);
		}
		if (getElementAt(e.getX(), e.getY()) == replayLabel) {
			if (replayLabel != null) {
				replayLabel.setColor(Color.LIGHT_GRAY);
			}
		} else if (replayLabel != null) {
			replayLabel.setColor(Color.BLACK);
		}
		if (running) {
			paddle.setLocation(e.getX() - PADDLE_WIDTH / 2, paddle.getY());
			if (paddle.getX() > APPLICATION_WIDTH - PADDLE_WIDTH) {
				paddle.setLocation(APPLICATION_WIDTH - PADDLE_WIDTH, paddle.getY());
			} else if (paddle.getX() < 0) {
				paddle.setLocation(0, paddle.getY());
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if (getElementAt(e.getX(), e.getY()) == startGame) {
			remove(startGame);
			remove(titleLabel);
			running = true;
		} else if (getElementAt(e.getX(), e.getY()) == replayLabel) {
			remove(replayLabel);
			remove(scoreLabel);
			remove(highScoreLabel);
			removeBricks();
			createBricks();
			running = true;
		}
	}
	
	private void runProgram() {
		createBall();
		createCurrentScoreLabel();
		createLives();
		
		while (running) {
			runBall(ball);
		}
		if (fullScore > highScore) {
			highScore = fullScore;
		}
		endSetUp();
	}
	
	private void createCurrentScoreLabel() {
		currentScoreLabel = new GLabel("Score: 0");
		
		currentScoreLabel.setFont(new Font("Serif Plain", 0, 16));
		currentScoreLabel.setLocation(X_PADDING, Y_PADDING);
		
		add(currentScoreLabel);
	}
	
	private void createLives() {
		livesLabel = new GLabel("Lives: " + lives);
		
		livesLabel.setFont(new Font("Serif Plain", 0, 16));
		livesLabel.setLocation(APPLICATION_WIDTH - X_PADDING - livesLabel.getWidth(), Y_PADDING);
		
		add(livesLabel);
	}
	
	private void createBall() {
		ball = new GOval((APPLICATION_WIDTH - BALL_DIAMETER) / 2, (APPLICATION_HEIGHT -
				BALL_DIAMETER) / 2,BALL_DIAMETER, BALL_DIAMETER);
		
		ball.setFilled(true);
		ball.setFillColor(Color.RED);
		
		add(ball);
	}
	
	double xDirection = 0.0;
	double yDirection = 1.0;
	double speedEquilizer = 1;
	
	double originalSpeed = 5.0 * PAUSE_TIME / BASE_PAUSE;
	double ySpeed = 5.0 * PAUSE_TIME / BASE_PAUSE;
	double xSpeed = 0.0;
	
	double lastX;
	double lastY;
	
	boolean brickRemoved = false;
	int brickIndex = 0;
	
	private void runBall(GOval ball) {
		pause(1000);
		while (running) {
			brickRemoved = false;
			
			ball.move(xSpeed * xDirection, ySpeed * yDirection);
			pause(PAUSE_TIME);
			if (ball.getY() + BALL_DIAMETER >= APPLICATION_HEIGHT) {
				ball.setLocation((APPLICATION_WIDTH - BALL_DIAMETER) / 2, (APPLICATION_HEIGHT -
						BALL_DIAMETER) / 2);
				if (lives > 0) {
					lives -= 1;
				}
				livesLabel.setLabel("Lives: " + lives);
				if (lives > 0) {
					xSpeed = 0.0;
					fullScore -= 20;
					currentScoreLabel.setLabel("Score: " + fullScore);
					yDirection = 1;
					xDirection = 0.0;
					originalSpeed = 5.0 * PAUSE_TIME / BASE_PAUSE;
					ySpeed = 5.0 * PAUSE_TIME / BASE_PAUSE;
				} else {
					running = false;
					xSpeed = 0.0;
					yDirection = 1;
					xDirection = 0.0;
					originalSpeed = 5.0 * PAUSE_TIME / BASE_PAUSE;
					ySpeed = 5.0 * PAUSE_TIME / BASE_PAUSE;
					lastX = (APPLICATION_WIDTH - BALL_DIAMETER) / 2;
					lastY = (APPLICATION_HEIGHT - BALL_DIAMETER) / 2;
				}
				break;
			} else if (ball.getY() <= 0) {
				collidingY(1);
			}
			if (ball.getX() + BALL_DIAMETER >= APPLICATION_WIDTH || ball.getX() <= 0) {
				xDirection *= -1;
			}
			reactToCollision();
			
			lastX = ball.getX();
			lastY = ball.getY();
		}
	}
	
	private void reactToCollision() {
		GObject collidingObjectTopLeft = getCollidingObject(ball.getX(), ball.getY());
		GObject collidingObjectTopRight = getCollidingObject(ball.getX() + BALL_DIAMETER,
				ball.getY());
		GObject collidingObjectBottomLeft = getCollidingObject(ball.getX(), ball.getY() +
				BALL_DIAMETER);
		GObject collidingObjectBottomRight = getCollidingObject(ball.getX() + BALL_DIAMETER,
				ball.getY() + BALL_DIAMETER);
		if (collidingObjectTopLeft != currentScoreLabel && collidingObjectTopLeft != livesLabel &&
				collidingObjectTopRight != currentScoreLabel && collidingObjectTopRight !=
				livesLabel && collidingObjectBottomLeft != currentScoreLabel &&
				collidingObjectBottomLeft != livesLabel && collidingObjectBottomRight !=
				currentScoreLabel && collidingObjectBottomRight != livesLabel) {
			if (collidingObjectBottomLeft == paddle || collidingObjectBottomRight == paddle) {
				collidingY(-1);
				paddleHits += 1;
				if (originalSpeed <= 15 * BASE_PAUSE / PAUSE_TIME) {
					originalSpeed *= SPEED_INCREMENT;
				}
			} else if (collidingObjectTopLeft != paddle && collidingObjectTopRight != paddle) {
				reactToBrickCollision(collidingObjectTopLeft, 1, 1);
				reactToBrickCollision(collidingObjectTopRight, 1, -1);
				reactToBrickCollision(collidingObjectBottomLeft, -1, 1);
				reactToBrickCollision(collidingObjectBottomRight, -1, -1);
				
				addingToBrickIndex(collidingObjectTopLeft, collidingObjectTopRight,
						collidingObjectBottomLeft, collidingObjectBottomRight);
				
				if (brickIndex == NBRICKS_PER_ROW * NBRICK_ROWS) {
					running = false;
					xSpeed = 0.0;
					yDirection = 1;
					xDirection = 0.0;
					originalSpeed = 5.0 * PAUSE_TIME / BASE_PAUSE;
					ySpeed = 5.0 * PAUSE_TIME / BASE_PAUSE;
					lastX = (APPLICATION_WIDTH - BALL_DIAMETER) / 2;
					lastY = (APPLICATION_HEIGHT - BALL_DIAMETER) / 2;
				}
			}
		}
	}

	private void reactToBrickCollision(GObject collidingObject, int hDirection, int wDirection) {
		Color newColor = rgen.nextColor();
		
		double collidingBrickX;
		double collidingBrickY;
		double currentX = ball.getX();
		double currentY = ball.getY();
		double possibleDist;
		double cornerX;
		double cornerY;
		
		if (collidingObject != null) {
			ball.setFillColor(newColor);
			fullScore = fullScore + (10 - paddleHits);
			currentScoreLabel.setLabel("Score: " + fullScore);
			paddleHits = 0;
			
			if (hDirection == -1) {
				collidingBrickY = collidingObject.getY();
				cornerY = currentY + BALL_DIAMETER;
			} else {
				collidingBrickY = collidingObject.getY() + BRICK_HEIGHT;
				cornerY = currentY;
			}
			if (wDirection == -1) {
				collidingBrickX = collidingObject.getX();
				cornerX = currentX + BALL_DIAMETER;
			} else {
				collidingBrickX = collidingObject.getX() + BRICK_WIDTH;
				cornerX = currentX;
			}
			
			possibleDist = Math.pow(Math.pow(currentX - lastX, 2) + Math.pow(currentY - lastY, 2),
					0.5);
			
			cornerCollision(cornerX, collidingBrickX, possibleDist, cornerY, collidingBrickY,
					hDirection, wDirection);
			
			remove(collidingObject);
		}
	}
	
	private void collidingY(int direction) {
		yDirection = direction;
		xSpeed = rgen.nextDouble(xSpeed - BOUNCING_ERROR, xSpeed + BOUNCING_ERROR);
		if (xSpeed <= 0.0) {
			if (xDirection == 0) {
				xDirection = 1;
			}
			xDirection *= -1;
			xSpeed *= -0.5;
		} else if (xDirection == 0) {
			xDirection = 1;
		}
		if (ySpeed <= 0.0) {
			yDirection *= -1;
		}
		speedEquilizer = Math.pow(Math.pow(xSpeed, 2) + Math.pow(ySpeed, 2), 0.5) /
				originalSpeed;
		xSpeed /= speedEquilizer;
		ySpeed /= speedEquilizer;
	}
	
	private GObject getCollidingObject(double x, double y) {
		if (getElementAt(x, y) != null) {
			return getElementAt(x, y);
		} else {
			return null;
		}
	}
	
	private void cornerCollision(double cornerX, double collidingBrickX, double possibleDist, double
			cornerY, double collidingBrickY, double hDirection, double wDirection) {
		if ((cornerX >= collidingBrickX - possibleDist && cornerY >= collidingBrickY - possibleDist
				&& hDirection == 1 && wDirection == 1) || (cornerX <= collidingBrickX + possibleDist
				&& cornerY <= collidingBrickY + possibleDist && hDirection == -1 && wDirection ==
				-1) || (cornerX >= collidingBrickX - possibleDist && cornerY <= collidingBrickY +
				possibleDist && hDirection == -1 && wDirection == 1) || (cornerX <= collidingBrickX
				+ possibleDist && cornerY >= collidingBrickY - possibleDist && hDirection == 1 &&
				wDirection == -1)) {
			xDirection *= -1;
			yDirection *= -1;
		} else {
			if ((wDirection == 1 && cornerX >= collidingBrickX - possibleDist) || (wDirection == -1
					&& cornerX <= collidingBrickX + possibleDist)) {
				xDirection = wDirection;
			} else if ((hDirection == 1 && cornerY >= collidingBrickY - possibleDist) || (hDirection
					== -1 && cornerY <= collidingBrickY + possibleDist)) {
				yDirection = hDirection;
			}
		}
	}
	
	private int addingObjectsToBrickIndex(GObject collidingObjectOne, GObject
			collidingObjectTwo) {
		if (collidingObjectOne != null) {
			if (collidingObjectTwo != null) {
				if (collidingObjectOne == collidingObjectTwo) {
					return 1;
				} else {
					return 2;
				}
			} else {
				return 3;
			}
		} else if (collidingObjectTwo != null) {
			return 4;
		} else {
			return 0;
		}
	}
	
	private void addingToBrickIndex(GObject cornerOne, GObject cornerTwo, GObject cornerThree,
			GObject cornerFour) {
		if (addingObjectsToBrickIndex(cornerOne, cornerTwo) == 1) {
			brickIndex += 1;
		} else if (addingObjectsToBrickIndex(cornerOne, cornerTwo) == 2) {
			brickIndex += 2;
		} else if (addingObjectsToBrickIndex(cornerOne, cornerTwo) == 3) {
			if (addingObjectsToBrickIndex(cornerOne, cornerThree) == 1 ||
					addingObjectsToBrickIndex(cornerOne, cornerThree) == 3) {
				brickIndex += 1;
			} else {
				brickIndex += 2;
			}
		} else if (addingObjectsToBrickIndex(cornerOne, cornerTwo) == 4) {
			if (addingObjectsToBrickIndex(cornerTwo, cornerFour) == 1 ||
					addingObjectsToBrickIndex(cornerTwo, cornerFour) == 3) {
				brickIndex += 1;
			} else {
				brickIndex += 2;
			}
		} else if (addingObjectsToBrickIndex(cornerThree, cornerFour) == 1 ||
				addingObjectsToBrickIndex(cornerThree, cornerFour) == 3 ||
				addingObjectsToBrickIndex(cornerThree, cornerFour) == 4) {
			brickIndex += 1;
		} else if (addingObjectsToBrickIndex(cornerThree, cornerFour) == 2){
			brickIndex += 2;
		}
	}
}
