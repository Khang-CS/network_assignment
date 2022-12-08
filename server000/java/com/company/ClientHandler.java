package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
  public static ArrayList<ClientHandler> clientHandlers = new ArrayList<ClientHandler>();
  
  private Socket socket;
  
  private DataInputStream dataInputStream;
  
  private DataOutputStream dataOutputStream;
  
  private String clientUsername;
  
  public static ArrayList<String> onlineUser = new ArrayList<String>();
  
  public static ArrayList<String> accountUser = new ArrayList<String>();
  
  public ClientHandler(Socket socket) {
    try {
      this.socket = socket;
      this.dataInputStream = new DataInputStream(socket.getInputStream());
      this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
      String messageFromClient = this.dataInputStream.readUTF();
      if (messageFromClient.contains("LOGIN:")) {
        String user = messageFromClient.split(":")[1];
        if (accountUser.contains(user)) {
          if (!onlineUser.contains(user)) {
            responseMessage("SERVER->LOGIN:" + user + ":accepted");
            this.clientUsername = user;
            onlineUser.add(user);
            clientHandlers.add(this);
          } else {
            responseMessage("SERVER->LOGIN:" + user + ":now login");
          } 
        } else {
          responseMessage("SERVER->LOGIN:" + user + ":not register");
        } 
      } else if (messageFromClient.contains("REGISTER:")) {
        String user = messageFromClient.split(":")[1];
        if (accountUser.contains(user)) {
          responseMessage("SERVER->REGISTER:" + user + ":user existed");
        } else {
          accountUser.add(user);
          responseMessage("SERVER->REGISTER:" + user + ":register successful");
        } 
      } 
    } catch (IOException e) {
      closeEverything(socket, this.dataInputStream, this.dataOutputStream);
    } 
  }
  
  public void run() {
    while (!this.socket.isClosed()) {
      try {
        String messageFromClient = this.dataInputStream.readUTF();
        if (messageFromClient != null) {
          if (messageFromClient.contains("ENTER:")) {
            broadcastMessage("Server->All:New:" + messageFromClient.split(":")[1] + " has entered the chat");
            String onlineList = "";
            for (int i = 0; i < onlineUser.size(); i++)
              onlineList = onlineList + onlineList + ";;"; 
            broadcastMessage("Server->All:Online:" + onlineList);
            continue;
          } 
          if (messageFromClient.contains("PUBLIC::")) {
            broadcastMessage(messageFromClient);
            continue;
          } 
          if (messageFromClient.contains("PRIVATE::")) {
            forwardMessage(messageFromClient);
            continue;
          } 
          if (messageFromClient.contains("FILE::"))
            forwardFile(messageFromClient); 
        } 
      } catch (IOException e) {
        closeEverything(this.socket, this.dataInputStream, this.dataOutputStream);
        break;
      } 
    } 
  }
  
  public void broadcastMessage(String messageToSend) {
    for (ClientHandler clientHandler : clientHandlers) {
      try {
        clientHandler.dataOutputStream.writeUTF(messageToSend);
        clientHandler.dataOutputStream.flush();
      } catch (IOException e) {
        closeEverything(this.socket, this.dataInputStream, this.dataOutputStream);
      } 
    } 
  }
  
  public void responseMessage(String messageToSend) {
    try {
      this.dataOutputStream.writeUTF(messageToSend);
      this.dataOutputStream.flush();
    } catch (IOException e) {
      closeEverything(this.socket, this.dataInputStream, this.dataOutputStream);
    } 
  }
  
  public void forwardMessage(String messageToSend) {
    for (ClientHandler clientHandler : clientHandlers) {
      try {
        String temp = messageToSend.split("::")[1];
        String receiver = temp.split("-->")[1];
        String fromUser = temp.split("-->")[0];
        if (clientHandler.clientUsername.equals(receiver) || clientHandler.clientUsername.equals(fromUser)) {
          clientHandler.dataOutputStream.writeUTF(messageToSend);
          clientHandler.dataOutputStream.flush();
        } 
      } catch (IOException e) {
        closeEverything(this.socket, this.dataInputStream, this.dataOutputStream);
      } 
    } 
  }
  
  public void forwardFile(String messageToSend) throws IOException {
    int fileNameLength = this.dataInputStream.readInt();
    if (fileNameLength > 0) {
      byte[] fileNameBytes = new byte[fileNameLength];
      this.dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
      String fileName = new String(fileNameBytes);
      int fileLen = this.dataInputStream.readInt();
      if (fileLen > 0) {
        byte[] fileContentBytes = new byte[fileLen];
        this.dataInputStream.readFully(fileContentBytes, 0, fileLen);
        String temp = messageToSend.split("::")[1];
        String receiver = temp.split("-->")[1];
        String fromUser = temp.split("-->")[0];
        for (ClientHandler clientHandler : clientHandlers) {
          try {
            if (clientHandler.clientUsername.equals(receiver)) {
              clientHandler.dataOutputStream.writeUTF("FILE::" + fromUser + "-->" + receiver);
              clientHandler.dataOutputStream.writeInt(fileNameBytes.length);
              clientHandler.dataOutputStream.write(fileNameBytes);
              clientHandler.dataOutputStream.writeInt(fileContentBytes.length);
              clientHandler.dataOutputStream.write(fileContentBytes);
              clientHandler.dataOutputStream.flush();
            } 
          } catch (IOException e) {
            closeEverything(this.socket, this.dataInputStream, this.dataOutputStream);
          } 
        } 
      } 
    } 
  }
  
  public void removeClientHandler() {
    clientHandlers.remove(this);
    if (this.clientUsername != null)
      broadcastMessage("Server->All:New:" + this.clientUsername + " has left the chat"); 
    onlineUser.remove(this.clientUsername);
    String onlineList = "";
    for (int i = 0; i < onlineUser.size(); i++)
      onlineList = onlineList + onlineList + ";;"; 
    broadcastMessage("Server->All:Online:" + onlineList);
  }
  
  public void closeEverything(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
    removeClientHandler();
    try {
      if (dataInputStream != null)
        dataInputStream.close(); 
      if (dataOutputStream != null)
        dataOutputStream.close(); 
      if (socket != null)
        socket.close(); 
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
