package frame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import entity.Product;
import service.ProductService;
import utils.CustomSwingTableUtil;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class ProductSearchFrame extends JFrame {

	private JPanel contentPane;
	private JTextField searchTextField;
	private JTable productTable;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProductSearchFrame frame = new ProductSearchFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ProductSearchFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1000, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel titleLabel = new JLabel("상품 조회");
		titleLabel.setForeground(new Color(0, 0, 0));
		titleLabel.setBackground(new Color(255, 255, 255));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(22, 10, 150, 20);
		contentPane.add(titleLabel);
		
		JButton productModifyButton = new JButton("수정");
		productModifyButton.setBounds(760, 7, 100, 23);
		contentPane.add(productModifyButton);
		
		JButton productRemoveButton = new JButton("삭제");
		productRemoveButton.setBounds(872, 7, 100, 22);
		contentPane.add(productRemoveButton);
		
		JLabel searchLabel = new JLabel("검색");
		searchLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		searchLabel.setBounds(419, 40, 69, 20);
		contentPane.add(searchLabel);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"전체", "상품명", "색상", "카테고리"}));
		comboBox.setBounds(500, 40, 75, 20);
		contentPane.add(comboBox);
		
		searchTextField = new JTextField();
		searchTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String searchOption = (String) comboBox.getSelectedItem();
					String searchValue = searchTextField.getText();
					
					List<Product> searchProductList = ProductService.getInstance().searchProduct(searchOption, searchValue);
					String[][] searchProductModelArray = CustomSwingTableUtil.searchProductListToArray(searchProductList);
					
					productTable.setModel(new DefaultTableModel(
						searchProductModelArray,
							
						new String[] {
							"product_id", "product_name", "product_price", "product_color_id", "product_color_name", "product_category_id", "product_category_name"
						}
					));
					
				}
			}
		});
		searchTextField.setBounds(572, 40, 400, 21);
		contentPane.add(searchTextField);
		searchTextField.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(22, 79, 950, 372);
		contentPane.add(scrollPane);
		
		productTable = new JTable();
		
		scrollPane.setViewportView(productTable);

	}
}
