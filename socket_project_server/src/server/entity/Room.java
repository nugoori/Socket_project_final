package server.entity;

import java.util.List;



import lombok.Builder;
import lombok.Data;
import server.ConnectedSocket;

@Builder
@Data
public class Room {
	private String roomName;
	private String owner;
	private List<ConnectedSocket> userList;
	
	
}
