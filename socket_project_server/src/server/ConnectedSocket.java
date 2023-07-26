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
	
	private Gson gson;
	private String username;
	private final Socket socket;
	RequestBodyDto<String> removeTextArea;
	RequestBodyDto<String> exitMessage;
	SendMessage sendMessage;
	
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
		
		System.out.println(resource);
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
//			case "toSendMessage" :
//				toSendMessage(requsetBody);
//				break;
		}
	}
	private void updateRoomList(boolean isConnection) {
		List<String> roomNameList = new ArrayList<>();
		
		Server.roomList.forEach(room -> {
			roomNameList.add(room.getRoomName());
		});
		
		RequestBodyDto<List<String>> updateRoomListRequestBodyDto =
				new RequestBodyDto<List<String>>("updateRoomList", roomNameList);
		
		if(isConnection) {
			ServerSender.getInstance().send(socket, updateRoomListRequestBodyDto);
			return;
		}
		
		Server.connectedSocketList.forEach(con -> {
			ServerSender.getInstance().send(con.socket, updateRoomListRequestBodyDto);
		});
	}
	
	private void updateUsernameList(Room room) {
		List<String> usernameList = new ArrayList<>();
		
		room.getUserList().forEach(con -> {
			usernameList.add(con.username);
		});
		
		RequestBodyDto<List<String>> updateUsernameListRequestDto = 
				new RequestBodyDto<List<String>>("updateUserList", usernameList);
		
		room.getUserList().forEach(con -> {
			ServerSender.getInstance().send(con.socket, updateUsernameListRequestDto);			
		});
	}
	
	private void connection(String requestBody) {
		// 객체
		username = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		
		// 서버에서 룸리스트 불러오기
		/* 반복을 돌려서 클라에 데이터를 보내주는 부븐이 중복됨 */
		updateRoomList(false);
	}
	
	private void createRoom(String requestBody) {
		// 객체
		String roomName = (String) gson.fromJson(requestBody, RequestBodyDto.class).getBody();
		String ownerUser = username;
		
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
		removeTextArea =
				new RequestBodyDto<String>("removeTextArea", "");
		ServerSender.getInstance().send(this.socket, removeTextArea);
		
		// 업데이트
		RequestBodyDto<List<String>> updateRoomListRequestBodyDto = 
				new RequestBodyDto<List<String>>("updateRoomList", roomNameList);
		
		Server.connectedSocketList.forEach(con -> {
			ServerSender.getInstance().send(con.socket, updateRoomListRequestBodyDto);	
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
				exitMessage = new RequestBodyDto<>("exitChattingRoom", "채팅방이 삭제되었습니다.");
				room.getUserList().forEach(con -> {
					ServerSender.getInstance().send(con.socket, exitMessage);					
				});		
			}
		});
		
		// 서버에서 삭제할 방 객체 골라서 삭제
		/* Predicate : 제네릭 타입의 매개변수를 전달받아 특정 작업을 수행 후 Boolean타입을 반환하는 작업을 수행할 때 사용 */
		/* removeIf : removeIf로 넘어온 술어 조건을 만족하는 원소를 모두 제거하는 메소드.
		 * Predicate를 매개변수로 받아 Iterator생성자를 생성하여 Iterator.hasNext()로 반복을 돌려서 Predicate매개변수.test(Iterator.next)가 true이면(test에서 람다식으로 넘어온 조건을 검사후 T/F를 반환) Iterator에서 삭제? */

							/*  Predicate의 test함수를 람다로 넘겨줌 /  filter로 넘어옴?  */
		Server.roomList.removeIf(room -> room.getRoomName().equals(roomName));
		
		// 클라이언트에 표시할 룸리스트 초기화
		roomNameList.clear();
		updateRoomList(false);
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
			exitMessage =
				new RequestBodyDto<>("exitChattingRoom", "방이 존재하지 않습니다.");				
			ServerSender.getInstance().send(this.socket, exitMessage);
		
			RequestBodyDto<List<String>> updateUserListRequestBodyDto =
					new RequestBodyDto<List<String>>("updateRoomList", new ArrayList());
			ServerSender.getInstance().send(this.socket, updateUserListRequestBodyDto);
			
			removeTextArea =
					new RequestBodyDto<String>("removeTextArea", "");
			ServerSender.getInstance().send(this.socket, removeTextArea);
			
			RequestBodyDto<List<String>> updateRoomListRequestBodyDto =
				new RequestBodyDto<List<String>>("updateRoomList", roomNameList);			
			ServerSender.getInstance().send(socket, updateRoomListRequestBodyDto);					
		}
		
		
		Server.roomList.forEach(room -> {
			if(room.getRoomName().equals(roomName)) {
				room.getUserList().add(this);
				// 메세지 창 초기화
				removeTextArea =
						new RequestBodyDto<String>("removeTextArea", "");
				ServerSender.getInstance().send(this.socket, removeTextArea);
				
				// 업데이트
				updateUsernameList(room);
				room.getUserList().forEach(connectedSocket -> {
					RequestBodyDto<String> joinMessageDto = 
							new RequestBodyDto<String>("showMessage", username + "님이 들어왔습니다.");
					
					ServerSender.getInstance().send(connectedSocket.socket, joinMessageDto);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
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
				/*  userList<ConnectedSocket> class ConnectedSocket > this사용 가능  */
				room.getUserList().remove(this);
				
				// 업데이트 및 나가기 메세지 전송
				updateUsernameList(room);
				room.getUserList().forEach(connectedSocket -> {		
					RequestBodyDto<String> exitMessageDto = new RequestBodyDto<String>("showMessage", username + "님이 나갔습니다.");
				
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
	
//	private void toSendMessage(String requestBody) {
//		TypeToken<RequestBodyDto<SendMessage>> typeToken = new TypeToken<RequestBodyDto<SendMessage>>() {};
//		
//		RequestBodyDto<SendMessage> requestBodyDto = gson.fromJson(requestBody, typeToken.getType());
//		sendMessage = requestBodyDto.getBody();	
//		
//		String toSend = sendMessage.getToUsername();
//		
//		Server.roomList.forEach(room -> {
//			if(room.getUserList().contains(this)) {
//				room.getUserList().forEach(con -> {
//					// 
//					if((con.username + " <방장>").equals(toSend + "<방장>") || con.username.equals(toSend)) {
//						RequestBodyDto<String> dto = 
//								new RequestBodyDto<String>("showMessage", sendMessage.getFromUsername() + "님의 귓속말: " + sendMessage.getMessageBody());
//						ServerSender.getInstance().send(con.socket, dto);					
//					}
//				});
//				
//				room.getUserList().forEach(myName -> {
//					if(myName.username.equals(username)) {
//						RequestBodyDto<String> dto = 
//								new RequestBodyDto<String>("showMessage", sendMessage.getToUsername() + "님에게 귓속말: " + sendMessage.getMessageBody());
//						ServerSender.getInstance().send(myName.socket, dto);	
//					}
//				});
//			}			
//		});	
//	}
	
	/* roomListUpdate를 메소드로 만들어서 boolean isConnection을 매개변수로 받아 전체에게 주거나 연결된 socket에서만 실행하도록 */
	
	private void sendMessage(String requestBody) {
		TypeToken<RequestBodyDto<SendMessage>> typeToken = new TypeToken<RequestBodyDto<SendMessage>>() {};
		
		RequestBodyDto<SendMessage> requestBodyDto = gson.fromJson(requestBody, typeToken.getType());
		System.out.println(requestBodyDto);
		sendMessage = requestBodyDto.getBody();	
		
		System.out.println(sendMessage.toString()); // 여기서도 안뜨나
		String toSend = sendMessage.getToUsername();
//		System.out.println(toSend.toString()); // toSend가 null로 들어옴
		
		/* if(Objects.isNull(sendMessage.getToUsername()) { }*/
		
		if(toSend != null) {
			Server.roomList.forEach(room -> {
				if(room.getUserList().contains(this)) {
					room.getUserList().forEach(con -> {
						// 
						if((con.username + " <방장>").equals(toSend + "<방장>") || con.username.equals(toSend)) {
							RequestBodyDto<String> dto = new RequestBodyDto<String>("showMessage", toSend + "님의 귓속말: " + sendMessage.getMessageBody());
							ServerSender.getInstance().send(con.socket, dto);					
						}
					});
					
					room.getUserList().forEach(myName -> {
						if(myName.username.equals(username)) {
							RequestBodyDto<String> dto = new RequestBodyDto<String>("showMessage", toSend + "님에게 귓속말: " + sendMessage.getMessageBody());
							ServerSender.getInstance().send(myName.socket, dto);	
						}
					});
				}			
			});	
		} else {
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
		
	}
	
	
}

		 
		 
	
	

	 
	

