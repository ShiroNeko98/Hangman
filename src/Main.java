/**************************************************
 * Hangman
 * 
 * @author Quoc Duy Do
 * @version 1.1 30.July 2019
**************************************************/

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

/** Main-Class */
public class Main
{
	public static void main(String[] args)
    {
		/** creates screen */
		JFrame obj = new JFrame("Hangman");
		Gameplay game = new Gameplay();
		Dimension monitorSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int screenWidth = 624;
		final int screenHeight = 381;
		obj.setBounds((int)(monitorSize.getWidth()-screenWidth)/2, (int)(monitorSize.getHeight()-screenHeight)/2, screenWidth, screenHeight);
		obj.setResizable(false);
		obj.setVisible(true);
		obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		obj.add(game);
	}
}
