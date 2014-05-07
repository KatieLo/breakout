/**
 * Breakout Game
 *
 * This file contains the class Breakout, which is an animated, asynchronous interactive game.
 * The constants referred to in this file can be found in the BreakoutConstants.java file. 
 * The game features a wall of multicolored bricks, which need to be eliminated by hitting the bricks with the ball, 
 * using the paddle. The paddle moves left and right when the mouse is moved. The game is won by clearing all the bricks, and lost 
 * if the ball hits the bottom of the window three times. 
 * 
 */

import acm.graphics.*;     // GOval, GRect, etc.
import acm.program.*;      // GraphicsProgram
import acm.util.*;         // RandomGenerator
import java.applet.*;      // AudioClip
import java.awt.*;         // Color
import java.awt.color.ColorSpace;
import java.awt.event.*;   // MouseEvent
import java.math.*;

public class Breakout extends GraphicsProgram implements BreakoutConstants {
	
	/**
	 * Fields for the velocity of the ball
	 */
	double xVelocity = 3.0; 
	double yVelocity = 3.0;
	
	/**
	 * Field for the number of turns the player has left
	 */
	int turnsRemaining = NTURNS;
	
	/**
	 * Fields for the animated ball and the moving paddle
	 */
	private GRect movingPaddle;
	private GOval ball;
	
	
	public void run() {
		xVelocity = randomVelocity();
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);	
		setBricks();
		buildMovingPaddle();
		addMouseListeners();
		bouncingBall();
	}
	
	/**
	 * Generates a random velocity between VELOCITY_MIN and VELOCITY_MAX, excluding
	 * -1 to 1 because they make the game too easy.
	 * @return a random velocity between VELOCITY_MIN and VELOCITY_MAX, excluding the range -1 to 1. 
	 */
	private double randomVelocity(){
		RandomGenerator moveRandom = RandomGenerator.getInstance();
		double velocity = moveRandom.nextDouble(VELOCITY_MIN, VELOCITY_MAX);
		double randomDirection = moveRandom.nextDouble(-1.0, 1.0);
		if (randomDirection < 0){
			velocity *= -1;
		}
		return velocity;
	}
	
	
	// Set Up Bricks 
	/**
	 * Prints a wall of bricks with NBRICK_ROWS rows and NBRICKS_PER_ROW in each row.
	 */
	private void setBricks() {
		double x = (BOARD_WIDTH - NBRICKS_PER_ROW * (BRICK_WIDTH + BRICK_SEP))/2;
		double y = BRICK_Y_OFFSET;
		
		// Prints two rows of each color of bricks. 
		for(int i = 0; i < NBRICK_ROWS; i++){
			
			double yOffset = (BRICK_HEIGHT + BRICK_SEP) * i;
			// Sets bricks color sequence as red, orange, yellow, green, cyan
			Color brickColor = new Color(1);
			if (i % 10 == 0 || i % 10 == 1){
				brickColor = Color.RED;
			} else if(i % 10 == 2 || i % 10 == 3){
				brickColor = Color.ORANGE;
			} else if(i % 10 == 4 || i % 10 == 5){
				brickColor = Color.YELLOW;
			} else if(i % 10 == 6 || i % 10 == 7){
				brickColor = Color.GREEN;
			} else {
				brickColor = Color.CYAN;
			}
			
			for (int j = 0; j < NBRICKS_PER_ROW; j++ ){
				// Prints a row or bricks
				double xOffset = (BRICK_WIDTH + BRICK_SEP) * j;
				GRect brick = new GRect((x +  xOffset), (y + yOffset), BRICK_WIDTH, BRICK_HEIGHT );
				brick.setColor(brickColor);
				brick.setFilled(true);
				brick.setFillColor(brickColor);
				add(brick);
			}
		}
	}
	
	/**
	 * Builds a moving paddle in the bottom center of the window that follows the mouse movements in the x-direction.
	 */
		public void buildMovingPaddle(){
			double y = getHeight() - PADDLE_Y_OFFSET;
			double x = (getWidth() - PADDLE_WIDTH)/2;;
			movingPaddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
			movingPaddle.setColor(Color.BLACK);
			movingPaddle.setFilled(true);
			movingPaddle.setFillColor(Color.BLACK);
			add(movingPaddle, x, y);
			
		}
		
		/**
		 *  Repositions the paddle when the mouse is moved and prevents the paddle going outside the window.  	
		 */
		public void mouseMoved(MouseEvent e) {
			double mousePosition = e.getX();
			double xpos;
			if(mousePosition < PADDLE_WIDTH/2) {
				xpos = 0;
			}
			else if(mousePosition > getWidth() - (PADDLE_WIDTH)/2) {
				xpos = getWidth() - PADDLE_WIDTH;
			}
			else {
				xpos = mousePosition - (PADDLE_WIDTH/2);
			}
			movingPaddle.setLocation(xpos, getHeight() - PADDLE_Y_OFFSET);
		}
		
		
		/**
		 * Method creates and animates the bouncing ball.
		 * The ball moves around the screen according to xVelocity and yVelocity, bouncing off walls.
		 * If the ball hits a brick, the brick is removed. When the ball hits the last brick, the game is won.
		 * If the ball hits the middle of the paddle, its velocity is reversed. If it hits the edges, it's velocity increases in the opposite x-direction. 
		 * If the ball hits the bottom window, a turn is ended and after three turns, the game is lost. 
		 * The ball's velocity increases as the number of bricks remaining decreases.
		 */
		public void bouncingBall(){
			// Ball begins centered in the window
			double x =  (getWidth() - (BALL_RADIUS * 2))/2;
			double y = (getHeight() - (BALL_RADIUS * 2))/2;
			ball = new GOval((BALL_RADIUS * 2), (BALL_RADIUS * 2));
			ball.setColor(Color.BLACK);
			ball.setFilled(true);
			ball.setFillColor(Color.BLACK);
			add(ball, x, y);
			int brickCount = (NBRICKS_PER_ROW * NBRICK_ROWS);
			int numBricks = (NBRICKS_PER_ROW * NBRICK_ROWS);
			
			// Ball bouncing animation loop
			while (turnsRemaining > 0){
				ball.move(xVelocity, yVelocity);
				// As bricks are removed, the speed of the balls increases from the initial speed, DELAY, until twice the initial speed.
				double new_delay = (DELAY/(1 + ((numBricks - brickCount)/(double)numBricks)));
				pause(new_delay);
				
				GPoint ballLocation = ball.getLocation();
				double ballX = ballLocation.getX();
				double ballY = ballLocation.getY();	
				
				// If the ball hits the bottom window, a turn is ended. The next turn starts when the user clicks the mouse
				if(ballY >  (getHeight() - (BALL_RADIUS * 2))){
					turnsRemaining--;
					if (turnsRemaining > 0){
						GLabel message;
						if(turnsRemaining > 1){
							message = new GLabel("You have " + turnsRemaining + " lives left. Click to start a new turn.", 200, 150);

						} else {
							message = new GLabel("You have 1 life left. Click to start your last turn.", 200, 150);
						}
						message.setColor(Color.ORANGE);
						message.setFont("sansSerif-18");
						add(message, (getWidth() - message.getWidth())/2, (getHeight() - message.getHeight())/2);
						waitForClick();
						remove(message);
						ball.setLocation(x, y);
						xVelocity = randomVelocity();
					}
					
				}
				// Ball bounces off the top, left and right sides of the window
				if(ballX > (getWidth() - (BALL_RADIUS * 2)) || ballX < 0){
					xVelocity = xVelocity * (-1);
				}
				if(ballY < 0 ){
					yVelocity = yVelocity * (-1);
				}
				
				
				GObject objectHit = collider(ballX, ballY);	
				// If the ball hits the paddle, it is deflected. If it hits a brick, the brick is removed.
				if (objectHit != null){
					 if (objectHit == movingPaddle){
						 
						 GPoint paddleLocation = movingPaddle.getLocation();
						 double paddleEdge = paddleLocation.getX();
						 double rightEdgeOfBall = ballX + 2 * BALL_RADIUS;
						 double rightPaddleEdge = paddleEdge + PADDLE_WIDTH;
						 
						 // Prevents the ball from getting stuck in the paddle by reversing x-velocity when ball hits paddle side-on
						 if(rightEdgeOfBall == paddleEdge || rightPaddleEdge == ballX){
							 xVelocity = xVelocity * (-1);
						 } else { // normal hitting of paddle
							 // only reverse ball's y-velocity if it is traveling downwards
							 if(yVelocity > 0){
								 yVelocity = yVelocity * (-1); 
							 }
							 
							 // if the ball hits the left or right top edges of the paddle, it adds x-velocity
							 if ((ballX + BALL_RADIUS) < paddleEdge + (0.1 * PADDLE_WIDTH)){
								 xVelocity += -1;
							 } else if ((ballX + BALL_RADIUS) > paddleEdge + (0.9 * PADDLE_WIDTH)){
								 xVelocity += 1;
							 }
						 }
				
					}  else {
						// If the ball hit something that is not the paddle or window, it has hit a brick. Plays a hitting sound when the ball hits a brick and removes the brick
						AudioClip bounceClip = MediaTools.loadAudioClip("res/bounce.au");
						bounceClip.play();
						remove(objectHit);
						brickCount--;
						if (brickCount == 0){
							break;
						}
						yVelocity = yVelocity * (-1);
					}
				}
			}
			if (brickCount == 0){
				// The user has won the game
				GLabel won = new GLabel("You've won!", 200, 150);
				won.setColor(Color.GREEN);
				won.setFont("sansSerif-36");
				add(won, (getWidth() - won.getWidth())/2, (getHeight() - won.getHeight())/2);
			
			} else {
				// The user has lost the game
				GLabel lost = new GLabel("Game Over, you've lost", 200, 150);
				lost.setColor(Color.RED);
				lost.setFont("sansSerif-36");
				add(lost, (getWidth() - lost.getWidth())/2, (getHeight() - lost.getHeight())/2);
			}
		}
				
		/**
		 * This method calculates if the ball has collided with an object. The ball's top left, top right, bottom let and bottom right corners are
		 * checked for collision with an object.
		 * @param It takes the ball's containing square's top-left x and y coordinates as parameters
		 * @return It returns the object the ball collided with, or null if there was no collision
		 */
		private GObject collider(double x, double y){
			GObject hitTopLeft = getElementAt(x, y);
			GObject hitTopRight = getElementAt(x + (BALL_RADIUS * 2), y);
			GObject hitBottomLeft = getElementAt(x, y  + (BALL_RADIUS * 2));
			GObject hitBottomRight = getElementAt(x + (BALL_RADIUS * 2), y + (BALL_RADIUS * 2));
			if (hitTopLeft != null){
				return hitTopLeft;
			} else if (hitTopRight != null){
				return hitTopRight;
			} else if (hitBottomLeft != null){
				return hitBottomLeft;
			} else {
				// hitBottomRight equals null if hitBottomRight is not hit, so if no object is hit, the function returns null
				return hitBottomRight; 
			}
		}
		
}
