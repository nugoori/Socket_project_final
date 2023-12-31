package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import client.dto.RequestBodyDto;

public class ClientReceiver extends Thread{
		
	Gson gson = new Gson();
	
	@Override
	public void run() {
		Client simpleGUIClient = Client.getInstance();
		while(true) {
			try {
				BufferedReader bufferedReader = 
						new BufferedReader(new InputStreamReader(simpleGUIClient.getSocket().getInputStream()));
				String requestBody = bufferedReader.readLine();
		
				requestController(requestBody);				
			} catch (IOException e) {
				System.out.println("서버 닫힘");
				JOptionPane.showMessageDialog(Client.getInstance().getChattingRoomPanel(), "서버 접속 오류");
				System.exit(0);
				break;
			}			
		}
	}
	

	
	private void requestController(String requestBody) {
		
		String resource = gson.fromJson(requestBody, RequestBodyDto.class).getResource();
		switch(resource) {
		
			case "updateRoomList" :
				updateRoomList(requestBody);
				break;
		
			case "updateUserList" :
				updateUserList(requestBody);	
				break; 				
				
			case "showMessage" :
				showMessage(requestBody);
				break;			
				
			case "removeTextArea" :
				removeTextArea(requestBody);
				break;
				
			case "exitChattingRoom" : // 서버에서 명령을 받아 클라이언트를 채팅방에서 내보내는 스위치
				exitChattingRoom(requestBody);
				break;
		}
	}
	
	private void updateRoomList(String requestBody) {
		List<String> roomList = (List<String>) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		Client.getInstance().getRoomListModel().clear();
		Client.getInstance().getRoomListModel().addAll(roomList);
	}
	
	private void updateUserList(String requestBody) {
		List<String> usernameList = (List<String>) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		Client.getInstance().getUserListModel().clear();
		usernameList.set(0, usernameList.get(0) + " <방장>");
		Client.getInstance().getUserListModel().addAll(usernameList);	
	}
	
	private void showMessage(String requestBody) {
		String messageContent = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		Client.getInstance().getChattingTextArea().append(messageContent + "\n");
	}
	
	private void removeTextArea(String requestBody) {
		Client.getInstance().getChattingTextArea().setText("");
	}
	
	private void exitChattingRoom(String requestBody) {
		String receiveExitMessage = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		JOptionPane.showMessageDialog(Client.getInstance().getChattingRoomListPanel(), receiveExitMessage);
		Client.getInstance().getMainCardLayout().show(Client.getInstance().getMainCardPanel(), "chattingRoomListPanel");
	}
		
		
			
}
		
	

