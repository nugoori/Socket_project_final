package server;




import java.io.BufferedReader;



import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;




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
				System.out.println("클라이언트 연결 종료");
				break;
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
			case "removeRoom" :
				removeRoom(requsetBody);
				break;
			case "toSendMessage" :
				toSendMessage(requsetBody);
				break;
			case "removeSocket" :
//				removeSocket(requsetBody);
				break;
		}
		
		
	}
		
	
	private void removeSocket() {
		
	}
	
	
	
	
	
	
	private void toSendMessage(String requestBody) {
		TypeToken<RequestBodyDto<SendMessage>> typeToken = new TypeToken<RequestBodyDto<SendMessage>>() {};
		
		RequestBodyDto<SendMessage> requestBodyDto = gson.fromJson(requestBody, typeToken.getType());
		SendMessage sendMessage = requestBodyDto.getBody();	
		String toSend = sendMessage.getToUsername();
		
		Server.roomList.forEach(room -> {
			if(room.getUserList().contains(this)) {
				room.getUserList().forEach(toSenduser -> {
					if(toSenduser.username.equals(toSend)) {
						RequestBodyDto<String> dto = 
								new RequestBodyDto<String>("showMessage", sendMessage.getFromUsername() + "님의 귓속말: " + sendMessage.getMessageBody());
						ServerSender.getInstance().send(toSenduser.socket, dto);					
					}
				});
				
				room.getUserList().forEach(myName -> {
					if(myName.username.equals(username)) {
						RequestBodyDto<String> dto = 
								new RequestBodyDto<String>("showMessage", sendMessage.getToUsername() + "님에게 귓속말: " + sendMessage.getMessageBody());
						ServerSender.getInstance().send(myName.socket, dto);	
					}
				});
				
			}			
		});	
	}
	
	
	
	
	
	private void removeRoom(String requestBody) {
		// 객체
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		

		
		List<String> roomNameList = new ArrayList<>();
		Server.roomList.forEach(item -> {
			roomNameList.add(item.getRoomName());
		});
		
		// 채팅방에 있던 인원들에게만 전송하는 forEach문
		Server.roomList.forEach(room -> {							
			if(room.getRoomName().equals(roomName)) {		
				// 방에 남아있는 인원 내보내기				
				RequestBodyDto<String> exitMessage = new RequestBodyDto<>("exitChattingRoom", "채팅방이 삭제되었습니다.");
				room.getUserList().forEach(con -> {
					ServerSender.getInstance().send(con.socket, exitMessage);					
				});		
			}
		});
		
		// 서버에서 삭제할 방 객체 골라서 삭제		
		Server.roomList.removeIf(room -> room.getRoomName().equals(roomName));
		
		// 클라이언트에 표시할 룸리스트 초기화
		roomNameList.clear();
		Server.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});
		
		// 클라이언트 룸리스트 업데이트			
			RequestBodyDto<List<String>> updateRoomListRequestBodyDto = 
					new RequestBodyDto<List<String>>("updateRoomList", roomNameList);
			Server.connectedSocketList.forEach(con -> {
				ServerSender.getInstance().send(con.socket, updateRoomListRequestBodyDto);
			});
			
	}
	
	
	
	
	
	
	
	
	
	private void connection(String requestBody) {
		// 객체
		username = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		
		List<String> roomNameList = new ArrayList<>();
		
		// 서버에서 룸리스트 불러오기
		Server.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});
		
		// 클라이언트에 불러온 리스트 업데이트
		RequestBodyDto<List<String>> updateRoomListRequestBodyDto = 
				new RequestBodyDto<List<String>>("updateRoomList", roomNameList);
		
		ServerSender.getInstance().send(socket, updateRoomListRequestBodyDto);
	}
	
	
	
	
	
	
	
	private void createRoom(String requestBody) {
		// 객체
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		String ownerUser = username + " <방장>";
		
		Room newRoom = Room.builder()
			.roomName(roomName)
			.owner(ownerUser)
			.userList(new ArrayList<ConnectedSocket>())
			.build();
		
		// 생성된 방 서버에 저장
		Server.roomList.add(newRoom);
		
		List<String> roomNameList = new ArrayList<>();
		
		Server.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});
		
		// 메세지 창 초기화
		RequestBodyDto<String> removeTextArea =
				new RequestBodyDto<String>("removeTextArea", "");
		ServerSender.getInstance().send(this.socket, removeTextArea);

		// 업데이트
		RequestBodyDto<List<String>> updateRoomListRequestBodyDto = 
				new RequestBodyDto<List<String>>("updateRoomList", roomNameList);
		
		Server.connectedSocketList.forEach(con -> {
			ServerSender.getInstance().send(con.socket, updateRoomListRequestBodyDto);	
		});		
	}
	
	
	
	
	
	
	
	private void join(String requestBody) {
		// 객체
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();	
		List<String> roomNameList = new ArrayList<>();	
		Server.roomList.forEach(room -> {			
			roomNameList.add(room.getRoomName());			
		});	
		
		
		// 삭제된 방 입장 방지
		if(!roomNameList.contains(roomName)) {
			System.out.println("test");
			RequestBodyDto<String> exitMessage =
				new RequestBodyDto<>("exitChattingRoom", "방이 존재하지 않습니다.");				
			ServerSender.getInstance().send(this.socket, exitMessage);
		
			RequestBodyDto<List<String>> updateUserListRequestBodyDto =
					new RequestBodyDto<List<String>>("updateRoomList", new ArrayList());
			ServerSender.getInstance().send(this.socket, updateUserListRequestBodyDto);
			
			RequestBodyDto<String> removeTextArea =
					new RequestBodyDto<String>("removeTextArea", " ");
			ServerSender.getInstance().send(this.socket, removeTextArea);
			
			RequestBodyDto<List<String>> updateRoomListRequestBodyDto =
				new RequestBodyDto<List<String>>("updateRoomList", roomNameList);			
			ServerSender.getInstance().send(socket, updateRoomListRequestBodyDto);					
		}
		
		
		Server.roomList.forEach(room -> {			
			if(room.getRoomName().equals(roomName)) {
				room.getUserList().add(this);
				
				List<String> usernameList = new ArrayList<>();
				
				// 메세지 창 초기화
				RequestBodyDto<String> removeTextArea =
						new RequestBodyDto<String>("removeTextArea", "");
				ServerSender.getInstance().send(this.socket, removeTextArea);
				
				room.getUserList().forEach(con -> {				
					usernameList.add(con.username);				
				});
				
				// 업데이트
				room.getUserList().forEach(connectedSocket -> {
					RequestBodyDto<List<String>> updateUserListDto = 
							new RequestBodyDto<List<String>>("updateUserList", usernameList);
					RequestBodyDto<String> joinMessageDto = 
							new RequestBodyDto<String>("showMessage", username + "님이 들어왔습니다.");
					
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
		// 객체
		String roomName = (String) gson.fromJson(requsetBody, RequestBodyDto.class).getBody();	

		// 채팅방 안 리스트에서 내 이름 삭제
		Server.roomList.forEach(room -> {
			if(room.getRoomName().equals(roomName)) {
				room.getUserList().remove(this);
				
				List<String> usernameList = new ArrayList<>();
				
				room.getUserList().forEach(con -> {
					usernameList.add(con.username);
				});
	
				// 업데이트 및 나가기 메세지 전송
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
				
				
			}
			
			
		});			
	}	
}











	
/* 사용 가능한 방법들
	
				System.out.println(requestBody);
				simpleGUIServer.connectedSocketList.forEach( connectedSocket -> {
					try {
						PrintWriter printWriter = new PrintWriter(connectedSocket.socket.getOutputStream(), true);
						printWriter.println(requestBody);
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
				
	case2		for(ConnectedSocket connectedSocket = simpleGUIServer.connectedSocketList) {}
				
  	case3		for(int i = 0; i < simpleGUIServer.connectedSocketList.size(); i++) {}		
				
*/
		 
		 
	
	

	 
	

