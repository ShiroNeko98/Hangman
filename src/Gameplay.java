import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Gameplay extends JPanel implements KeyListener {
	
	private boolean charTyped;		// word is being written
	private boolean play;		// word was entered => ready to play
	private boolean gameOver;
	private boolean won;
	private boolean restart;

	private ArrayList<Character> letters;		// stores typed letters
	private Character guess;
	private Character[] uncover;
	private short tries;
	private ArrayList<Character> usedLetters;
	
	private BufferedImage background;
	private BufferedImage gallows1, gallows3, gallows5, gallowsLeft, gallowsRight;
	
	//HINT scores and multiplayer, rounds
	
	//private JButton resetButton;
	
	Gameplay() {
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		letters = new ArrayList<>();
		usedLetters = new ArrayList<>(30);
		
		loadImages();
		
		/* HINT add reset button
		resetButton = new JButton("RESET");
		resetButton.setBounds(0, 0, 80, 30);
		resetButton.setVisible(true);
		add(resetButton); */
	}
		
	@Override
	public void paint(Graphics g) {
		
		if(!play && !charTyped)	{	// no letter has been typed yet
			g.drawImage(background, 0, 0, null);
			drawBottomString("Set word and press [ENTER] to start", Color.GRAY, g);
		} else if(!play) {		// at least one letter was typed
			g.drawImage(background, 0, 0, null);
			
			/* display typed letters */
			String str = "";
			for(Character c : letters)
				str += c.toString();
			drawBottomString(str, Color.GRAY, g);
		} else if(charTyped) {		// to be guessed word was entered
			g.drawImage(background, 0, 0, null);
			
			/* draw gallows, if a false guess happened */
			if(tries != 0)
				drawGallows(g);

			/* display guessed letter */
			if(guess == null)
				drawGuess("", g);
			else drawGuess(guess.toString(), g);
			
			/* display already used letters */
			drawUsedLetters(g);
						
			if(gameOver == false) {		// display covered and uncovered letters
				String str = "";
				for(int i = 0; i < uncover.length; i++)
					if(uncover[i] == null)
						str += "*";
					else str += uncover[i].toString();
				drawBottomString(str, Color.GRAY, g);
			} else {		// reveal remaining covered letters in case of a lost			
				String helpStr = "";
				for(int i = 0; i < letters.size(); i++)
					helpStr += letters.get(i).toString();
				int xStr = getXStr(helpStr, g);
				
				String str = "";
				for(int i = 0; i < uncover.length; i++) {
					for(int j = i; j > 0; j--)
						str += " ";
					if(uncover[i] == null) {
						str += letters.get(i).toString();
						reveal(str, Color.RED, xStr, g);
						str = "";
					} else {							
						str += uncover[i].toString();
						reveal(str, Color.GRAY, xStr, g);
						str = "";
					}
				}
								
				/* display GAME OVER text*/
				String gameOverStr = null;
				String pressToRestart = "Press [R] to restart";
				
				g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 35));
				FontMetrics fm = g.getFontMetrics();
				int x = (int)(getWidth()*0.05);
				int y = (int)(getHeight()*0.2);
				
				if(won) {
					g.setColor(Color.GREEN);
					gameOverStr = "YOU WIN";
				} else {
					g.setColor(Color.RED);
					gameOverStr = "YOU LOSE";
				}
				g.drawString(gameOverStr, x, y);
				
				g.setColor(Color.WHITE);
				g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
				FontMetrics fm2 = g.getFontMetrics();
				x += (fm.stringWidth(gameOverStr) - fm2.stringWidth(pressToRestart))/2;
				y += (int)(getHeight()*0.05);
				g.drawString(pressToRestart, x, y);

				/* restart the game */
				if(restart) {
					g.dispose();
					restart();
				}
			}
		}
		
		g.dispose();
	}
		
	/** 
	 * @param str ... text to be drawn at the bottom of the screen 
	 * @return x position of the first letter
	 */
	private void drawBottomString(String str, Color c, Graphics g) {
		g.setColor(c);
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		FontMetrics fm = g.getFontMetrics();
		int x = (getWidth() - fm.stringWidth(str)) / 2;
		int y = (int)(getHeight()*0.965);
		g.drawString(str, x, y);
	}
		
	/** @param str ... text to be drawn at the right-center of the screen */
	private void drawGuess(String str, Graphics g) {
		g.setColor(Color.GRAY);
		g.setFont(new Font("Serif", Font.BOLD, 50));
		FontMetrics fm = g.getFontMetrics();
		int x = (int)(getWidth() * 0.8);
		int y = (int)(fm.getAscent() + (getHeight()*0.6 - (fm.getAscent() + fm.getDescent())/2));
		g.drawString(str, x, y);
	}
	
	/** draw used letters at the right-top of the screen */
	private void drawUsedLetters(Graphics g) {
		g.setColor(Color.GRAY);
		g.setFont(new Font("Serif", Font.BOLD, 25));
		FontMetrics fm = g.getFontMetrics();
		int x = (int)(getWidth()*0.97);
		int y = (int)(getHeight()*0.15);
		
		String help = "";
		if(usedLetters.size() > 0 || usedLetters.size() > 6) {
			for(int i = 0; i < usedLetters.size() && i < 6; i++)
				help += usedLetters.get(i).toString() + " ";
			g.drawString(help, x-fm.stringWidth(help), y);
			help = "";
		}
		if(usedLetters.size() > 6 || usedLetters.size() > 12) {
			for(int i = 6; i < usedLetters.size() && i < 12; i++)
				help += usedLetters.get(i).toString() + " ";
			y += fm.getHeight()*0.75;
			g.drawString(help, x-fm.stringWidth(help), y);
			help = "";
		}
		if(usedLetters.size() > 12 || usedLetters.size() > 18) {
			for(int i = 12; i < usedLetters.size() && i < 18; i++)
				help += usedLetters.get(i).toString() + " ";
			y += fm.getHeight()*0.75;
			g.drawString(help, x-fm.stringWidth(help), y);
			help = "";
		}
		if(usedLetters.size() > 18 || usedLetters.size() > 24) {
			for(int i = 18; i < usedLetters.size() && i < 24; i++)
				help += usedLetters.get(i).toString() + " ";
			y += fm.getHeight()*0.75;
			g.drawString(help, x-fm.stringWidth(help), y);
			help = "";
		}
		if(usedLetters.size() > 24) {
			for(int i = 24; i < usedLetters.size(); i++)
				help += usedLetters.get(i).toString() + " ";
			y += fm.getHeight()*0.75;
			g.drawString(help, x-fm.stringWidth(help), y);
			help = "";
		}
	}
	
	/**
	 * Use for reveal(String,Color,int,Graphics)-method
	 *  
	 * @return x position of str */
	private int getXStr(String str, Graphics g) {
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		FontMetrics fm = g.getFontMetrics();
		return (getWidth() - fm.stringWidth(str)) / 2;
	}
	
	/** reveals remaining letters */
	private void reveal(String str, Color c, int x, Graphics g) {
		g.setColor(c);
		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
		int y = (int)(getHeight()*0.965);
		g.drawString(str, x, y);
	}
	
	/** draws gallows */
	private void drawGallows(Graphics g) {
		/* 1st false guess */
		int x = (int)(getWidth()*0.35);
		int y = (int)(getHeight()*0.61);
		g.drawImage(gallows1, x, y, 80, 45, null);
		if(tries == 1) return;
		
		/* 2nd false guess */
		final short width2 = 10;
		final short height2 = 150;
		x += gallows1.getWidth()/2 - width2/2;
		y -= height2;
		g.fillRect(x, y, width2, height2);
		if(tries == 2) return;
		
		/* 3rd false guess */
		final short width3 = 100;
		g.fillRect(x, y, width3, 10);
		g.drawImage(gallows3, x, y, 35, 35, null);
		if(tries == 3) return;
		
		/* 4th false guess */
		final short width4 = 5;
		final short height4 = 35;
		x += width3 - width4*2;
		g.fillRect(x, y, width4, height4);
		if(tries == 4) return;
		
		/* 5th false guess */
		final short width5 = 30;
		final short height5 = width5;
		x += width4/2 - width5/2;
		y += height4;
		g.drawImage(gallows5, x, y, width5, height5, null);
		if(tries == 5) return;
		
		/* 6th false guess */
		final short width6 = 3;
		final short height6 = 40;
		x += width5/2 - width6/2;
		y += height5;
		g.fillRect(x, y, width6, height6);
		if(tries == 6) return;
		
		/* 7th false guess */
		final short widthBodyParts = 15;
		final short heightBodyParts = widthBodyParts;
		x += width6/2;
		g.drawImage(gallowsLeft, x - widthBodyParts, (int)(y + y*0.05), widthBodyParts, heightBodyParts, null);
		g.drawImage(gallowsRight, x, (int)(y + y*0.05), widthBodyParts, heightBodyParts, null);
		if(tries == 7) return;
		
		/* 8th false guess */
		y += height6;
		g.drawImage(gallowsLeft, x - widthBodyParts, y, widthBodyParts, heightBodyParts, null);
		g.drawImage(gallowsRight, x, y, widthBodyParts, heightBodyParts, null);
		if(tries == 8) return;
	}
	
	/** imports all required images */
	private void loadImages() {
		try {
			background = ImageIO.read(new File("Pictures/background.jpg"));
			gallows1 = ImageIO.read(new File("Pictures/gallows1.png"));
			gallows3 = ImageIO.read(new File("Pictures/gallows3.png"));
			gallows5 = ImageIO.read(new File("Pictures/gallows5.png"));
			gallowsLeft = ImageIO.read(new File("Pictures/gallowsLeft.png"));
			gallowsRight = ImageIO.read(new File("Pictures/gallowsRight.png"));
		} catch(IOException e) {
			System.out.println("Image can't be found!");
			System.exit(0);
		}
	}

	/** sets everything to default */
	private void restart() {
		play = false;
		gameOver = false;
		won = false;
		restart = false;
		charTyped = false;
		letters = new ArrayList<>();
		guess = null;
		uncover = new Character[0];
		tries = 0;
		usedLetters = new ArrayList<>(30);
		repaint();
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
		if(charTyped == false && e.getKeyChar() != KeyEvent.VK_ENTER && e.getKeyChar() != KeyEvent.VK_BACK_SPACE && e.getKeyChar() != KeyEvent.VK_ESCAPE) {		
			charTyped = true;
			keyTyped(e);
		} else if(play == false && charTyped && e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {		// removes last letter from array
			letters.remove(letters.size()-1);
			if(letters.size() == 0)
				charTyped = false;
			repaint();
		} else if(play == false && charTyped && (Character.isAlphabetic(e.getKeyChar()) || e.getKeyChar() == KeyEvent.VK_SPACE) && e.getKeyChar() != KeyEvent.VK_ENTER && letters.size() < 39) {		// adds typed letters and space to array
			letters.add(Character.toUpperCase(e.getKeyChar()));
			repaint();
		} else if(play && gameOver == false && !usedLetters.contains(Character.toUpperCase(e.getKeyChar())) && Character.isAlphabetic(e.getKeyChar()) && e.getKeyChar() != KeyEvent.VK_ENTER) {		// guessing has started
			guess = Character.toUpperCase(e.getKeyChar());
			repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(play == false && charTyped && e.getKeyCode() == KeyEvent.VK_ENTER) {		// start to guess
			play = true;
			uncover = new Character[letters.size()];
			for(int i = 0; i < letters.size(); i++) {
				if(letters.get(i).equals(' '))
					uncover[i] = ' ';
			}
			repaint();
		} else if(play && gameOver == false && guess != null && e.getKeyCode() == KeyEvent.VK_ENTER) {		// guessing	
			/** add letter to already used letters array */
			usedLetters.add(guess);
			
			if(!letters.contains(guess)) {		// wrong guess
				tries++;
				
				/** GAME OVER - no more tries left */
				if(tries == 8)
					gameOver = true;
			} else {		// right guess
				for(int i = 0; i < letters.size(); i++)		
					if(letters.get(i).equals(guess))
						uncover[i] = guess;
				
				/** checks if every letter was uncovered */
				for(int i = 0; i < uncover.length; i++)
					if(uncover[i] == null)
						break;
					else if(i == uncover.length-1 && uncover[i] != null) {
						gameOver = true;
						won = true;
					}
			}
					 
			guess = null;
			repaint();
		} else if((play && e.getKeyCode() == KeyEvent.VK_ESCAPE) || (play && gameOver && e.getKeyCode() == KeyEvent.VK_R)) {
			gameOver = true;
			restart = true;
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {		
	}
}
