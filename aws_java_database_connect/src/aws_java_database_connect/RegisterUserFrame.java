package aws_java_database_connect;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
		
		// 가입 버튼을 누르면 DB에 데이터 추가
		JButton addUserButton = new JButton("가입");
		addUserButton.setBounds(12, 72, 410, 23);
		
		// 원래는 MouseListener Interface를 상속받는 class를 만들어 추상 메서드를 상속받아 모든 추상메서드를 재정의하여 사용해야하지만
		// MouseListener인터페이스들을 상속받은 MouseAdapter 추상 클래스를 생성하여 재정의된 메서드 중 필요한 메서드만 가져와서 사용한다
		addUserButton.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// textField에서 받은 데이터를 DB에 insert
				if(!insertUser(usernameTextField.getText(), passwordTextField.getText())) {
					JOptionPane.showMessageDialog(contentPane, "가입 실패", "insert 오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// insert 성공하면 userListTable 업데이트
				updateUserListTable(userListTable);
			}
		});
		
		contentPane.add(addUserButton);
		
		JScrollPane UserListScrollPane = new JScrollPane();
		UserListScrollPane.setBounds(10, 105, 412, 146);
		contentPane.add(UserListScrollPane);
		
		userListTable = new JTable();
		userListTable.setModel(getUserTableModel());
		UserListScrollPane.setViewportView(userListTable);
	}
	
	// client의 userList를 업데이트 하는 메소드
	private void updateUserListTable(JTable userListTable) {
		// userListModel 메소드로 userList를 가져와서 userListTable에 저장
		userListTable.setModel(getUserTableModel());
	}
	
///////////////////////        client에서 DB에 데이터를 추가하기 위한 메서드         ////////////////////////////////
	// DB에 추가하기위한 username, password를 매개변수로 받는 메소드
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
			// executeUpdate가 boolean타입을 반환 
			// 1 the row count for SQL Data Manipulation Language or 2 for SQL statements that return nothing
			result = pstmt.executeUpdate() != 0;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}
		
		return result;
	}
	
	/////////////////////////        table에서 data를 받아오는 메소드들         ////////////////////////////////
	public DefaultTableModel getUserTableModel() {
		// DB의 컬럼명을 받을 배열
		String[] header = new String[] {"user_id", "username", "password"};
		// 컬럼의 Data들을 넣은 userList배열을 받을 배열
		List<List<Object>> allUserList = getUserListAll();
		// 첫번째 배열에는 row의 수 만큼  /  두번째 배열에는 컬럼의 수만큼 data가 들어감
		Object[][] userTableArray = new Object[allUserList.size()][allUserList.get(0).size()];
		
		for(int i = 0; i < allUserList.size(); i++) {
			for(int j = 0; j < allUserList.size(); j++) {
				userTableArray[i][j] = allUserList.get(i).get(j);
			}
		}
		// getUserTableModel을 호출하면 DefaultTableModel타입의 객체에 header와 userTableArray을 반환해줘야함 
		// 호출 할 때마다 새로 받아야하기 때문에? DefaultTableModel이 colu mnName을 나중에 받음
		return new DefaultTableModel(userTableArray, header);
	}
	
	public List<List<Object>> getUserListAll() {
		// jdbc
		DBConnectionMgr pool = DBConnectionMgr.getInstance();
		// 특정 DB와 연결, vector?
		Connection con = null;
		// spl문으로 바꿀 준비
		PreparedStatement pstmt = null;
		// 프로토콜 
		ResultSet rs = null;
		
		List<List<Object>> userList = new ArrayList();
		
		try {
			con = pool.getConnection();
			String sql = "select * from user_tb";
			//          Creates a PreparedStatement object for sending parameterized SQL statements to the database.
			pstmt = con.prepareStatement(sql);
//			Executes the SQL query in this PreparedStatement object and returns the ResultSet object generated by the query.
			rs = pstmt.executeQuery();
			// ResultSet 객체로 만들어진 SQL query문이 다음이 없을 때 까지 반복을 돌려 안의 데이터를 가져와서 리스트에 넣음, 
			while (rs.next()) {
				List<Object> sqlList = new ArrayList<>();
				// SQL query문의 컬럼index를 받아 int, String으로 받아옴
				sqlList.add(rs.getInt(1));
				sqlList.add(rs.getString(2));
				sqlList.add(rs.getString(3));
				// DB에 저장된 row의 수 만금 userList가 생김
				userList.add(sqlList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return userList;
	}
}
