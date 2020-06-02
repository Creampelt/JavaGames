import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class PlatformGame extends GraphicsProgram {
	
	public static final double ORIGINAL_PLAYER_HEIGHT = 200.0;
	public static final double ORIGINAL_PLAYER_WIDTH = 100.0;
	public static final double PLAYER_SIZE_FACTOR = 0.25;
	
	public static final double PLAYER_HEIGHT = ORIGINAL_PLAYER_HEIGHT * PLAYER_SIZE_FACTOR;
	public static final double PLAYER_WIDTH = ORIGINAL_PLAYER_WIDTH * PLAYER_SIZE_FACTOR;
	
	GImage player;
	
	HashMap<Integer, Boolean> keyStatus = new HashMap	<Integer, Boolean>();
	
	int startingX = 50;
	int startingY = 50;
	int playerDirection = 1;
	
	double playerSpeed = 10;
	
	boolean running;
	
	public void run() {
		addKeyListeners();
		setUp();
		running = true;
		runGame();
	}
	
	private void setUp() {
		player = new GImage("../drawables/player_standingstill.png");
		player.setLocation(startingX, startingY);
		player.setSize(PLAYER_WIDTH, PLAYER_HEIGHT);
		add(player);
	}
	
	private void runGame() {
		while (running) {
			movePlayer();
			pause(100);
		}
	}
	
	private void movePlayer() {
		if (keyStatus.get(65) == true) {
			playerDirection = -1;
			player.move(playerSpeed * playerDirection, 0);
		} else if (keyStatus.get(68) == true) {
			playerDirection = 1;
			player.move(playerSpeed * playerDirection, 0);
		}
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
	
	/*
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
	*/
}
