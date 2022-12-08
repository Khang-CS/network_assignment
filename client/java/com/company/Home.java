package com.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;

public class Home extends JPanel {
  private int port;
  
  private Socket socket;
  
  private DataOutputStream dataOutputStream;
  
  private DataInputStream dataInputStream;
  
  private String username;
  
  HashMap<String, String> messPrivate = new HashMap<>();
  
  HashSet<String> newMessList = new HashSet<>();
  
  HashMap<String, byte[]> drive = (HashMap)new HashMap<>();
  
  HashMap<String, ArrayList<String>> listFileInDrive = new HashMap<>();
  
  String messDashboard = "";
  
  DefaultTableModel onlineModel;
  
  DefaultTableModel newMessModel;
  
  DefaultTableModel fileMessModel;
  
  int selectedIndexFriendTable;
  
  int selectedIndexMessTable;
  
  private JButton closeButton;
  
  private JPanel countPane;
  
  private JPanel dashboardTabPane;
  
  private JScrollPane fileScrollPane;
  
  private JTable fileTable;
  
  private JLabel friendChatLabel;
  
  private JLabel friendLabel;
  
  private JPanel inboxTabPane;
  
  private JTextField inputAllField;
  
  private JPanel inputAllPane;
  
  private JPanel inputAllPane2;
  
  private JTextField inputPrivateField;
  
  private JLabel jLabel1;
  
  private JPanel jPanel1;
  
  private JPanel jPanel2;
  
  private JPanel jPanel3;
  
  private JPanel jPanel4;
  
  private JPanel jPanel5;
  
  private JScrollPane jScrollPane1;
  
  private JTabbedPane jTabbedPane1;
  
  private JScrollPane messAllScrollPane;
  
  private JTextPane messPrivateArea;
  
  private JTextArea messageAllArea;
  
  private JScrollPane newMessScrollPane;
  
  private JTable newMessTable;
  
  private JLabel onlineCountLabel;
  
  private JButton sendAllButton;
  
  private JButton sendButton;
  
  private JButton sendFileButton;
  
  private JPanel sidebarPane;
  
  private JTable tableFriend;
  
  private JScrollPane tableFriendScrollPane;
  
  private JPanel tablePane;
  
  private JLabel usernameLabel;
  
  public Home(Socket socket, String username, int portNumber) {
    try {
      this.socket = socket;
      this.port = portNumber;
      this.username = username;
      this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
      this.dataInputStream = new DataInputStream(socket.getInputStream());
      this.dataOutputStream.writeUTF("ENTER:" + username);
      this.dataOutputStream.flush();
    } catch (IOException e) {
      closeEverything(socket, this.dataInputStream, this.dataOutputStream);
      return;
    } 
    initComponents();
    listenForMessage();
    this.usernameLabel.setText(username);
  }
  
  public void sendMessage(String messageToSend) {
    try {
      if (this.socket.isConnected()) {
        this.dataOutputStream.writeUTF(messageToSend);
        this.dataOutputStream.flush();
      } 
    } catch (IOException e) {
      closeEverything(this.socket, this.dataInputStream, this.dataOutputStream);
    } 
  }
  
  public void loadDataIntoTable(ArrayList<String> users) {
    this.onlineModel = new DefaultTableModel();
    Vector<String> headerColumn = new Vector();
    headerColumn.add("Friends");
    this.onlineModel.setColumnIdentifiers(headerColumn);
    for (String e : users) {
      Vector<String> row = new Vector();
      row.add(e);
      this.onlineModel.addRow(row);
    } 
    this.tableFriend.setModel(this.onlineModel);
  }
  
  private void updateOnlineTable(String msg) {
    String onlineListString = msg.split(":")[2];
    ArrayList<String> onlineList = new ArrayList<>(Arrays.asList(onlineListString.split(";;")));
    loadDataIntoTable(onlineList);
  }
  
  public void loadDataIntoNewMessTable(ArrayList<String> users) {
    this.newMessModel = new DefaultTableModel();
    Vector<String> headerColumn = new Vector();
    headerColumn.add("New Message With:");
    this.newMessModel.setColumnIdentifiers(headerColumn);
    for (String e : users) {
      Vector<String> row = new Vector();
      row.add(e);
      this.newMessModel.addRow(row);
    } 
    this.newMessTable.setModel(this.newMessModel);
  }
  
  private void updateNewChatTable() {
    ArrayList<String> newChat = new ArrayList<>(this.newMessList);
    loadDataIntoNewMessTable(newChat);
  }
  
  public void updateDriveFileTable() {
    this.fileMessModel = new DefaultTableModel();
    Vector<String> headerColumn = new Vector();
    headerColumn.add("From");
    headerColumn.add("File Name");
    this.fileMessModel.setColumnIdentifiers(headerColumn);
    for (Map.Entry<String, ArrayList<String>> entry : this.listFileInDrive.entrySet()) {
      ArrayList<String> values = entry.getValue();
      for (String e : values) {
        Vector<String> row = new Vector();
        row.add(entry.getKey());
        row.add(e);
        this.fileMessModel.addRow(row);
      } 
    } 
    this.fileTable.setModel(this.fileMessModel);
  }
  
  public void handleReceiveFile(String msgFromGroupChat) throws IOException {
    String temp = msgFromGroupChat.split("::")[1];
    String fromUser = temp.split("-->")[0];
    String receiver = temp.split("-->")[1];
    if (fromUser.equals(this.username)) {
      fromUser = "You";
      String chatWith = receiver;
    } else {
      String chatWith = fromUser;
    } 
    int fileNameLength = this.dataInputStream.readInt();
    if (fileNameLength > 0) {
      byte[] fileNameBytes = new byte[fileNameLength];
      this.dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
      String fileName = new String(fileNameBytes);
      int fileLen = this.dataInputStream.readInt();
      if (fileLen > 0) {
        byte[] fileContentBytes = new byte[fileLen];
        this.dataInputStream.readFully(fileContentBytes, 0, fileLen);
        if (this.drive.containsKey(fileName)) {
          int ret = JOptionPane.showConfirmDialog(this.jPanel1, "This file name is exist in your drive. Do you want to overwrite?", "Confirm ", 0);
          if (ret != 0)
            return; 
        } 
        this.drive.put(fileName, fileContentBytes);
        System.out.println("receive file ok");
        if (this.listFileInDrive.containsKey(fromUser) == true) {
          ArrayList<String> filenames = this.listFileInDrive.get(fromUser);
          filenames.add(fileName);
          this.listFileInDrive.put(fromUser, filenames);
        } else {
          ArrayList<String> fileNames = new ArrayList<>();
          fileNames.add(fileName);
          this.listFileInDrive.put(fromUser, fileNames);
        } 
        updateDriveFileTable();
      } 
    } 
  }
  
  public void listenForMessage() {
    (new Thread(new Runnable() {
          public void run() {
            while (Home.this.socket.isConnected()) {
              try {
                String msgFromGroupChat = Home.this.dataInputStream.readUTF();
                if (msgFromGroupChat.contains("Server->All:New:")) {
                  String temp = msgFromGroupChat.split(":")[2];
                  Home.this.messDashboard = Home.this.messDashboard + Home.this.messDashboard + "\n\n";
                  Home.this.messageAllArea.setEditable(true);
                  Home.this.messageAllArea.setText(Home.this.messDashboard);
                  Home.this.messageAllArea.setEditable(false);
                  continue;
                } 
                if (msgFromGroupChat.contains("Server->All:Online:")) {
                  Home.this.updateOnlineTable(msgFromGroupChat);
                  continue;
                } 
                if (msgFromGroupChat.contains("PUBLIC::")) {
                  String fromUser = msgFromGroupChat.split("::")[1];
                  String content = msgFromGroupChat.split("::")[2];
                  if (fromUser.equals(Home.this.username))
                    fromUser = "You"; 
                  Home.this.messDashboard = Home.this.messDashboard + Home.this.messDashboard + ": " + fromUser + "\n\n";
                  Home.this.messageAllArea.setEditable(true);
                  Home.this.messageAllArea.setText(Home.this.messDashboard);
                  Home.this.messageAllArea.setEditable(false);
                  continue;
                } 
                if (msgFromGroupChat.contains("PRIVATE::")) {
                  String chatWith, mess, temp = msgFromGroupChat.split("::")[1];
                  String fromUser = temp.split("-->")[0];
                  String receiver = temp.split("-->")[1];
                  String content = msgFromGroupChat.split("::")[2];
                  if (fromUser.equals(Home.this.username)) {
                    fromUser = "You";
                    chatWith = receiver;
                  } else {
                    chatWith = fromUser;
                  } 
                  Home.this.newMessList.add(chatWith);
                  Home.this.updateNewChatTable();
                  if (Home.this.messPrivate.containsKey(chatWith) == true) {
                    mess = (String)Home.this.messPrivate.get(chatWith) + (String)Home.this.messPrivate.get(chatWith) + ": " + fromUser + "\n\n";
                  } else {
                    mess = fromUser + ": " + fromUser + "\n\n";
                  } 
                  Home.this.friendChatLabel.setText(chatWith);
                  Home.this.messPrivate.put(chatWith, mess);
                  Home.this.messPrivateArea.setEditable(true);
                  Home.this.messPrivateArea.setText(mess);
                  Home.this.messPrivateArea.setEditable(false);
                  continue;
                } 
                if (msgFromGroupChat.contains("FILE::"))
                  Home.this.handleReceiveFile(msgFromGroupChat); 
              } catch (IOException e) {
                Home.this.closeEverything(Home.this.socket, Home.this.dataInputStream, Home.this.dataOutputStream);
              } 
            } 
          }
        })).start();
  }
  
  public void closeEverything(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
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
  
  private void initComponents() {
    this.sidebarPane = new JPanel();
    this.countPane = new JPanel();
    this.onlineCountLabel = new JLabel();
    this.usernameLabel = new JLabel();
    this.tablePane = new JPanel();
    this.tableFriendScrollPane = new JScrollPane();
    this.tableFriend = new JTable();
    this.jPanel1 = new JPanel();
    this.newMessScrollPane = new JScrollPane();
    this.newMessTable = new JTable();
    this.jPanel2 = new JPanel();
    this.closeButton = new JButton();
    this.jPanel5 = new JPanel();
    this.fileScrollPane = new JScrollPane();
    this.fileTable = new JTable();
    this.jLabel1 = new JLabel();
    this.jTabbedPane1 = new JTabbedPane();
    this.dashboardTabPane = new JPanel();
    this.messAllScrollPane = new JScrollPane();
    this.messageAllArea = new JTextArea();
    this.inputAllPane = new JPanel();
    this.sendAllButton = new JButton();
    this.inputAllField = new JTextField();
    this.inboxTabPane = new JPanel();
    this.inputAllPane2 = new JPanel();
    this.sendButton = new JButton();
    this.inputPrivateField = new JTextField();
    this.sendFileButton = new JButton();
    this.jPanel3 = new JPanel();
    this.jPanel4 = new JPanel();
    this.friendLabel = new JLabel();
    this.jScrollPane1 = new JScrollPane();
    this.messPrivateArea = new JTextPane();
    this.friendChatLabel = new JLabel();
    this.onlineCountLabel.setText("Your username:");
    GroupLayout countPaneLayout = new GroupLayout(this.countPane);
    this.countPane.setLayout(countPaneLayout);
    countPaneLayout.setHorizontalGroup(countPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(countPaneLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.onlineCountLabel)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.usernameLabel, -2, 91, -2)
          .addContainerGap(-1, 32767)));
    countPaneLayout.setVerticalGroup(countPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(countPaneLayout.createSequentialGroup()
          .addContainerGap(-1, 32767)
          .addGroup(countPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.onlineCountLabel)
            .addComponent(this.usernameLabel))));
    this.tableFriend.setModel(new DefaultTableModel(new Object[0][], (Object[])new String[] { "Friends" }) {
          Class[] types = new Class[] { String.class };
          
          boolean[] canEdit = new boolean[] { false };
          
          public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
          }
          
          public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
          }
        });
    this.tableFriend.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent evt) {
            Home.this.tableFriendMouseClicked(evt);
          }
        });
    this.tableFriendScrollPane.setViewportView(this.tableFriend);
    GroupLayout tablePaneLayout = new GroupLayout(this.tablePane);
    this.tablePane.setLayout(tablePaneLayout);
    tablePaneLayout.setHorizontalGroup(tablePaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.tableFriendScrollPane, -2, 0, 32767));
    tablePaneLayout.setVerticalGroup(tablePaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.tableFriendScrollPane, -1, 122, 32767));
    this.newMessTable.setModel(new DefaultTableModel(new Object[0][], (Object[])new String[] { "New Message With" }) {
          Class[] types = new Class[] { String.class };
          
          boolean[] canEdit = new boolean[] { false };
          
          public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
          }
          
          public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
          }
        });
    this.newMessTable.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent evt) {
            Home.this.newMessTableMouseClicked(evt);
          }
        });
    this.newMessScrollPane.setViewportView(this.newMessTable);
    GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
    this.jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.newMessScrollPane, -2, 0, 32767));
    jPanel1Layout.setVerticalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.newMessScrollPane, -2, 0, 32767));
    this.closeButton.setText("Close");
    this.closeButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            Home.this.closeButtonActionPerformed(evt);
          }
        });
    GroupLayout jPanel2Layout = new GroupLayout(this.jPanel2);
    this.jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(jPanel2Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
          .addContainerGap(116, 32767)
          .addComponent(this.closeButton)
          .addGap(112, 112, 112)));
    jPanel2Layout.setVerticalGroup(jPanel2Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.closeButton)
          .addContainerGap(34, 32767)));
    this.fileTable.setModel(new DefaultTableModel(new Object[0][], (Object[])new String[] { "From", "Filename" }) {
          Class[] types = new Class[] { String.class, String.class };
          
          boolean[] canEdit = new boolean[] { false, false };
          
          public Class getColumnClass(int columnIndex) {
            return this.types[columnIndex];
          }
          
          public boolean isCellEditable(int rowIndex, int columnIndex) {
            return this.canEdit[columnIndex];
          }
        });
    this.fileTable.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent evt) {
            Home.this.fileTableMouseClicked(evt);
          }
        });
    this.fileScrollPane.setViewportView(this.fileTable);
    this.jLabel1.setText("Drive File: ");
    GroupLayout jPanel5Layout = new GroupLayout(this.jPanel5);
    this.jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(jPanel5Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.fileScrollPane, -1, 289, 32767)
        .addGroup(jPanel5Layout.createSequentialGroup()
          .addComponent(this.jLabel1)
          .addGap(0, 0, 32767)));
    jPanel5Layout.setVerticalGroup(jPanel5Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
          .addContainerGap(-1, 32767)
          .addComponent(this.jLabel1)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.fileScrollPane, -2, 97, -2)
          .addGap(0, 0, 0)));
    GroupLayout sidebarPaneLayout = new GroupLayout(this.sidebarPane);
    this.sidebarPane.setLayout(sidebarPaneLayout);
    sidebarPaneLayout.setHorizontalGroup(sidebarPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.countPane, -1, -1, 32767)
        .addComponent(this.tablePane, -1, -1, 32767)
        .addComponent(this.jPanel1, -1, -1, 32767)
        .addComponent(this.jPanel2, GroupLayout.Alignment.TRAILING, -1, -1, 32767)
        .addComponent(this.jPanel5, -1, -1, 32767));
    sidebarPaneLayout.setVerticalGroup(sidebarPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(sidebarPaneLayout.createSequentialGroup()
          .addComponent(this.countPane, -2, -1, -2)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.tablePane, -2, -1, -2)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.jPanel5, -2, -1, -2)
          .addGap(18, 18, 18)
          .addComponent(this.jPanel1, -1, -1, 32767)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.jPanel2, -2, -1, -2)));
    this.messageAllArea.setEditable(false);
    this.messageAllArea.setColumns(20);
    this.messageAllArea.setRows(5);
    this.messAllScrollPane.setViewportView(this.messageAllArea);
    this.sendAllButton.setText("Send All");
    this.sendAllButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            Home.this.sendAllButtonActionPerformed(evt);
          }
        });
    GroupLayout inputAllPaneLayout = new GroupLayout(this.inputAllPane);
    this.inputAllPane.setLayout(inputAllPaneLayout);
    inputAllPaneLayout.setHorizontalGroup(inputAllPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, inputAllPaneLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.inputAllField)
          .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
          .addComponent(this.sendAllButton)
          .addContainerGap()));
    inputAllPaneLayout.setVerticalGroup(inputAllPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(inputAllPaneLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(inputAllPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.sendAllButton)
            .addComponent(this.inputAllField, -2, -1, -2))
          .addContainerGap(-1, 32767)));
    GroupLayout dashboardTabPaneLayout = new GroupLayout(this.dashboardTabPane);
    this.dashboardTabPane.setLayout(dashboardTabPaneLayout);
    dashboardTabPaneLayout.setHorizontalGroup(dashboardTabPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.messAllScrollPane, -1, 422, 32767)
        .addComponent(this.inputAllPane, -1, -1, 32767));
    dashboardTabPaneLayout.setVerticalGroup(dashboardTabPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(dashboardTabPaneLayout.createSequentialGroup()
          .addComponent(this.messAllScrollPane, -2, 406, -2)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.inputAllPane, -2, -1, -2)
          .addContainerGap(-1, 32767)));
    this.jTabbedPane1.addTab("Dashboard", this.dashboardTabPane);
    this.sendButton.setText("Send");
    this.sendButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            Home.this.sendButtonActionPerformed(evt);
          }
        });
    this.sendFileButton.setText("File");
    this.sendFileButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            Home.this.sendFileButtonActionPerformed(evt);
          }
        });
    GroupLayout inputAllPane2Layout = new GroupLayout(this.inputAllPane2);
    this.inputAllPane2.setLayout(inputAllPane2Layout);
    inputAllPane2Layout.setHorizontalGroup(inputAllPane2Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, inputAllPane2Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.inputPrivateField)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.sendButton)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.sendFileButton)
          .addContainerGap()));
    inputAllPane2Layout.setVerticalGroup(inputAllPane2Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(inputAllPane2Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(inputAllPane2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.sendButton)
            .addComponent(this.inputPrivateField, -2, -1, -2)
            .addComponent(this.sendFileButton))
          .addContainerGap(-1, 32767)));
    this.friendLabel.setText("Friend:");
    this.messPrivateArea.setEditable(false);
    this.jScrollPane1.setViewportView(this.messPrivateArea);
    GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
    this.jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(jPanel4Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.friendLabel)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.friendChatLabel)
          .addContainerGap(331, 32767))
        .addComponent(this.jScrollPane1, GroupLayout.Alignment.TRAILING));
    jPanel4Layout.setVerticalGroup(jPanel4Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.friendLabel)
            .addComponent(this.friendChatLabel))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.jScrollPane1, -1, 370, 32767)));
    GroupLayout jPanel3Layout = new GroupLayout(this.jPanel3);
    this.jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(jPanel3Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.jPanel4, -2, -1, -2));
    jPanel3Layout.setVerticalGroup(jPanel3Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.jPanel4, -2, -1, -2));
    GroupLayout inboxTabPaneLayout = new GroupLayout(this.inboxTabPane);
    this.inboxTabPane.setLayout(inboxTabPaneLayout);
    inboxTabPaneLayout.setHorizontalGroup(inboxTabPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.inputAllPane2, -1, -1, 32767)
        .addComponent(this.jPanel3, -1, -1, 32767));
    inboxTabPaneLayout.setVerticalGroup(inboxTabPaneLayout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(inboxTabPaneLayout.createSequentialGroup()
          .addComponent(this.jPanel3, -2, -1, -2)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.inputAllPane2, -2, -1, -2)
          .addContainerGap(-1, 32767)));
    this.jTabbedPane1.addTab("Inbox", this.inboxTabPane);
    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);
    layout.setHorizontalGroup(layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
          .addComponent(this.jTabbedPane1, -2, 427, -2)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addComponent(this.sidebarPane, -2, -1, -2)));
    layout.setVerticalGroup(layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.sidebarPane, -1, -1, 32767)
        .addComponent(this.jTabbedPane1, GroupLayout.Alignment.TRAILING));
  }
  
  private void sendAllButtonActionPerformed(ActionEvent evt) {
    String messageInput = this.inputAllField.getText();
    if (messageInput.equals("")) {
      JOptionPane.showMessageDialog(this, "Please enter something to send");
    } else {
      sendMessage("PUBLIC::" + this.username + "::" + messageInput);
      this.inputAllField.setText("");
    } 
  }
  
  private void sendButtonActionPerformed(ActionEvent evt) {
    String receiver = this.friendChatLabel.getText();
    System.out.println("chat private with: " + receiver);
    if (receiver.equals("")) {
      JOptionPane.showMessageDialog(this, "Please choose someone to chat");
      return;
    } 
    String messageInput = this.inputPrivateField.getText();
    if (messageInput.equals("")) {
      JOptionPane.showMessageDialog(this, "Please enter something to send");
    } else {
      sendMessage("PRIVATE::" + this.username + "-->" + receiver + "::" + messageInput);
      this.inputPrivateField.setText("");
    } 
  }
  
  private void sendFileButtonActionPerformed(ActionEvent evt) {
    String receiver = this.friendChatLabel.getText();
    if (receiver.equals("")) {
      JOptionPane.showMessageDialog(this, "Please choose someone to chat");
      return;
    } 
    sendFileForm fileForm = new sendFileForm(this.socket, this.username, receiver);
    fileForm.setVisible(true);
  }
  
  private void tableFriendMouseClicked(MouseEvent evt) {
    this.selectedIndexFriendTable = this.tableFriend.getSelectedRow();
    this.jTabbedPane1.setSelectedIndex(1);
    String chatWith = this.onlineModel.getValueAt(this.selectedIndexFriendTable, 0).toString();
    if (chatWith.equals(this.username))
      return; 
    this.friendChatLabel.setText(chatWith);
    String mess = this.messPrivate.get(chatWith);
    if (mess == null) {
      mess = "";
      this.messPrivate.put(chatWith, mess);
    } 
    this.messPrivateArea.setEditable(true);
    this.messPrivateArea.setText(mess);
    this.messPrivateArea.setEditable(false);
  }
  
  private void newMessTableMouseClicked(MouseEvent evt) {
    this.selectedIndexFriendTable = this.newMessTable.getSelectedRow();
    this.jTabbedPane1.setSelectedIndex(1);
    String chatWith = this.newMessTable.getValueAt(this.selectedIndexFriendTable, 0).toString();
    if (chatWith.equals(this.username))
      return; 
    this.friendChatLabel.setText(chatWith);
    String mess = this.messPrivate.get(chatWith);
    if (mess == null) {
      mess = "";
      this.messPrivate.put(chatWith, mess);
    } 
    this.messPrivateArea.setEditable(true);
    this.messPrivateArea.setText(mess);
    this.messPrivateArea.setEditable(false);
    this.newMessList.remove(chatWith);
    updateNewChatTable();
  }
  
  private void fileTableMouseClicked(MouseEvent evt) {
    int selectIndex = this.fileTable.getSelectedRow();
    String sender = this.fileTable.getValueAt(selectIndex, 0).toString();
    String filename = this.fileTable.getValueAt(selectIndex, 1).toString();
    Object[] options = { "Download", "Remove", "Cancel" };
    int result = JOptionPane.showOptionDialog(this, "File: " + filename + " from " + sender + ". Do you want to download or remove from drive?", "Download Option", 1, 3, null, options, options[0]);
    if (result == 0) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setSelectedFile(new File(filename));
      int rVal = fileChooser.showSaveDialog(null);
      if (rVal == 0) {
        String filePath, fileToSave = fileChooser.getSelectedFile().getName();
        String dir = fileChooser.getCurrentDirectory().toString();
        if (fileToSave == null) {
          filePath = dir + "\\" + dir;
        } else {
          filePath = dir + "\\" + dir;
        } 
        byte[] fileBytes = this.drive.get(filename);
        File fileDownload = new File(filePath);
        try {
          FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
          fileOutputStream.write(fileBytes);
          fileOutputStream.close();
          JOptionPane.showMessageDialog(this, "Download Successful");
        } catch (FileNotFoundException e) {
          JOptionPane.showMessageDialog(this, "Download Fail");
        } catch (IOException e) {
          JOptionPane.showMessageDialog(this, "Download File Fail");
        } 
      } 
    } else if (result == 1) {
      int ret = JOptionPane.showConfirmDialog(this.jPanel1, "Are you sure to remove this file?", "Confirm Remove", 0);
      if (ret != 0)
        return; 
      this.drive.remove(filename);
      if (this.listFileInDrive.containsKey(sender))
        if (((ArrayList)this.listFileInDrive.get(sender)).size() > 1) {
          ((ArrayList)this.listFileInDrive.get(sender)).remove(filename);
        } else {
          this.listFileInDrive.remove(sender);
        }  
      updateDriveFileTable();
    } else if (result == 2) {
      System.out.println("cancel clicked");
      return;
    } 
  }
  
  private void closeButtonActionPerformed(ActionEvent evt) {
    int ret = JOptionPane.showConfirmDialog(this, "Do you want to cancel app?", "Confirm", 0);
    if (ret != 0)
      return; 
    System.exit(0);
  }
  
  public static void createAndShowGUI(Socket socket, String username, int port) throws IOException {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("Client Chatroom");
    frame.setDefaultCloseOperation(3);
    Home newContentPane = new Home(socket, username, port);
    newContentPane.setOpaque(true);
    frame.setContentPane(newContentPane);
    frame.pack();
    frame.setVisible(true);
    frame.setResizable(false);
  }
}
