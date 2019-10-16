package main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Classe permettant de créer un missile
 * @author kai
 *
 */
public class Missile extends Thread {

	// Le coefficient de tir
	private transient double x, y;
        
        private Board board;

	// La direction du tir
	private transient double dx, dy;

	public boolean toDie = false;

	// Notre image
	private BufferedImage image;
	boolean visible;
	private int width, height;
        
                //Boucle 1 du mouton
        public void run() {
            
            try
            {
                while(this.isVisible())
                {
                    this.move();
                    Thread.sleep(25);
                }
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
            }
        }

	/**
	 * Le constructeur de missile
	 * @param x : son placement en x
	 * @param y : son placement en y
	 * @param angle : l'angle du missile
	 * @throws IOException : si on ne trouve pas le fichier
	 */
	public Missile(Board board, double x, double y, int angle) throws IOException {
		
		this.image = ImageIO.read(getClass().getResource("missile.png"));
		this.width = image.getWidth();
		this.height = image.getHeight();
		
		this.x = x;
                this.y = y;
                this.dx = 10 * Math.cos(2 * Math.PI * (angle - 90) / 360);
                this.dy = 10 * Math.sin(2 * Math.PI * (angle - 90) / 360);
		visible = true;
                
                this.board = board;
	}
	
	/**
	 * Méthode permettant de dessiner le missile
	 * @param g2d : le contexte graphique de l'application
	 */
	public void draw(Graphics2D g2d) {
		AffineTransform at = new AffineTransform();
		at.translate(x, y);
		g2d.drawImage(image, at, null);
	}

	/**
	 * Méthode permettant de déplacer les missiles
	 */
	public void move() {
		x += dx;
		y += dy;

		if (this.x > 600 || this.x < -10 || this.y > 450 || this.y < -10) {
                    this.toDie = true;
		}
                
	}

	/* Les getteurs / setteurs */
	public Image getImage() {
		return image;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public double getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}
