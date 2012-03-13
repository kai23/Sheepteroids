//TODO : Gestion scores, écrans accueil, apparaitre JIDE quelque part, changer vaisseau ?
package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
/**
 * La Classe Board est le tableau de contrôle de notre application C'est ici que
 * tout va se faire.
 * 
 * @author kai
 * 
 */
@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {

	private Timer timer; // Pour savoir quand on va « repeindre »

	// Nos éléments
	private Vaisseau vaisseau;
	private ArrayList<Mouton> mouton;
	private ArrayList<Missile> ms;
	private Image image;
	
	// Savoir si on est en jeu, ou pas.
	boolean ingame;
	private int B_WIDTH;
	private int B_HEIGHT;

	// Les constantes de notre application
	private final int VITESSEJEU = 20;
	private int NBMOUTONS    ;
	private static final int NBMOUTONSDEDOUBLEMENT = 2;
	private int nbVie;
	
	private int scoreLevel = 0;
 	private int scoreTotal;
	Main m;
	/**
	 * Le constructeur. C'est peut-être pas hyper malin d'avoir le throws
	 * IOException ici, mais au moins le code est plus clair.
	 * 
	 * 
	 * @throws IOException
	 *             : pour savoir si les fichiers chargés sont bien lus.
	 */
	public Board(Main m, int nbVie, int level, int scoreTotal, int scoreLevel) throws IOException {
		// La fen�tre
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(new Color(0, 102, 204));
		setDoubleBuffered(true);
		setSize(600, 440);
		
		this.scoreLevel = scoreLevel;
		this.scoreTotal = scoreTotal;
		this.NBMOUTONS = level;
		this.nbVie = nbVie;
		this.m = m;
		// On cr�e le vaisseau
		vaisseau = new Vaisseau();

		// ... et nos moutons
		initMouton();

		// On lance la partie
		ingame = true;

		// Toutes les 5 ms, on va faire appel � actionPerformed
		timer = new Timer(VITESSEJEU, this);
		timer.start();
		
		 ImageIcon ii = new ImageIcon(this.getClass().getResource("NBVie.png"));
	     this.image = ii.getImage();

	}

	/**
	 * L'initialisation de nos moutons, concrêtement, le remplissage de notre
	 * liste de mouton
	 * 
	 * @throws IOException
	 */
	public void initMouton() throws IOException {
		mouton = new ArrayList<Mouton>();
		for (int i = 0; i < NBMOUTONS; i++) {
			mouton.add(new Mouton(1.0, 0, 0));
		}
	}

	/**
	 * C'est ici qu'on définit ce qui va être repeint.
	 */
	public void actionPerformed(ActionEvent e) {
		// On récupère les missiles
		ms = vaisseau.getMissiles();

		// Pour chaque missile
		for (int i = 0; i < ms.size(); i++) {

			// On le r�cup�re
			Missile m = (Missile) ms.get(i);

			// S'il est visible
			if (m.isVisible())
				// On le bouge
				m.move();
			else
				// Sinon, on le supprime de la liste
				ms.remove(i);
		}

		// On fait exactement la même chose avec les moutons
		for (int i = 0; i < mouton.size(); i++) {
			Mouton a = (Mouton) mouton.get(i);
			if (a.isVisible())
				a.move();
			else
				mouton.remove(i);
		}

		// On vérifie s'il y a collision
		try {
			checkCollisions();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Si besoin, on bouge notre vaisseau
		vaisseau.move();

		// Et c'est reparti pour un tour !
		repaint();

	}

	/**
	 * Fonction permettant de vérifier s'il y a une collision ou pas. Elle est
	 * appellée à chaque tour de boucle
	 */
	private void checkCollisions() throws IOException {

		// On va se servir des rectangles pour définir si oui ou non on a
		// collision
		Rectangle r1 = vaisseau.getBounds();

		// Collision entre VAISSEAU - MOUTON
		for (int j = 0; j < mouton.size(); j++) {
			Mouton a = (Mouton) mouton.get(j);
			Rectangle r2 = a.getBounds();
			if (r1.intersects(r2)) {
				vaisseau.setVisible(false);
				a.setVisible(false);
				nbVie = nbVie - 1;
				for (int i = 0; i < mouton.size(); i++) {
					mouton.remove(i);
				}
				ingame = false; // C'est perdu !
			}
		}

		ms = vaisseau.getMissiles();
		boolean boucle = true; // Pour éviter de faire un break;

		// Collision entre missile - mouton
		for (int i = 0; i < ms.size(); i++) {
			Missile m = (Missile) ms.get(i);
			Rectangle r3 = m.getBounds();

			for (int j = 0; boucle && j < mouton.size(); j++) {
				Mouton a = (Mouton) mouton.get(j);
				Rectangle r4 = a.getBounds();

				// Si on a une collision
				if (r3.intersects(r4)) {
					m.setVisible(false);
					a.setVisible(false);

					// On définit une nouvelle taille
					double nouvelleTaille = a.getScaleX() / 2;

					// On crée nos moutons
					for (int k = 0; k < NBMOUTONSDEDOUBLEMENT; k++) {
						mouton.add(new Mouton(nouvelleTaille, a.getX(), a
								.getY()));
					}

					// Et pour éviter de se taper une exception de type
					// ConcurrentModificationException
					// On sort de la boucle
					scoreLevel += 10;
					scoreTotal += 10;
					boucle = false;
				}
			}

		}

	}

	/**
	 * Fonction permettant d'obtenir la taille de la fenêtre.
	 */
	public void addNotify() {
		super.addNotify();
		B_WIDTH = getWidth();
		B_HEIGHT = getHeight();
	}

	/**
	 * C'est la qu'on va commencer à dessiner !
	 */
	public void paint(Graphics g) {
		super.paint(g);
		if (ingame) {
			if (mouton.size() == 0) {
				String msg = "C'est gagné ! Appuyez sur espace pour aller au level   " + NBMOUTONS;
				Font small = new Font("Helvetica", Font.BOLD, 14);
				FontMetrics metr = this.getFontMetrics(small);

				g.setColor(Color.white);
				g.setFont(small);
				g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2,
						B_HEIGHT / 2);
				g.drawString("Score level " + scoreLevel, (B_WIDTH - metr.stringWidth(msg)) / 2,
						B_HEIGHT / 4);
				g.drawString("Score total " + scoreTotal, (B_WIDTH - metr.stringWidth(msg)) / 3,
						B_HEIGHT / 3);
			} else {
				// Pour plus de possibilité, on cast nos graphiques en
				// graphiques 2D
				Graphics2D g2d = (Graphics2D) g;

				// On dessine le vaisseau
				vaisseau.draw(g2d);

				// On r�cup�re les missiles
				ms = vaisseau.getMissiles();

				// On dessine chaque missile
				for (Missile missile : ms) {
					missile.draw(g2d);
				}

				// On dessine tous les moutons également
				for (Mouton moutmout : mouton) {
					moutmout.draw(g2d);
				}

				g.setColor(Color.WHITE);
				
				// Le score du level
				String score = Integer.toString(scoreLevel);
				String scoreTotalSring = Integer.toString(scoreTotal);
				g.drawString(score, 560, 370);
				g.drawString(scoreTotalSring, 560, 390);
				
				// Le level
				int level = NBMOUTONS-1;
				g.drawString("Lv " + level, 80, 395);
				
				// Le nombre de vie    
				for(int i = 0; i < nbVie ; i++) {
					g.drawImage(image, 20 + i * 20, 380, null);
				}
				
			}
 
		} else if (!ingame && nbVie > 0) {
				String msg = "Plus que " + nbVie +" restant !";
				String recommencer = "Appuyez sur ESPACE pour continuer";
				g.setFont(new Font("Calibri", Font.BOLD, 25));
				g.drawString(msg, B_WIDTH / 2,
						B_HEIGHT / 2);
				g.drawString(recommencer, 100, 100);
		}
		else if (!ingame && nbVie == 0) {
				for (int i = 0; i < mouton.size(); i++) {
					mouton.remove(i);
				}
				String msg = "Game Over";
				String recommencer = "Appuyez sur ESPACE pour recommencer";
				
				Font small = new Font("Helvetica", Font.BOLD, 14);
				FontMetrics metr = this.getFontMetrics(small);
				
				g.setColor(Color.white);
				g.setFont(small);
				g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2,
						B_HEIGHT / 2);
				g.drawString(recommencer, 100, 100);
				g.drawString("Score total " + scoreTotal, 200, 200);  
			}
		

		// Pour plus de fluidité sous Linux
		Toolkit.getDefaultToolkit().sync();

		// Et on relâche
		g.dispose();
	}

	/**
	 * Pour les touches sur lesquelles on appuie
	 * 
	 * @author kai
	 * 
	 */
	private class TAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			try {
				vaisseau.keyPressed(e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			int key = e.getKeyCode();
			
			// Dans le cas d'un game over
			if (!ingame && nbVie == 0 && key == KeyEvent.VK_SPACE) {
				m.reinitialiser(3, 2, 0, 0);
			}
			
			// Dans le cas d'un c'est gagné
			if (ingame && mouton.size() == 0 && key == KeyEvent.VK_SPACE) {
				m.reinitialiser(3, NBMOUTONS + 1, scoreTotal, 0);
			}
			
			// Dans le cas d'une perte de vie
			if (!ingame && nbVie > 0 && key == KeyEvent.VK_SPACE) {
				m.reinitialiser(nbVie, NBMOUTONS, scoreTotal - scoreLevel, 0);
			}
		}

		public void keyReleased(KeyEvent e) {
			try {
				vaisseau.keyReleased(e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
