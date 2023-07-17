package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.entity.Room;

public class Server {
	public static List<ConnectedSocket> connectedSocketList = new ArrayList<>();
	public static Socket socket;
	public static List<Room> roomList = new ArrayList<>();
	public static Thread thread;
	public static ServerSocket serverSocket;
	
	public static void main(String[] args) {				
		thread = new Thread( () -> {
			try {
				System.out.println("[ 서버 실행 ]");
				serverSocket = new ServerSocket(8000);
				  
					while(true) {
							socket = serverSocket.accept();
							System.out.println("클라이언트 감지");
							ConnectedSocket connectedSocket = new ConnectedSocket(socket);														
							connectedSocketList.add(connectedSocket);
							connectedSocket.start();
					}									
				} catch (IOException e) {
				e.printStackTrace();
				}
		});
		thread.start();
		
		
		
	}
}
