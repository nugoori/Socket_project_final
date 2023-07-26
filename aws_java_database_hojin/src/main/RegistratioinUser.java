package main;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import config.DBConnectionMgr;

public class RegistratioinUser extends JFrame {

	private JPanel contentPane;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegistratioinUser frame = new RegistratioinUser();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public RegistratioinUser() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		usernameTextField = new JTextField();
		usernameTextField.setBounds(12, 34, 192, 26);
		contentPane.add(usernameTextField);
		usernameTextField.setColumns(10);
		
		passwordTextField = new JTextField();
		passwordTextField.setColumns(10);
		passwordTextField.setBounds(230, 34, 192, 26);
		contentPane.add(passwordTextField);
		
		JLabel lblNewLabel = new JLabel("아이디");
		lblNewLabel.setBounds(12, 10, 57, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("비밀번호");
		lblNewLabel_1.setBounds(230, 10, 57, 15);
		contentPane.add(lblNewLabel_1);
		
		JButton addUserButton = new JButton("추가");
		addUserButton.setBounds(12, 69, 410, 26);
		
//		addUserButton.addMouseListener(new AddUserButtonMouseListener());
		// mouseAdapter가 상속받은 mouseListener는 메소드가 여러개라 람다 불가능
		addUserButton.addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!insertUser(usernameTextField.getText(), passwordTextField.getText())) {
					JOptionPane.showMessageDialog(contentPane, "사용자 추가 실패", "insert오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				updateUserListTable(table);
			}
		});
		contentPane.add(addUserButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 105, 410, 146);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setModel(getUserTableModel());
		scrollPane.setViewportView(table);
	}
	
	private boolean insertUser(String username, String password) {
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		boolean result = false;
		
		try {
			con = pool.getConnection();
			String sql = "insert into user_tb values(0, ?, ?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			result = pstmt.executeUpdate() != 0;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		
		return result;
	}
	
	private void updateUserListTable(JTable jTable) {
		jTable.setModel(getUserTableModel());
	}
	
	public DefaultTableModel getUserTableModel() {
		String[] header = new String[] { "user_id", "username", "password" };
		
		List<List<Object>> userListAll = getUserListAll();
		
		Object[][] userModelArray = new Object[userListAll.size()][userListAll.get(0).size()];
		
		for(int i = 0; i < userListAll.size(); i++) {
			for(int j = 0; j < userListAll.get(i).size(); j++) {
				userModelArray[i][j] = userListAll.get(i).get(j);
			}
		}
		
		return new DefaultTableModel(userModelArray, header);
	}
	
	public List<List<Object>> getUserListAll() {
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; // query문을 보낼때 사용
		
		List<List<Object>> mstList = new ArrayList<>();
		
		try {
			con = pool.getConnection();
			String sql = "select * from user_tb";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				List<Object> dtlList = new ArrayList<>();
				dtlList.add(rs.getInt(1));
				dtlList.add(rs.getString(2));
				dtlList.add(rs.getString(3));
				mstList.add(dtlList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return mstList;
	}
}
