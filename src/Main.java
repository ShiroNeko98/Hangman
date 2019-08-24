/**************************************************
 * PROJECT: Hangman
 * USER: Quoc Duy Do
 * VERSION: 1.2
 * LAST UPDATE: 24-Aug-19

 * DESCRIPTION: Hangman-Game
 * COMING SOON: Scores
 **************************************************/

import javax.swing.*;
import java.awt.*;

public class Main {
	public static void main(String[] args) {
		/* creates screen */
		JFrame obj = new JFrame("Hangman");
		Gameplay game = new Gameplay();
		Dimension monitorSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int screenWidth = 624;
		final int screenHeight = 381;
		obj.setBounds((int) (monitorSize.getWidth() - screenWidth) / 2, (int) (monitorSize.getHeight() - screenHeight) / 2, screenWidth, screenHeight);
		obj.setResizable(false);
		obj.setVisible(true);
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obj.add(game);
	}
}
