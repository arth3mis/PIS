package a.vw1;

import processing.core.*;

public class vw1_prk_1_flyball extends PApplet {
	public static void main(String[] args) {
		PApplet.runSketch(new String[]{""}, new vw1_prk_1_flyball());
	}

	int ballColor = color(120, 10, 140);
	float ballRadius = 20;
	float px, py;
	float vx, vy;

	public void settings() {
		size(600, 400);
	}

	public void setup() {
		//frameRate(60);
		// randomise initial position and velocity
		px = random(ballRadius, width - ballRadius);
		py = random(ballRadius, height - ballRadius);
		//   ----speed----   ---------direction----------
		vx = random(3, 10) * (random(-1, 1) > 0 ? 1 : -1);
		vy = random(3, 10) * (random(-1, 1) > 0 ? 1 : -1);
		// fixed draw settings
		noStroke();
	}

	public void draw() {
		update();
		// draw
		background(200);
		fill(ballColor);
		ellipse(px, py, ballRadius*2, ballRadius*2);
	}

	public void update() {
		px += vx;
		py += vy;
		// collision with wall?
		if (px <= ballRadius) {
			px = ballRadius;
			vx *= -1;
		} else if (px >= width - ballRadius) {
			px = width - ballRadius;
			vx *= -1;
		}
		if (py <= ballRadius) {
			py = ballRadius;
			vy *= -1;
		} else if (py >= height - ballRadius) {
			py = height - ballRadius;
			vy *= -1;
		}
	}
}