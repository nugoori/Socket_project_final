package main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

public class AddUserButtonMouseListener extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {
		JOptionPane.showMessageDialog(null, "test", "test2", JOptionPane.PLAIN_MESSAGE);
	}
	
}
