package main;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Notre classe Vaisseau, permettant de gérer tout le vaisseau
 * @author kai
 *
 */
public class Vaisseau  {

	// La vitesse du vaisseau :
	// inférieur à 1 va accélérer
	// supérieur à 1 va ralentir
	private static final double VITESSEVAISSEAU = 1;
	
	// La vitesse d'accélération
	// Plus la vitesse est haute, plus on pourra aller vite
	private static final double VITESSEACCELERATION = 8;
	
	// L'angle qui fera tourner le vaisseau.
	// Correspond à la vitesse de rotation
	private static final int ANGLE = 6;
	
	// Le temps entre deux tirs.
	private static final long VITESSETIR = 400;
	
	private long start;
	private long dernierTir;
	// L'image
	BufferedImage image;

	// Les variables de déplacement
	private double dx, dy;

	// Les positions
	private double x, y;

	// L'angle de rotation
	private int angle;

	// Notre liste de missiles
	private ArrayList<Missile> missiles;
	private boolean visible;

	// Pour appuyer sur plusieurs touches en même temps
	private boolean space, up, down, left, right;

	/**
	 * Constructeur de notre vaisseau
	 * @throws IOException
	 */
	public Vaisseau() throws IOException {
		
		// On initialise les missiles
		missiles = new ArrayList<Missile>();

		// On définit ce qu'est l'image
		this.image = ImageIO.read(getClass().getResource("vaisseau.png"));
		
		// Et les variables de départ
		angle = 0;
		x = 300; y = 220;
		dx = 0;  dy = 0;
		
		//
		dernierTir = System.currentTimeMillis();;
		// Puis on le rends visible
		visible = true;
		
	}

	/**
	 * Fonction permettant de dessiner un vaisseau
	 * @param g2d : le contexte graphique de l'application
	 */
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform(); // Pour faire des transformations sur notre vaisseau
		at.rotate(Math.toRadians(angle), x + image.getWidth(null) / 2, y
				+ image.getHeight(null) / 2);
		at.translate(x, y);
		g2d.drawImage(image, at, null);
	}

	/**
	 * Fonction permettant au vaisseau de créer un nouveau missile
	 */
	public void fire() {
		start = System.currentTimeMillis();
		System.out.println(start - dernierTir);
		if (start - dernierTir > VITESSETIR ) {
			dernierTir = System.currentTimeMillis();
			try {
				missiles.add(new Missile(x + image.getWidth(null) / 2, y
						+ image.getHeight(null) / 2, angle));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Fonction permettant de changer l'angle du vaisseau
	 * @param ang : l'angle qui va être changé
	 */
	public void tournerVaisseau(int ang) {
		this.angle += ang;
	}

	/**
	 * Fonction permettant de déplacer notre vaisseau
	 */
	public void deplacerVaisseau() {
		dx += Math.cos(2 * Math.PI * (angle - 90) / 360) / VITESSEVAISSEAU;
		dy += Math.sin(2 * Math.PI * (angle - 90) / 360) / VITESSEVAISSEAU;
		
		// Pour l'accéleration
		if (dy > VITESSEACCELERATION) {
			dy = VITESSEACCELERATION;
		}
		if (dy < -VITESSEACCELERATION) {
			dy = -VITESSEACCELERATION;
		}
		if (dx > VITESSEACCELERATION) {
			dx = VITESSEACCELERATION;
		}
		if (dx < -VITESSEACCELERATION) {
			dx = -VITESSEACCELERATION;
		}
	}

	/**
	 * Parce qu'il faut bien s'arrêter à un moment donné
	 */
	public void freiner() {
		dx *= 0.5;
		dy *= 0.5;
	}

	/**
	 * Permet de bouger le vaisseau
	 */
	public void move() {
		
		// En X
		x += dx;
		if (x > 550) {
			x -= 550;
		}
		if (x < 0) {
			x += 550;
		}
		
		// En Y
		y += dy;
		if (y > 400) {
			y -= 400;
		}
		if (y < 0) {
			y += 400;
		}
	}

	/**
	 * Qu'est-ce qu'il se passe si j'appuie sur une touche ?
	 * @param e : la touche sur laquelle on appuie
	 * @throws IOException 
	 */
	public void keyPressed(KeyEvent e) throws IOException {

		// On récupère la touche en question
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_SPACE) {
			space = true;
			fire();
		}

		if (key == KeyEvent.VK_LEFT) {
			left = true;
			tournerVaisseau(-ANGLE);
		}

		if (key == KeyEvent.VK_RIGHT) {
			right = true;
			tournerVaisseau(ANGLE);

		}

		if (key == KeyEvent.VK_UP) {
			up = true;
			deplacerVaisseau();
			this.image = ImageIO.read(getClass().getResource("VaisseauMove.png"));
		}

		if (key == KeyEvent.VK_DOWN) {
			// nouvelle position
			down = true;
			freiner();
		}

		doAction();
	}

	/**
	 * Qu'est ce qu'il se passe si je relache une touche ?
	 * @param e : la touche sur laquelle j'appuie
	 * @throws IOException 
	 */
	public void keyReleased(KeyEvent e) throws IOException {
		// On récupère la touche en question
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_SPACE) {
			space = false;
		}

		if (key == KeyEvent.VK_LEFT) {
			left = false;
		}

		if (key == KeyEvent.VK_RIGHT) {
			right = false;

		}

		if (key == KeyEvent.VK_UP) {
			this.image = ImageIO.read(getClass().getResource("vaisseau.png"));
			up = false;
		}

		if (key == KeyEvent.VK_DOWN) {
			down = false;
		}

	}

	/**
	 * Pour gérer les différents appuies de touches en même temps
	 */
	public void doAction() {
		if (space && left) {
			fire();
			tournerVaisseau(-6);
		} else if (space && right) {
			fire();
			tournerVaisseau(6);
		} else if (space && up) {
			fire();
			move();
		} else if (up && left) {
			move();
			tournerVaisseau(-6);
		} else if (up && right) {
			move();
			tournerVaisseau(6);
		} else if (down && right) {
			freiner();
			tournerVaisseau(6);
		} else if (down && left) {
			freiner();
			tournerVaisseau(-6);
		}
	}

	/**********************************************************************************************/
	/* LES GETTEURS / SETTEURS */

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public ArrayList<Missile> getMissiles() {
		return missiles;
	}

	public int getAngle() {
		if (angle > 359 || angle < -359)
			angle = 0;
		if (angle < 0)
			angle += 360;
		return angle;
	}

	/*
	 * Méthode permettant de retourner un rectangle, correspondant à notre
	 * vaisseau
	 */
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, image.getWidth(),
				image.getHeight());
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/*****************************************************************************************************/

}