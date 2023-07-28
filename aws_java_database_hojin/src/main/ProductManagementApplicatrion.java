package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import frame.ProductCategoryRegisterFrame;
import frame.ProductColorRegisterFrame;
import frame.ProductRegisterFrame;
import frame.ProductSearchFrame;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProductManagementApplicatrion extends JFrame {

	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProductManagementApplicatrion frame = new ProductManagementApplicatrion();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ProductManagementApplicatrion() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton productSearchFrameOpenButton = new JButton("상품조회");
		productSearchFrameOpenButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ProductSearchFrame productSearchFrame = 
						new ProductSearchFrame();
				productSearchFrame.setVisible(true);
			}
		});
		productSearchFrameOpenButton.setBounds(12, 38, 397, 43);
		contentPane.add(productSearchFrameOpenButton);
		
		JButton productListFrameOpenButton = new JButton("상품등록");
		productListFrameOpenButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ProductRegisterFrame productRegisterFrame =
						new ProductRegisterFrame();
				productRegisterFrame.setVisible(true);
			}
		});
		
		productListFrameOpenButton.setBounds(12, 91, 397, 43);
		contentPane.add(productListFrameOpenButton);
		
		JButton productColorRegisterFrameOpenButton = new JButton("상품색상등록");
		productColorRegisterFrameOpenButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ProductColorRegisterFrame productColorRegisterFrame =
						new ProductColorRegisterFrame();
				productColorRegisterFrame.setVisible(true);
			}
		});
		productColorRegisterFrameOpenButton.setBounds(12, 149, 397, 43);
		contentPane.add(productColorRegisterFrameOpenButton);
		
		JButton productCategoryRegisterOpenButton = new JButton("카테고리등록");
		productCategoryRegisterOpenButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ProductCategoryRegisterFrame productCategoryRegisterFrame =
						new ProductCategoryRegisterFrame();
				productCategoryRegisterFrame.setVisible(true);
			}
		});
		productCategoryRegisterOpenButton.setBounds(12, 202, 397, 43);
		contentPane.add(productCategoryRegisterOpenButton);
	}
}
