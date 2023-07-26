package aws_java_database_connect;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.mysql.cj.protocol.Resultset;

import config.DBConnectionMgr;

public class RegisterUserFrame extends JFrame {

	private JPanel contentPane;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JTable userListTable;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegisterUserFrame frame = new RegisterUserFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public RegisterUserFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		usernameTextField = new JTextField();
		usernameTextField.setBounds(10, 41, 200, 21);
		contentPane.add(usernameTextField);
		usernameTextField.setColumns(10);
		
		passwordTextField = new JTextField();
		passwordTextField.setBounds(222, 41, 200, 21);
		contentPane.add(passwordTextField);
		passwordTextField.setColumns(10);
		
		JLabel usernameLabel = new JLabel("아이디");
		usernameLabel.setBounds(12, 10, 200, 21);
		contentPane.add(usernameLabel);
		
		JLabel passwordLabel_1 = new JLabel("비밀번호");
		passwordLabel_1.setBounds(222, 10, 200, 21);
		contentPane.add(passwordLabel_1);
		
		JButton addUserButton = new JButton("가입");
		addUserButton.setBounds(12, 72, 410, 23);
		contentPane.add(addUserButton);
		
		JScrollPane UserListScrollPane = new JScrollPane();
		UserListScrollPane.setBounds(10, 105, 412, 146);
		contentPane.add(UserListScrollPane);
		
		userListTable = new JTable();
		userListTable.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
				{null, null, null},
				{null, null, null},
			},
			new String[] {
				"New column", "New column", "New column"
			}
		));
		UserListScrollPane.setViewportView(userListTable);
	}
	
	public DefaultTableModel getUserTableModel() {
		
	}
	
	public List<List<Object>> getUserListAll() {
		// jdbc
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		// 특정 DB와 연결, vector?
		Connection con = null;
		// spl문으로 바꿀 준비
		PreparedStatement pstmt = null;
		Resultset rs = null;
		
		List<List<Object>> userList = new ArrayList();
		
		try {
			con = pool.getConnection();
			String sql = "select * from user_tb";
			
			pstmt = con.prepareStatement(sql);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
