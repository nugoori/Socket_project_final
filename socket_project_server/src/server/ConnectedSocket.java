package server;

import java.awt.event.MouseAdapter;


import java.awt.event.MouseEvent;
import java.io.BufferedReader;



import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import lombok.RequiredArgsConstructor;
import server.dto.RequestBodyDto;
import server.dto.SendMessage;
import server.entity.Room;

@RequiredArgsConstructor
public class ConnectedSocket extends Thread{
	
	private String username;
	private Gson gson;
	
	private final Socket socket;
	
	 @Override
	public void run() {
		 gson = new Gson();
		 while(true) {
			 try {
				BufferedReader bufferedReader =
						new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String requestBody = bufferedReader.readLine();
				
				requestController(requestBody);
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
	 }
		 
	private void requestController(String requsetBody) {

		String resource = gson.fromJson(requsetBody, RequestBodyDto.class).getResource();
//		TypeToken<RequestBodyDto<SendMessage>> token = new TypeToken<RequestBodyDto<SendMessage>>() {};
//		RequestBodyDto<SendMessage> requestBodyDto = gson.fromJson(requsetBody, token.getType());
		
		
		switch(resource) {		
			case "connection" :
				connection(requsetBody);
				break;		
			case "createRoom" :
				createRoom(requsetBody);					
				break;		
			case "sendMessage" :
				sendMessage(requsetBody);				
				break;								
			case "join" :
				join(requsetBody);			
				break;			
			case "exit" :
				exit(requsetBody);
				break;
		}
		
		
		
		
	}

				
	private void connection(String requestBody) {
		username = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		
		List<String> roomNameList = new ArrayList<>();
		
		Server.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});
		
		RequestBodyDto<List<String>> updateRoomListRequestBodyDto = 
				new RequestBodyDto<List<String>>("updateRoomList", roomNameList);
		
		ServerSender.getInstance().send(socket, updateRoomListRequestBodyDto);
	}
	
	private void createRoom(String requestBody) {
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		username = username + " <방장>";
		Room newRoom = Room.builder()
			.roomName(roomName)
			.owner(username)
			.userList(new ArrayList<ConnectedSocket>())
			.build();
		
		Server.roomList.add(newRoom);
		
		List<String> roomNameList = new ArrayList<>();
		
		Server.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});
		
		RequestBodyDto<String> removeTextArea =
				new RequestBodyDto<String>("removeTextArea", "");
		ServerSender.getInstance().send(this.socket, removeTextArea);
		
		
		
		RequestBodyDto<List<String>> updateRoomListRequestBodyDto = 
				new RequestBodyDto<List<String>>("updateRoomList", roomNameList);
		
		Server.connectedSocketList.forEach(con -> {
			ServerSender.getInstance().send(con.socket, updateRoomListRequestBodyDto);	
		});		
	}
	
	private void join(String requestBody) {
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();	
		Server.roomList.forEach(room -> {
			if(room.getRoomName().equals(roomName)) {
				room.getUserList().add(this);
				
				List<String> usernameList = new ArrayList<>();
				
				RequestBodyDto<String> removeTextArea =
						new RequestBodyDto<String>("removeTextArea", "");
				ServerSender.getInstance().send(this.socket, removeTextArea);
				
				room.getUserList().forEach(con -> {
					usernameList.add(con.username);
				});
				
				room.getUserList().forEach(connectedSocket -> {
					RequestBodyDto<List<String>> updateUserListDto = new RequestBodyDto<List<String>>("updateUserList", usernameList);
					RequestBodyDto<String> joinMessageDto = new RequestBodyDto<String>("showMessage", username + "님이 들어왔습니다.");
					
					ServerSender.getInstance().send(connectedSocket.socket, joinMessageDto);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ServerSender.getInstance().send(connectedSocket.socket, updateUserListDto);
				});
				
			}
		});	
	}
	
	
	
	private void sendMessage(String requestBody) {
		TypeToken<RequestBodyDto<SendMessage>> typeToken = new TypeToken<RequestBodyDto<SendMessage>>() {};
		
		RequestBodyDto<SendMessage> requestBodyDto = gson.fromJson(requestBody, typeToken.getType());
		SendMessage sendMessage = requestBodyDto.getBody();
		
//		String inRoomUsername = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		
		Server.roomList.forEach(room -> {
			if(room.getUserList().contains(this)) {				
				room.getUserList().forEach(connectedSocket -> {
					RequestBodyDto<String> dto = 
							new RequestBodyDto<String>("showMessage", sendMessage.getFromUsername() + ": " + sendMessage.getMessageBody());
					ServerSender.getInstance().send(connectedSocket.socket, dto);
				});
			}
		});
	}
	
	
	private void exit(String requsetBody) {
		String roomName = (String) gson.fromJson(requsetBody, RequestBodyDto.class).getBody();	
//		SimpleGUIServer.roomList.
		Server.roomList.forEach(room -> {
			if(room.getRoomName().equals(roomName)) {
				room.getUserList().remove(this);
				
				List<String> usernameList = new ArrayList<>();
				
				room.getUserList().forEach(con -> {
					usernameList.add(con.username);
				});
	
		
				room.getUserList().forEach(connectedSocket -> {
					RequestBodyDto<List<String>> updateUserListDto = new RequestBodyDto<List<String>>("updateUserList", usernameList);		
					RequestBodyDto<String> exitMessageDto = new RequestBodyDto<String>("showMessage", username + "님이 나갔습니다.");
					
					ServerSender.getInstance().send(connectedSocket.socket, updateUserListDto);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ServerSender.getInstance().send(connectedSocket.socket, exitMessageDto);
				});
				
				
				
				//
				
				
//				if(room.getOwner().contains(username)) {
//					List<String> roomNameList = new ArrayList<>(); 
//					SimpleGUIServer.roomList.forEach(roomNameLegacy -> {
//						if(roomNameLegacy.getRoomName().equals(roomName)) {							
//						}else {roomNameList.add(roomNameLegacy.getRoomName());}
//					});
//					RequestBodyDto<List<String>> updateRoomListRequestBodyDto = 
//							new RequestBodyDto<List<String>>("updateRoomList", roomNameList);					
//					SimpleGUIServer.connectedSocketList.forEach(con -> {
//						ServerSender.getInstance().send(con.socket, updateRoomListRequestBodyDto);
//					});
//					
//				}
				
				
				if(usernameList.isEmpty()) {
					System.out.println("방 삭제 작동");
					
					List<String> roomNameList = new ArrayList<>();
					
					Server.roomList.forEach(roomList -> {
						roomNameList.add(roomList.getRoomName());
						roomNameList.remove(roomName);
					});		
					
					RequestBodyDto<String> removeEmptyRoom =
							new RequestBodyDto<String> ("removeRoom", roomName);
							
					Server.connectedSocketList.forEach( con -> {
						ServerSender.getInstance().send(con.socket, removeEmptyRoom);
					});
										
					RequestBodyDto<List<String>> updateRoomListRequestBodyDto = 
							new RequestBodyDto<List<String>>("updateRoomList", roomNameList);					
					Server.connectedSocketList.forEach(con -> {
						ServerSender.getInstance().send(con.socket, updateRoomListRequestBodyDto); 						
					});
				}
				
			}
		});	
		//
	
	}
	
	/*    메세지 창 초기화
	RequestBodyDto<String> removeTextArea =
			new RequestBodyDto<String>("removeTextArea", "");
	try {
		Thread.sleep(10);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	ServerSender.getInstance().send(connectedSocket.socket, removeTextArea);
	*/
	
	
}

	
	
	
//				System.out.println(requestBody);
//				simpleGUIServer.connectedSocketList.forEach( connectedSocket -> {
//					try {
//						PrintWriter printWriter = new PrintWriter(connectedSocket.socket.getOutputStream(), true);
//						printWriter.println(requestBody);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				});
				
//	case2		for(ConnectedSocket connectedSocket = simpleGUIServer.connectedSocketList) {}
				
//  case3		for(int i = 0; i < simpleGUIServer.connectedSocketList.size(); i++) {}		
				

		 
		 
	
	

	 
	

