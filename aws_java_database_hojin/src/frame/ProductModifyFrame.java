package frame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import entity.Product;
import entity.ProductCategory;
import entity.ProductColor;
import service.ProductCategoryService;
import service.ProductColorService;
import service.ProductService;
import utils.CustomSwingComboBoxUtil;
import utils.CustomSwingTextUtil;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProductModifyFrame extends JFrame {

	private JPanel contentPane;
	private JTextField productNameTextField;
	private JTextField productPriceTextField;
	private JTextField productIdTextField;
	
	public ProductModifyFrame(int productId) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 347);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel titleLabel = new JLabel("상품 수정");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setBounds(12, 26, 410, 26);
		contentPane.add(titleLabel);
		
		JLabel productIdLabel = new JLabel("상품번호");
		productIdLabel.setBounds(12, 97, 57, 15);
		contentPane.add(productIdLabel);
		
		productIdTextField = new JTextField();
		productIdTextField.setBounds(65, 94, 357, 18);
		productIdTextField.setColumns(10);
		productIdTextField.setEnabled(false);
		contentPane.add(productIdTextField);
		
		JLabel productNameLabel = new JLabel("상품명");
		productNameLabel.setBounds(12, 122, 57, 18);
		contentPane.add(productNameLabel);
		
		productNameTextField = new JTextField();
		productNameTextField.setBounds(65, 122, 357, 18);
		contentPane.add(productNameTextField);
		productNameTextField.setColumns(10);
		
		productPriceTextField = new JTextField();
		productPriceTextField.setColumns(10);
		productPriceTextField.setBounds(65, 150, 357, 18);
		contentPane.add(productPriceTextField);
		
		JLabel productPriceLabel = new JLabel("가격");
		productPriceLabel.setBounds(12, 150, 57, 18);
		contentPane.add(productPriceLabel);
		
		JLabel productColorLabel = new JLabel("색상");
		productColorLabel.setBounds(12, 178, 57, 18);
		contentPane.add(productColorLabel);
		
		JComboBox productColorComboBox = new JComboBox();
		CustomSwingComboBoxUtil.setComboBoxModel(
				productColorComboBox, 
				ProductColorService.getInstance().getProductColorLNameList()
				);
		productColorComboBox.setBounds(65, 178, 357, 18);
		contentPane.add(productColorComboBox);
		
		JLabel productCategoryLabel = new JLabel("카테고리");
		productCategoryLabel.setBounds(12, 206, 57, 18);
		contentPane.add(productCategoryLabel);
		
		JComboBox productCategoryComboBox = new JComboBox();
		CustomSwingComboBoxUtil.setComboBoxModel(
				productCategoryComboBox,
				ProductCategoryService.getInstance().getProductCategoryNameList());
		productCategoryComboBox.setBounds(65, 206, 357, 18);
		contentPane.add(productCategoryComboBox);

		JButton modifySubmitButton = new JButton("상품 수정");
		modifySubmitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 입력값이 있는지 확인
				String productName = productNameTextField.getText();
				if(CustomSwingTextUtil.isTextEmpty(contentPane, productName)) {return;}
				
				String productPrice = productPriceTextField.getText();
				if(CustomSwingTextUtil.isTextEmpty(contentPane, productPrice)) {return;}
				
				String productColorName = (String) productColorComboBox.getSelectedItem();
				if(CustomSwingTextUtil.isTextEmpty(contentPane, productColorName)) {return;}
				
				String productCategoryName = (String) productCategoryComboBox.getSelectedItem();
				if(CustomSwingTextUtil.isTextEmpty(contentPane, productCategoryName)) {return;}
				
				// 여기는 정상작동
				if(ProductService.getInstance().isProductNameDuplicated(productName)) {
					JOptionPane.showMessageDialog(contentPane, "이미 등록된 제품입니다.", "중복오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				Product product = Product.builder()
						.productId(productId)
						.productName(productName)
						.productPrice(Integer.parseInt(productPrice))
						.productColor(ProductColor.builder().productColorName(productColorName).build())
						.productCategory(ProductCategory.builder().productCategoryName(productCategoryName).build())
						.build();

				// 오류남
				if(!ProductService.getInstance().modifyProduct(product)) {
					JOptionPane.showMessageDialog(contentPane, "상품수정 중 오류가 발생하였습니다.", "수정오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				JOptionPane.showMessageDialog(contentPane, "상품을 수정하였습니다.", "수정성공", JOptionPane.PLAIN_MESSAGE);
				ProductSearchFrame.getInstance().setSearchProductTableModel();
				dispose();
			}
		});
		modifySubmitButton.setBounds(12, 250, 410, 54);
		contentPane.add(modifySubmitButton);
		
		Product product = ProductService.getInstance().getProductByProductId(productId);
		
		if(product != null) {
			productIdTextField.setText(Integer.toString(product.getProductId()));
			productNameTextField.setText(product.getProductName());
			productPriceTextField.setText(Integer.toString(product.getProductPrice()));
			productColorComboBox.setSelectedItem(product.getProductColor().getProductColorName());
			productCategoryComboBox.setSelectedItem(product.getProductCategory().getProductCategoryName());			
		}
		
	}	
}
