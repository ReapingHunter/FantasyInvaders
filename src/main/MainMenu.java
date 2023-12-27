package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import tile.TileManager;

public class MainMenu extends JPanel implements ActionListener{
	private FantasyInvaders main;
	private TileManager tileM;
	private GamePanel gp;
	public MainMenu(FantasyInvaders main) {
		this.main = main;
		gp = new GamePanel(main);
		tileM = new TileManager(gp);
		setLayout(null);
		JButton start = new JButton("Play");
		JButton quit = new JButton("Quit");
		customizeButton(start);
		start.setBounds(tileM.getScreenWidth() / 2 - tileM.getTileSize() * 2, tileM.getScreenHeight() / 2, 100, 40);
		start.addActionListener(this);
		add(start);
		customizeButton(quit);
		quit.setBounds(tileM.getScreenWidth() / 2, tileM.getScreenHeight() / 2, 100, 40);
		quit.addActionListener(this);
		add(quit);
	}
	
	public void customizeButton(JButton button) {
        button.setBackground(new Color(150, 75, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Courier", Font.BOLD, 16));
    }
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		Font font = new Font("Arial", Font.BOLD, tileM.getTileSize());
		FontMetrics metrics = g2.getFontMetrics(font);
        int stringWidth = metrics.stringWidth("Fantasy Invaders");
        int stringHeight = metrics.getHeight();

        // Calculate the position to center the text horizontally and vertically
        int x = (getWidth() - stringWidth) / 2;
        int y = (getHeight() - stringHeight) / 2 + metrics.getAscent();
        
		g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
		tileM.draw(g2);
		g.setFont(new Font("Helvetica", Font.BOLD, tileM.getTileSize()));
        g.drawString("Fantasy Invaders", x, y / 2);
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Play")) {
            // Close the main menu and start the game
        	main.add(gp);
        	setVisible(false);
            gp.startGameThread();
        } else if(e.getActionCommand().equals("Quit")) {
        	System.exit(0);
        }
    }
}
