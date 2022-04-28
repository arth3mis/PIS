package a.vw1;

import processing.core.*;

public class vw1_prk_3_starsky extends PApplet {
	public static void main(String[] args) {
		PApplet.runSketch(new String[]{""}, new vw1_prk_3_starsky());
	}

	int border = 0;

	Star[] stars = new Star[100];
	int blinkTimer;
	Tree tree;

	int[] starColors = new int[]{
		color(255, 255, 255),
		color(255, 255, 255),
		color(255, 255, 255),
		color(255, 255, 255),
		color(230, 230, 230),
		color(200, 200, 200),
		color(230, 230, 255),
		color(255, 255, 230)
	};

	public void settings() {
		size(800, 700);
	}

	public void setup() {
		//frameRate(60);
		for (int i = 0; i < stars.length; i++) {
			stars[i] = new Star(starColors[i % starColors.length]);
		}
		blinkTimer = round(random(5));
		tree = new Tree();
	}

	public void draw() {
		update();
		// draw
		background(0, 0, 30);
		stroke(150, 40, 10);
		strokeWeight(border*2);
		noFill();
		rect(0, 0, width, height);
		for (Star b : stars) {
			b.draw();
		}
		tree.draw();
	}

	public void update() {
		for (Star b : stars) {
			b.update(stars);
		}
		blinkTimer--;
		if (blinkTimer <= 0) {
			Star s;
			do {
				int i = (int)random(stars.length);
				s = stars[i];
			} while (s.blinking > 0);
			s.reverseBlink = false;
			s.blinkSpeed = random(0.03f, 0.06f);
			s.blinking = s.blinkSpeed/10;
			blinkTimer = round(random(frameRate/2, frameRate*2));
		}
		tree.update();
	}

	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == UP) {
				tree.v.y = -tree.speed;
			} else if (keyCode == DOWN) {
				tree.v.y = tree.speed;
			}
		}
	}

	public void keyReleased() {
		if (key == CODED) {
			if (keyCode == UP || keyCode == DOWN) {
				tree.v.y = 0;
			}
		}
	}

	class Tree {
		PImage img = loadImage("tree.png");
		PVector imgSize = new PVector(444, 468);
		float factor = 0.7f;
		PVector size;
		float MAX_Y;
		PVector p;
		PVector v;
		float speed = 1.5f;

		Tree() {
			size = new PVector(imgSize.x, imgSize.y);
			size.mult(factor);
			MAX_Y = height - size.y + 20 * factor;
			p = new PVector(width/2 - size.x/2, MAX_Y);
			v = new PVector();
		}

		void draw() {
			tint(230,180,255);
			image(img, p.x, p.y, size.x, size.y);
			// in the air?
			//if (p.y < MAX_Y) {
			//}
		}

		void update() {
			p.add(v);
			if (p.y < -20 * factor)
				p.y = -20 * factor;
			if (p.y > MAX_Y)
				p.y = MAX_Y;
		}
	}

	class Star {
		float radius;
		int colour;
		PVector p;
		PVector v;

		float blinking, blinkSpeed;
		boolean reverseBlink;

		Star(int c) {
			colour = c;
			// randomise size, initial position and velocity
			radius = random(1, 3);
			p = new PVector(
				random(border + radius, width - border - radius),
				random(border + radius, height - border - radius));
			v = new PVector(
				random(0.2f) * (random(-1, 1) > 0 ? 1 : -1),
				random(0.2f) * (random(-1, 1) > 0 ? 1 : -1));
		}

		void draw() {
			noStroke();
			fill(colour);
			ellipse(p.x, p.y, radius*2, radius*2);
			if (blinking > 0)
				drawBlink();
		}

		void drawBlink() {
			pushMatrix();
			translate(p.x, p.y);
			rotate(-PI/7 + QUARTER_PI * blinking);
			float size = radius*0.8f;
			float len = radius*7;
			if (reverseBlink) {
				len *= 2 - blinking;
			} else {
				len *= blinking;
			}
			fill(255);
			beginShape();
			curveVertex(len, 0);
			curveVertex(len, 0);
			curveVertex(size, size);
			curveVertex(0, len);
			curveVertex(0, len);
			vertex(0, len);
			curveVertex(0, len);
			curveVertex(0, len);
			curveVertex(-size, size);
			curveVertex(-len, 0);
			curveVertex(-len, 0);
			vertex(-len, 0);
			curveVertex(-len, 0);
			curveVertex(-len, 0);
			curveVertex(-size, -size);
			curveVertex(0, -len);
			curveVertex(0, -len);
			vertex(0, -len);
			curveVertex(0, -len);
			curveVertex(0, -len);
			curveVertex(size, -size);
			curveVertex(len, 0);
			curveVertex(len, 0);
			endShape();
			popMatrix();
		}

		void update(Star[] others) {
			p.add(v);
			// blink
			if (blinking > 2) blinking = 0;
			if (blinking > 1) reverseBlink = true;
			if (blinking > 0) blinking += blinkSpeed;
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
			// collision with other stars
			for (Star b : others) {
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