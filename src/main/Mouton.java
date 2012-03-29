package main;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import javax.imageio.ImageIO;

/**
 * Classe permettant de créer et gérer les moutons
 * 
 * @author kai
 * 
 */
public class Mouton extends Thread {

	// Attention, plus la vitesse est grande, plus la vitesse de rotation est
	// petite
	private static final double VITESSEROTATION = 50;
	private static final double VITESSEMOUTON = 1;
        
        private Board board;

	// Nos variables de placement
        // Passer les variables en transient
	private int x, y;
	double dy, dx, spin;
	double scaleX, scaleY;

	// Pour l'image
	private int width;
	private int height;
	private BufferedImage image;

	private int lower = 0;
	private int higher = 100;
	private int random = (int) (Math.random() * (higher - lower)) + lower;
	private boolean visible;

        //Boucle 1 du mouton
        public void run() {
            
            try
            {
                while(this.isVisible())
                {
                    this.move();
                    //board.repaint();
                    Thread.sleep(25);
                }
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        
	/**
	 * Notre constructeur de mouton
	 * 
	 * @param scale
	 *            : la taille du mouton
	 * @param x
	 *            : le placement en x.
	 * @param y
	 *            : le placement y
	 * @throws IOException
	 *             : si on ne trouve pas le fichier
	 */
	public Mouton(Board board, double scale, int x, int y) throws IOException {
		if (scale < 1) {
			this.x = x;
			this.y = y;
		} else {
			this.x = x + random;
			this.y = y + random;
		}
		int rand = (int) (Math.random() + 0.4);
		this.image = ImageIO.read(getClass().getResource("mouton" + rand + ".png"));
		this.width = (int) ((image.getWidth()) / (1 / scale));
		this.height = (int) ((image.getHeight()) / (1 / scale));

		this.scaleX = scale;
		this.scaleY = scale;

		dx = Math.random() * VITESSEMOUTON + 0.5;
		if (Math.random() < 0.5) {
			dx += -1;
		}
		dy = Math.random() * VITESSEMOUTON + 0.5;
		if (Math.random() < 0.5) {
			dy += 1;
		}
		visible = true;
                
                this.board = board;
	}

	/**
	 * Fonction permettant de dessiner un mouton
	 * 
	 * @param g2d
	 *            : le contexte graphique
	 */
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		at.rotate(this.spin / VITESSEROTATION, x + width / 2, y + height / 2);
		at.translate(x, y);
		at.scale(this.scaleX, this.scaleY);
		g2d.drawImage(image, at, null);
	}

	/**
	 * Fonction permettant de faire bouger le mouton
	 */
	public void move() {
		this.x += this.dx;
		if (this.x > 550) {
			this.x = 0;
		}
		if (this.x < 0) {
			this.x = 550;
		}
		this.y += this.dy;
		if (this.y > 400) {
			this.y = 0;
		}
		if (this.y < 0) {
			this.y = 400;
		}
		this.spin += 1;
		if (this.spin > 360 || this.spin < 0) {
			this.spin = 0;
		}
	}

	/* Les getteurs / setteurs */

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getSpin() {
		return spin;
	}

	public void setSpin(double spin) {
		this.spin = spin;
	}

	public double getScaleX() {
		return scaleX;
	}

	public void setScaleX(double scaleX) {
		this.scaleX = scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public void setScaleY(double scaleY) {
		this.scaleY = scaleY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

}
