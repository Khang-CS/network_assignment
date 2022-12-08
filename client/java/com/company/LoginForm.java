package com.company;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class LoginForm extends JFrame {
  private DataOutputStream dataOutputStream;
  
  private DataInputStream dataInputStream;
  
  Socket socket;
  
  private int port;
  
  private JButton cancelButton;
  
  private JLabel jLabel1;
  
  private JLabel jLabel2;
  
  private JLabel jLabel3;
  
  private JPanel jPanel1;
  
  private JButton loginButton;
  
  private JTextField portField;
  
  private JButton registerButton;
  
  private JTextField userInputField;
  
  public LoginForm() throws IOException {
    initComponents();
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
    this.jPanel1 = new JPanel();
    this.jLabel1 = new JLabel();
    this.userInputField = new JTextField();
    this.jLabel2 = new JLabel();
    this.registerButton = new JButton();
    this.loginButton = new JButton();
    this.cancelButton = new JButton();
    this.jLabel3 = new JLabel();
    this.portField = new JTextField();
    setDefaultCloseOperation(3);
    this.jLabel1.setText("Username: ");
    this.jLabel2.setText("LOG IN");
    this.registerButton.setText("Register");
    this.registerButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (LoginForm.this.portField.getText().equals("")) {
              JOptionPane.showMessageDialog(LoginForm.this.jPanel1, "Please enter port number");
              return;
            } 
            try {
              LoginForm.this.port = Integer.parseInt(LoginForm.this.portField.getText());
            } catch (Exception e) {
              JOptionPane.showMessageDialog(LoginForm.this.jPanel1, "Port number is invalid");
              return;
            } 
            try {
              LoginForm.this.registerButtonActionPerformed(evt);
            } catch (IOException e) {
              e.printStackTrace();
            } 
          }
        });
    this.loginButton.setText("Login");
    this.loginButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (LoginForm.this.portField.getText().equals("")) {
              JOptionPane.showMessageDialog(LoginForm.this.jPanel1, "Please enter port number");
              return;
            } 
            try {
              LoginForm.this.port = Integer.parseInt(LoginForm.this.portField.getText());
            } catch (Exception e) {
              JOptionPane.showMessageDialog(LoginForm.this.jPanel1, "Port number is invalid");
              return;
            } 
            try {
              LoginForm.this.loginButtonActionPerformed(evt);
            } catch (IOException e) {
              e.printStackTrace();
            } 
          }
        });
    this.cancelButton.setText("Cancel");
    this.cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            LoginForm.this.cancelButtonActionPerformed(evt);
          }
        });
    this.jLabel3.setText("Port: ");
    this.portField.setText("3200");
    this.portField.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            LoginForm.this.portFieldActionPerformed(evt);
          }
        });
    GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
    this.jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
          .addContainerGap(56, 32767)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.registerButton)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.loginButton)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.cancelButton))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(this.jLabel3, -2, 37, -2)
                .addComponent(this.jLabel1, -2, 67, -2))
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.userInputField, -2, 154, -2)
                .addComponent(this.portField, -2, 63, -2))))
          .addGap(49, 49, 49))
        .addGroup(jPanel1Layout.createSequentialGroup()
          .addGap(137, 137, 137)
          .addComponent(this.jLabel2, -2, 46, -2)
          .addContainerGap(-1, 32767)));
    jPanel1Layout.setVerticalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.jLabel2)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 15, 32767)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.jLabel3)
            .addComponent(this.portField, -2, -1, -2))
          .addGap(18, 18, 18)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.jLabel1)
            .addComponent(this.userInputField, -2, -1, -2))
          .addGap(18, 18, 18)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.registerButton)
            .addComponent(this.loginButton)
            .addComponent(this.cancelButton))
          .addGap(37, 37, 37)));
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.jPanel1, -1, -1, 32767));
    layout.setVerticalGroup(layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.jPanel1, -1, -1, 32767));
    pack();
  }
  
  private void registerButtonActionPerformed(ActionEvent evt) throws IOException {
    if (String.valueOf(this.userInputField.getText()).equals("")) {
      JOptionPane.showMessageDialog(this, "Please Enter username");
    } else {
      Socket socket;
      String username = this.userInputField.getText().toString();
      try {
        socket = new Socket("localhost", this.port);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Fail to connect to server");
        return;
      } 
      this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
      this.dataInputStream = new DataInputStream(socket.getInputStream());
      boolean status = false;
      try {
        String mess = "REGISTER:" + username;
        this.dataOutputStream.writeUTF(mess);
        this.dataOutputStream.flush();
        System.out.println("mess login: " + mess);
        System.out.println("register port: " + socket.getPort());
        while (socket.isConnected()) {
          try {
            String response = this.dataInputStream.readUTF();
            if (response.contains("SERVER->REGISTER:")) {
              String tempUsername = response.split(":")[1];
              if (tempUsername.equals(username) && response.split(":")[2].equals("register successful")) {
                status = true;
                JOptionPane.showMessageDialog(this, "Register successful. You can login now.");
                break;
              } 
              if (tempUsername.equals(username) && response.split(":")[2].equals("user existed")) {
                status = false;
                JOptionPane.showMessageDialog(this, "This username is existed. Please try another one.");
                break;
              } 
            } 
          } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Fail to connect to server");
            closeEverything(socket, this.dataInputStream, this.dataOutputStream);
          } 
        } 
        if (socket != null)
          socket.close(); 
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Fail to connect to server");
        closeEverything(socket, this.dataInputStream, this.dataOutputStream);
      } 
    } 
  }
  
  private void loginButtonActionPerformed(ActionEvent evt) throws IOException {
    if (String.valueOf(this.userInputField.getText()).equals("")) {
      JOptionPane.showMessageDialog(this, "Please Enter Username");
    } else {
      Socket socket;
      String username = this.userInputField.getText().toString();
      try {
        socket = new Socket("localhost", this.port);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Fail to connect to server");
        return;
      } 
      this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
      this.dataInputStream = new DataInputStream(socket.getInputStream());
      boolean status = false;
      System.out.println("login port: " + socket.getPort());
      try {
        String mess = "LOGIN:" + username;
        this.dataOutputStream.writeUTF(mess);
        this.dataOutputStream.flush();
        while (socket.isConnected()) {
          try {
            String response = this.dataInputStream.readUTF();
            System.out.println("response: " + response);
            if (response.contains("SERVER->LOGIN:")) {
              String tempUsername = response.split(":")[1];
              if (tempUsername.equals(username) && response.split(":")[2].equals("accepted")) {
                status = true;
                break;
              } 
              if (tempUsername.equals(username) && response.split(":")[2].equals("not register")) {
                status = false;
                JOptionPane.showMessageDialog(this, "This username is not registered.");
                if (socket != null) {
                  System.out.println("close port login");
                  socket.close();
                } 
                break;
              } 
              if (tempUsername.equals(username) && response.split(":")[2].equals("now login")) {
                status = false;
                JOptionPane.showMessageDialog(this, "This username are now login");
                if (socket != null) {
                  System.out.println("close port login");
                  socket.close();
                } 
                break;
              } 
            } 
          } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Fail to connect to server");
            closeEverything(socket, this.dataInputStream, this.dataOutputStream);
          } 
        } 
      } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Fail to connect to server");
        closeEverything(socket, this.dataInputStream, this.dataOutputStream);
      } 
      if (status) {
        Home.createAndShowGUI(socket, username, this.port);
        setVisible(false);
        dispose();
      } 
    } 
  }
  
  private void cancelButtonActionPerformed(ActionEvent evt) {
    int ret = JOptionPane.showConfirmDialog(this, "Do you want to cancel app?", "Confirm", 0);
    if (ret != 0)
      return; 
    setVisible(false);
    dispose();
  }
  
  private void portFieldActionPerformed(ActionEvent evt) {}
  
  public static void main(String[] args) {
    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        } 
      } 
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, (String)null, ex);
    } catch (InstantiationException ex) {
      Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, (String)null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, (String)null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, (String)null, ex);
    } 
    EventQueue.invokeLater(new Runnable() {
          public void run() {
            try {
              (new LoginForm()).setVisible(true);
            } catch (IOException e) {
              e.printStackTrace();
            } 
          }
        });
  }
}
