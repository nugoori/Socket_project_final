package client;

import java.awt.CardLayout;


import java.awt.EventQueue;

import java.io.IOException;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.dto.RequestBodyDto;
import client.dto.SendMessage;
import lombok.Getter;


import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

import javax.swing.SwingConstants;

@Getter
public class Client extends JFrame {
	private String username;
	private Socket socket;
	
	private CardLayout mainCardLayout;
	private JPanel mainCardPanel;
	
	private JPanel chattingRoomListPanel;
	private JScrollPane roomListScrollPanel;
	private DefaultListModel<String> roomListModel;
	private JList roomList;
	
	private JPanel chattingRoomPanel;
	private JTextField messageTextField;
	private JTextArea chattingTextArea;
	
	private static Client instance;
	private DefaultListModel<String> userListModel;
	private JList userList;			
	private JScrollPane userListScrollPane;
	private JPanel usernamePanel;
	private JTextField usernameTextField;
	private JButton exitChattingRoomButton;
	private JPanel chattingRoomTitlePanel;
	private JTextField chattingRoomTitleTextField;
	private JScrollPane chattingTextAreaScrollPanel;
	private JTextField toSendChattingTextField;
	
	public static Client getInstance() {
		if(instance == null) {
			instance = new Client();
		}
		return instance;
	}

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				try {
					Client frame = Client.getInstance();
					frame.setVisible(true);
					
					ClientReceiver clientReceiver = new ClientReceiver();
					clientReceiver.start();
					
					RequestBodyDto<String> requestBodyDto = new RequestBodyDto<String>("connection", frame.username);
					ClientSender.getInstance().send(requestBodyDto);
										
				} catch (Exception e) {
					System.out.println("서버 닫힘");			
				}
			}
		});
	}

	public Client() {
		
		/* 아이디 입력이 정상적으로 될 때 까지 반복을 돌려야 함 */
		username = JOptionPane.showInputDialog(chattingRoomPanel, "아이디를 입력하세요.");			
		
		if(username.contains("<방장>")) {
			JOptionPane.showMessageDialog(chattingRoomListPanel, "사용할 수 없는 이름입니다.", "아이디 생성 실패", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		/* 입력창을 닫을 때 */
		if(Objects.isNull(username)) {
			System.exit(0);
		}
		/* 입력값 없이 확인을 누른 경우 */
		if(username.isBlank()) {
			/* username이 빈값이 아닐 경우는 시스템을 끄기보단 break;문으로 반복을 다시 돌리는게 좋음 */
			System.exit(0);
		}
		/* 아이디를 입력하세요 창 , "접속 실패" */
		
		try {
			socket = new Socket("127.0.0.1", 8000);
			
		} catch (IOException e) {
			System.out.println("서버 닫힘");			
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		
		/*  */
		mainCardLayout = new CardLayout();
		mainCardPanel = new JPanel();
		mainCardPanel.setLayout(mainCardLayout);
		setContentPane(mainCardPanel);
		
		chattingRoomListPanel = new JPanel();
		chattingRoomListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		chattingRoomListPanel.setLayout(null);
		mainCardPanel.add(chattingRoomListPanel, "chattingRoomListPanel");
		
		JButton createRoomButton = new JButton("방만들기");
		createRoomButton.setBounds(10, 10, 100, 30);
		createRoomButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String roomName = JOptionPane.showInputDialog(chattingRoomListPanel, "방제목을 입력하세요.");
				/* 방 만들기에 실패하면 다시 창이 뜨도록 반복을 돌리면 좋을듯 */
				if(Objects.isNull(roomName)) {
					return;
				}
				if(roomName.isBlank()) {
					JOptionPane.showMessageDialog(chattingRoomListPanel, "방제목을 입력하세요.", "방만들기 실패", JOptionPane.ERROR_MESSAGE);
					return;
				}
				for(int i = 0; i < roomListModel.size(); i++) {
					if(roomListModel.get(i).equals(roomName)) {
						JOptionPane.showMessageDialog(chattingRoomListPanel, "이미 존재하는 방제목입니다.", "방만들기 실패", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				chattingRoomTitleTextField.setText(roomName);
				RequestBodyDto<String> requestBodyDto = new RequestBodyDto<String>("createRoom", roomName);
				ClientSender.getInstance().send(requestBodyDto);
				mainCardLayout.show(mainCardPanel, "chattingRoomPanel");

				requestBodyDto = new RequestBodyDto<String>("join", roomName);
				ClientSender.getInstance().send(requestBodyDto);								
			}
		});
		
		chattingRoomListPanel.add(createRoomButton);
		
		roomListScrollPanel = new JScrollPane();
		roomListScrollPanel.setBounds(10, 50, 414, 201);
		chattingRoomListPanel.add(roomListScrollPanel);
		
		roomListModel = new DefaultListModel<String>();
		roomList = new JList(roomListModel);
		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					String roomName = roomListModel.get(roomList.getSelectedIndex());
					mainCardLayout.show(mainCardPanel, "chattingRoomPanel");
					
					chattingRoomTitleTextField.setText(roomName);
					RequestBodyDto<String>requestBodyDto = new RequestBodyDto<String>("join", roomName);
					ClientSender.getInstance().send(requestBodyDto);
				}
			}	
		});
		roomListScrollPanel.setViewportView(roomList);
		
		usernamePanel = new JPanel();
		usernamePanel.setBounds(319, 10, 105, 30);
		chattingRoomListPanel.add(usernamePanel);
		usernamePanel.setLayout(null);
		
		usernameTextField = new JTextField(username); 
		usernameTextField.setEditable(false);
		usernameTextField.setHorizontalAlignment(SwingConstants.CENTER);
		usernameTextField.setBounds(0, 0, 105, 30);
		usernamePanel.add(usernameTextField);
		usernameTextField.setColumns(10);
						
		chattingRoomPanel = new JPanel();
		chattingRoomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		chattingRoomPanel.setLayout(null);
		mainCardPanel.add(chattingRoomPanel, "chattingRoomPanel");
		
		chattingTextAreaScrollPanel = new JScrollPane();
		chattingTextAreaScrollPanel.setBounds(12, 46, 270, 167);
		chattingRoomPanel.add(chattingTextAreaScrollPanel);
		
		chattingTextArea = new JTextArea();
		chattingTextAreaScrollPanel.setViewportView(chattingTextArea);
		
		// 전체: sendMessage // 아닐경우: toSendMessage(귓속말)
		messageTextField = new JTextField();
		messageTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						RequestBodyDto<SendMessage> requestBodyDto = null;
						SendMessage sendMessage = null;
						
						/* boolean타입의 변수명 앞에는 is/has를 붙이면 알아보기 좋음 */
						if(toSendChattingTextField.getText().equals("전체")) {
						
						sendMessage = SendMessage.builder()
								.fromUsername(username)
								.messageBody(messageTextField.getText())
								.build();
						/* 중복되는 자료형을 반복적으로 사용하지 말고 바깥에 null로 선언해두고 안에서 사용하면 됨 */
						requestBodyDto = 
								new RequestBodyDto<>("sendMessage", sendMessage); 
						
						ClientSender.getInstance().send(requestBodyDto);
						messageTextField.setText(""); /* 중복되는 부분은 if문이 끝나면 실행되도록 */
					} else {			
							sendMessage = SendMessage.builder()
									.fromUsername(username)
									.toUsername(toSendChattingTextField.getText())
									.messageBody(messageTextField.getText())
									.build();
							
							requestBodyDto =
									new RequestBodyDto<SendMessage>("SendMessage", sendMessage);
							System.out.println(requestBodyDto.toString()); // 여기선 toU
							ClientSender.getInstance().send(requestBodyDto);
							messageTextField.setText("");
							toSendChattingTextField.setText("전체");						
					}
				}
			}
		});
		toSendChattingTextField = new JTextField("전체");
		toSendChattingTextField.setHorizontalAlignment(SwingConstants.CENTER);
		toSendChattingTextField.setEditable(false);
		toSendChattingTextField.setBounds(12, 223, 51, 28);
		chattingRoomPanel.add(toSendChattingTextField);
		toSendChattingTextField.setColumns(10);
		
		messageTextField.setBounds(75, 223, 347, 28);
		chattingRoomPanel.add(messageTextField);
		messageTextField.setColumns(10);
		
		userListScrollPane = new JScrollPane();
		userListScrollPane.setBounds(294, 46, 128, 167);
		chattingRoomPanel.add(userListScrollPane);
		
		userListModel = new DefaultListModel<>();
		userList = new JList(userListModel);		
		userListScrollPane.setViewportView(userList);
		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					if(userListModel.get(userList.getSelectedIndex()).equals(username) ||
							userListModel.get(userList.getSelectedIndex()).equals(username + " <방장>")) 
					{
						// Data아님 JTextComponent에있는 메소드임
						toSendChattingTextField.setText("전체");						
					} else {
						
						String userName = userListModel.get(userList.getSelectedIndex()).replaceAll("<방장>", "");
						/* 아래 부분을 replaceAll로 대체할 수 있음 */
//						if(userName.contains("<방장>")) {
//							/* .subString .indexOf 완전 까먹 */
//							userName = userName.substring(0, userName.indexOf("<"));
//						}
						mainCardLayout.show(mainCardPanel, "chattingRoomPanel");
						toSendChattingTextField.setText(userName);
					}
				}
			}
		});

		chattingRoomTitlePanel = new JPanel();
		chattingRoomTitlePanel.setBounds(12, 10, 270, 26);
		chattingRoomPanel.add(chattingRoomTitlePanel);
		chattingRoomTitlePanel.setLayout(null);						
		
		chattingRoomTitleTextField = new JTextField(); 
		chattingRoomTitleTextField.setHorizontalAlignment(SwingConstants.CENTER);
		chattingRoomTitleTextField.setEditable(false);
		chattingRoomTitleTextField.setBounds(0, 0, 270, 26);
		chattingRoomTitlePanel.add(chattingRoomTitleTextField);
		chattingRoomTitleTextField.setColumns(10);
		
		exitChattingRoomButton = new JButton("나가기");
		exitChattingRoomButton.setBounds(294, 10, 128, 28);
		exitChattingRoomButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				int clicked = JOptionPane.showInternalConfirmDialog(mainCardPanel, "나가시겠습니까?");				
				if(clicked == 0) {			
					String roomName = chattingRoomTitleTextField.getText();
					
					if(userListModel.get(0).equals(username + " <방장>")) {
						RequestBodyDto<String> requestBodyDtoUsername = 
								new RequestBodyDto<>("removeRoom", roomName); 
						ClientSender.getInstance().send(requestBodyDtoUsername);
					}
					
					RequestBodyDto<String> requestBodyDto = 
							new RequestBodyDto<>("exit", roomName); 
					
					mainCardLayout.show(mainCardPanel, "chattingRoomListPanel");
					ClientSender.getInstance().send(requestBodyDto);
					chattingRoomTitleTextField.setText(roomName);
	
				} else if (clicked == 1) {
					return;
				}
			}			
		});
		chattingRoomPanel.add(exitChattingRoomButton);
		
	}
}