package com.company;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
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

public class ServerHome extends JFrame {
  Server server;
  
  private JLabel jLabel1;
  
  private JLabel jLabel2;
  
  private JPanel jPanel1;
  
  private JTextField portField;
  
  private JButton startButton;
  
  private JButton stopButton;
  
  public ServerHome() {
    initComponents();
  }
  
  private void initComponents() {
    this.jPanel1 = new JPanel();
    this.jLabel1 = new JLabel();
    this.jLabel2 = new JLabel();
    this.portField = new JTextField();
    this.startButton = new JButton();
    this.stopButton = new JButton();
    setDefaultCloseOperation(3);
    this.jLabel1.setFont(new Font("Segoe UI", 0, 36));
    this.jLabel1.setText("CHAT SERVER");
    this.jLabel2.setText("PORT:");
    this.portField.setText("3200");
    this.portField.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            ServerHome.this.portFieldActionPerformed(evt);
          }
        });
    this.startButton.setText("Start");
    this.startButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            try {
              ServerHome.this.startButtonActionPerformed(evt);
            } catch (IOException e) {
              e.printStackTrace();
            } 
          }
        });
    this.stopButton.setText("Stop");
    this.stopButton.setEnabled(false);
    this.stopButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            ServerHome.this.stopButtonActionPerformed(evt);
          }
        });
    GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
    this.jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addGap(163, 163, 163)
              .addComponent(this.jLabel2, -2, 37, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.portField, -2, 59, -2))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addGap(151, 151, 151)
              .addComponent(this.startButton)
              .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
              .addComponent(this.stopButton)))
          .addContainerGap(-1, 32767))
        .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
          .addGap(0, 108, 32767)
          .addComponent(this.jLabel1)
          .addGap(85, 85, 85)));
    jPanel1Layout.setVerticalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.jLabel1, -2, 28, -2)
          .addGap(39, 39, 39)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.jLabel2)
            .addComponent(this.portField, -2, -1, -2))
          .addGap(35, 35, 35)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.startButton)
            .addComponent(this.stopButton))
          .addContainerGap(41, 32767)));
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.jPanel1, GroupLayout.Alignment.TRAILING, -1, -1, 32767));
    layout.setVerticalGroup(layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addComponent(this.jPanel1, -1, -1, 32767));
    pack();
  }
  
  private void portFieldActionPerformed(ActionEvent evt) {}
  
  private void startButtonActionPerformed(ActionEvent evt) throws IOException {
    if (this.startButton.isEnabled()) {
      int port;
      if (this.portField.getText().equals("")) {
        JOptionPane.showMessageDialog(this, "Please enter port number");
        return;
      } 
      try {
        port = Integer.parseInt(this.portField.getText());
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Invalid port number.");
        return;
      } 
      ServerSocket serverSocket = new ServerSocket(port);
      this.server = new Server(serverSocket);
      Thread t = new Thread(this.server);
      t.start();
      JOptionPane.showMessageDialog(this, "Start server successful");
      this.startButton.setEnabled(false);
      this.stopButton.setEnabled(true);
    } 
  }
  
  private void stopButtonActionPerformed(ActionEvent evt) {
    if (this.stopButton.isEnabled()) {
      int ret = JOptionPane.showConfirmDialog(this.jPanel1, "Are you sure to stop server", "Confirm ", 0);
      if (ret != 0)
        return; 
      this.server.stop();
      setVisible(false);
      dispose();
    } 
  }
  
  public static void main(String[] args) {
    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        } 
      } 
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(ServerHome.class.getName()).log(Level.SEVERE, (String)null, ex);
    } catch (InstantiationException ex) {
      Logger.getLogger(ServerHome.class.getName()).log(Level.SEVERE, (String)null, ex);
    } catch (IllegalAccessException ex) {
      Logger.getLogger(ServerHome.class.getName()).log(Level.SEVERE, (String)null, ex);
    } catch (UnsupportedLookAndFeelException ex) {
      Logger.getLogger(ServerHome.class.getName()).log(Level.SEVERE, (String)null, ex);
    } 
    EventQueue.invokeLater(new Runnable() {
          public void run() {
            (new ServerHome()).setVisible(true);
          }
        });
  }
}
