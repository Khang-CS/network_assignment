package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
  private ServerSocket serverSocket;
  
  public Server(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }
  
  public void startServer() {
    try {
      while (!this.serverSocket.isClosed()) {
        Socket socket = this.serverSocket.accept();
        System.out.println("A new client has connected");
        ClientHandler clientHandler = new ClientHandler(socket);
        Thread thread = new Thread(clientHandler);
        thread.start();
      } 
    } catch (IOException e) {
      closeServerSocket();
    } 
  }
  
  public void closeServerSocket() {
    try {
      if (this.serverSocket != null)
        this.serverSocket.close(); 
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public void run() {
    try {
      while (!this.serverSocket.isClosed()) {
        Socket socket = this.serverSocket.accept();
        System.out.println("A new client has connected");
        ClientHandler clientHandler = new ClientHandler(socket);
        Thread thread = new Thread(clientHandler);
        thread.start();
      } 
    } catch (IOException e) {
      closeServerSocket();
    } 
  }
  
  public void stop() {
    closeServerSocket();
    System.out.println("Stop server");
    System.exit(0);
  }
}
