package main;

import java.io.IOException;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Main extends JFrame {

	private Board b;
    public Main(int nbVie, int level, int scoreTotal, int scoreLevel) {
    	try {
			this.b = new Board(this, nbVie, level, scoreTotal, scoreLevel);
			add(b);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 440);
        setLocationRelativeTo(null);
        setTitle("Sheepteroids");
        setResizable(false);
        setVisible(true);
    }
    
    public void reinitialiser(int nbVie, int level, int scoreTotal, int scoreLevel) {
    	setVisible(false);
    	new Main(nbVie, level, scoreTotal, scoreLevel);
    }
    public static void main(String[] args) {
        new Main(3,2,0, 0);
    }
}