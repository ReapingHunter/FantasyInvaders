package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

    public boolean leftPressed, rightPressed, shiftPressed, spacePressed;

    @Override
    public void keyTyped(KeyEvent e) {
    }
    
//  WHEN KEY IS PRESSED
    @Override
    public void keyPressed(KeyEvent e) {
        
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if(code == KeyEvent.VK_SHIFT) {
        	shiftPressed = true;
        }
        if(code == KeyEvent.VK_SPACE) {
        	spacePressed = true;
        }
    }

//    WHEN KEY IS NOT PRESSED
    @Override
    public void keyReleased(KeyEvent e) {
        
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if(code == KeyEvent.VK_SHIFT) {
        	shiftPressed = false;
        }
        if(code == KeyEvent.VK_SPACE) {
        	spacePressed = false;
        }
    }
    
}
