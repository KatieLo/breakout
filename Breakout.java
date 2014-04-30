// TODO: comment this program

import acm.graphics.*;     // GOval, GRect, etc.
import acm.program.*;      // GraphicsProgram
import acm.util.*;         // RandomGenerator
import java.applet.*;      // AudioClip
import java.awt.*;         // Color
import java.awt.color.ColorSpace;
import java.awt.event.*;   // MouseEvent
import java.math.*;

public class Breakout extends GraphicsProgram implements BreakoutConstants {
	
	// fields for ball velocity
	double xVelocity = 3.0; 
	double yVelocity = 3.0;
	
	// Number of turns left 
	int turnsRemaining = NTURNS;
	
	
	public void run() {
		xVelocity = randomVelocity();
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);	
		setBricks();
		addMouseListeners();
		bouncingBall();
	}
	
	// Random velocity Function
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
	private void setBricks() {
		double x = (BOARD_WIDTH - NBRICKS_PER_ROW * (BRICK_WIDTH + BRICK_SEP))/2;
		double y = BRICK_Y_OFFSET;
		
		// PRINT BRICKS - 2 ROWS OF EACH COLOR. starts at (x + brick-sep, y) and prints NBricks_per_row 
		for(int i = 0; i < NBRICK_ROWS; i++){
			double yOffset = (BRICK_HEIGHT + BRICK_SEP) * i;
			// sets bricks color sequence as red, orange, yellow, green, cyan with two rows of each color
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
				//prints a row or bricks
				double xOffset = (BRICK_WIDTH + BRICK_SEP) * j;
				GRect brick = new GRect((x +  xOffset), (y + yOffset), BRICK_WIDTH, BRICK_HEIGHT );
				brick.setColor(brickColor);
				brick.setFilled(true);
				brick.setFillColor(brickColor);
				add(brick);
			}
		}
	}
	
	// build paddle.
		public void init(){
			double y = getHeight() - PADDLE_Y_OFFSET;
			double x = (getWidth() - PADDLE_WIDTH)/2;;
			movingPaddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
			movingPaddle.setColor(Color.BLACK);
			movingPaddle.setFilled(true);
			movingPaddle.setFillColor(Color.BLACK);
			add(movingPaddle, x, y);
			
		}
		
		//called on mouse drag to reposition the paddle 	
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
		
		// Private instance variables (fields) used
		private GRect movingPaddle; // The paddle being dragged
		private GOval ball;
		
		// Create bouncing Ball
		public void bouncingBall(){
			double x =  (getWidth() - (BALL_RADIUS * 2))/2;
			double y = (getHeight() - (BALL_RADIUS * 2))/2;
			ball = new GOval((BALL_RADIUS * 2), (BALL_RADIUS * 2));
			ball.setColor(Color.BLACK);
			ball.setFilled(true);
			ball.setFillColor(Color.BLACK);
			add(ball, x, y);
			int brickCount = (NBRICKS_PER_ROW * NBRICK_ROWS);
			int numBricks = (NBRICKS_PER_ROW * NBRICK_ROWS);
			
			//ball bouncing animation loop [Main part of Game]
			while (turnsRemaining > 0){
				ball.move(xVelocity, yVelocity);
				double new_delay = (DELAY/(1 + ((numBricks - brickCount)/(double)numBricks)));
				pause(new_delay);
				
				GPoint ballLocation = ball.getLocation();
				double ballX = ballLocation.getX();
				double ballY = ballLocation.getY();	
				
				if(ballY >  (getHeight() - (BALL_RADIUS * 2))){
					turnsRemaining--;
					GLabel endTurn = new GLabel("You have " + turnsRemaining + " lives left. Click to start a new turn.", 200, 150);
					endTurn.setColor(Color.ORANGE);
					endTurn.setFont("sansSerif-18");
					GLabel endLastTurn = new GLabel("You have 1 life left. Click to start your last turn.", 200, 150);
					endLastTurn.setColor(Color.ORANGE);
					endLastTurn.setFont("sansSerif-18");
					if (turnsRemaining > 0){
						if(turnsRemaining > 1){
							add(endTurn, (getWidth() - endTurn.getWidth())/2, (getHeight() - endTurn.getHeight())/2);
						} else {
							add(endLastTurn, (getWidth() - endTurn.getWidth())/2, (getHeight() - endTurn.getHeight())/2);
						}
						waitForClick();
						remove(endTurn);
						remove(endLastTurn);
						ball.setLocation(x, y);
						xVelocity = randomVelocity();	
					}
					
				}
				if(ballX > (getWidth() - (BALL_RADIUS * 2)) || ballX < 0){
					xVelocity = xVelocity * (-1);
				}
				if(ballY < 0 ){
					yVelocity = yVelocity * (-1);
				}
				
				GObject objectHit = collider(ballX, ballY);	
				if (objectHit != null){
					 if (objectHit == movingPaddle){
						 yVelocity = yVelocity * (-1); 
						 GPoint paddleLocation = movingPaddle.getLocation();
						 double paddleEdge = paddleLocation.getX();
						 if ((ballX + BALL_RADIUS) < paddleEdge + (0.1 * PADDLE_WIDTH)){
							 xVelocity += -1;
						 } else if ((ballX + BALL_RADIUS) > paddleEdge + (0.9 * PADDLE_WIDTH)){
							 xVelocity += 1;
						 }
						
					}  else {
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
				// display "you won"
				GLabel won = new GLabel("You've won!", 200, 150);
				won.setColor(Color.GREEN);
				won.setFont("sansSerif-36");
				add(won, (getWidth() - won.getWidth())/2, (getHeight() - won.getHeight())/2);
			
			} else {
				//display "game over, you've lost"
				GLabel lost = new GLabel("Game Over, you've lost", 200, 150);
				lost.setColor(Color.RED);
				lost.setFont("sansSerif-36");
				add(lost, (getWidth() - lost.getWidth())/2, (getHeight() - lost.getHeight())/2);
			}
		}
				
			
		
		// Method to calculate if ball collides with an object 
		// It returns the object the ball collides with, or null if there was no collision. It takes in the ball's top-left
		// containing square's coordinates as parameters. 
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
				return hitBottomRight; // hitBottomRight == null if it is not hit, so if no object is hit, returns null
			}
		}
		
		
		
		
}
