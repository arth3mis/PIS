package a;

import processing.core.*;

public class vw1_prk_2_billard extends PApplet {
	public static void main(String[] args) {
		PApplet.runSketch(new String[]{""}, new vw1_prk_2_billard());
	}

	int border = 10;

	Ball[] balls = new Ball[8];

	int[] ballColors = new int[]{
		color(255, 205, 15),
		color(35, 50, 130),
		color(245, 55, 35),
		color(60, 40, 95),
		color(255, 110, 40),
		color(0, 100, 0),
		color(135, 10, 20),
		color(20, 17, 15)
	};

	public void settings() {
		size(800, 500);
	}

	public void setup() {
		//frameRate(60);
		for (int i = 0; i < balls.length; i++) {
			balls[i] = new Ball(ballColors[i % ballColors.length]);
		}
	}

	public void draw() {
		update();
		// draw
		background(0, 165, 60);
		stroke(150, 40, 10);
		strokeWeight(border*2);
		noFill();
		rect(0, 0, width, height);
		for (Ball b : balls) {
			b.draw();
		}
	}

	public void update() {
		for (Ball b : balls) {
			b.update();
			b.collision(balls);
		}
	}

	class Ball {
		float radius = 20;
		int colour;
		PVector p;
		PVector v;

		Ball(int c) {
			colour = c;
			// randomise initial position and velocity
			p = new PVector(
				random(border + radius, width - border - radius),
				random(border + radius, height - border - radius));
			v = new PVector(
				random(3, 10) * (random(-1, 1) > 0 ? 1 : -1),
				random(3, 10) * (random(-1, 1) > 0 ? 1 : -1));
		}

		void draw() {
			noStroke();
			fill(colour);
			ellipse(p.x, p.y, radius*2, radius*2);
		}

		void update() {
			p.add(v);
			// collision with wall?
			if (p.x <= border + radius) {
				p.x = border + radius;
				v.x *= -1;
			} else if (p.x >= width - border - radius) {
				p.x = width - border - radius;
				v.x *= -1;
			}
			if (p.y <= border + radius) {
				p.y = border + radius;
				v.y *= -1;
			} else if (p.y >= height - border - radius) {
				p.y = height - border - radius;
				v.y *= -1;
			}
		}

		void collision(Ball[] others) {
			for (Ball b : others) {
				if (b == this) continue;
				PVector bToThis = new PVector(p.x - b.p.x, p.y - b.p.y);
				// collision?
				if (bToThis.mag() <= radius + b.radius) {
					// set position
					bToThis.setMag(radius + b.radius - 0.1f);
					p.set(b.p.x + bToThis.x, b.p.y + bToThis.y);
					// deflect velocity
					PVector vel = new PVector(v.x, v.y);
					vel.setHeading(bToThis.heading());
					v = vel;
				}
			}
		}
	}
}