package main;

import javax.swing.*;


public class FantasyInvaders extends JFrame{
	private MainMenu menu = new MainMenu(this);
	public FantasyInvaders() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(true);
		setSize(1920, 1080);
		setTitle("Fantasy Invaders");
		add(menu);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new FantasyInvaders();
	}
}
