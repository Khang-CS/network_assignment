package com.company;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

public class sendFileForm extends JFrame {
  private Socket socket;
  
  private DataInputStream dis;
  
  private DataOutputStream dos;
  
  private String username;
  
  private String receiver;
  
  private File file;
  
  private BufferedReader bufferedReader;
  
  private BufferedWriter bufferedWriter;
  
  private JButton chooseFileButton;
  
  private JLabel fileNameLabel;
  
  private JLabel jLabel1;
  
  private JLabel jLabel2;
  
  private JLabel jLabel3;
  
  private JPanel jPanel1;
  
  private JButton sendFileButton;
  
  private JLabel sizeFileLabel;
  
  public sendFileForm(Socket sket, String user, String rcver) {
    this.socket = sket;
    this.username = user;
    this.receiver = rcver;
    initComponents();
  }
  
  private void initComponents() {
    this.jPanel1 = new JPanel();
    this.jLabel1 = new JLabel();
    this.jLabel2 = new JLabel();
    this.jLabel3 = new JLabel();
    this.fileNameLabel = new JLabel();
    this.sizeFileLabel = new JLabel();
    this.chooseFileButton = new JButton();
    this.sendFileButton = new JButton();
    this.jLabel1.setText("Send File Form");
    this.jLabel2.setText("Filename: ");
    this.jLabel3.setText("Size: ");
    this.chooseFileButton.setText("Choose File");
    this.chooseFileButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            sendFileForm.this.chooseFileButtonActionPerformed(evt);
          }
        });
    this.sendFileButton.setText("Send File");
    this.sendFileButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            try {
              sendFileForm.this.sendFileButtonActionPerformed(evt);
            } catch (FileNotFoundException e) {
              e.printStackTrace();
            } 
          }
        });
    GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
    this.jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
          .addContainerGap(37, 32767)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(this.jLabel3)
                .addComponent(this.jLabel2))
              .addGap(18, 18, 18)
              .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.fileNameLabel, -1, -1, 32767)
                .addGroup(jPanel1Layout.createSequentialGroup()
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel1, -2, 86, -2)
                    .addComponent(this.sizeFileLabel, -2, 83, -2))
                  .addGap(0, 0, 32767))))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addGap(24, 24, 24)
              .addComponent(this.chooseFileButton)
              .addGap(18, 18, 18)
              .addComponent(this.sendFileButton)))
          .addGap(79, 79, 79)));
    jPanel1Layout.setVerticalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.jLabel1)
          .addGap(41, 41, 41)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.jLabel2)
            .addComponent(this.fileNameLabel))
          .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.jLabel3)
            .addComponent(this.sizeFileLabel, -2, 16, -2))
          .addGap(18, 18, 18)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.chooseFileButton)
            .addComponent(this.sendFileButton))
          .addContainerGap(14, 32767)));
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
  
  private void sendFileButtonActionPerformed(ActionEvent evt) throws FileNotFoundException {
    if (this.fileNameLabel.getText().equals("") || this.file == null) {
      JOptionPane.showMessageDialog(this, "Please choose a file to send");
      return;
    } 
    if (this.socket == null) {
      JOptionPane.showMessageDialog(this, "You are not connected to server");
      return;
    } 
    try {
      FileInputStream fileInputStream = new FileInputStream(this.file.getAbsoluteFile());
      DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
      String filename = this.file.getName();
      Long sizeFile = Long.valueOf(this.file.length());
      byte[] fileNameBytes = filename.getBytes();
      byte[] fileContentBytes = new byte[(int)this.file.length()];
      fileInputStream.read(fileContentBytes);
      dataOutputStream.writeUTF("FILE::" + this.username + "-->" + this.receiver);
      dataOutputStream.writeInt(fileNameBytes.length);
      dataOutputStream.write(fileNameBytes);
      dataOutputStream.writeInt(fileContentBytes.length);
      dataOutputStream.write(fileContentBytes);
      dataOutputStream.writeUTF("PRIVATE::" + this.username + "-->" + this.receiver + ":: has sent file " + filename);
      JOptionPane.showMessageDialog(this, "Send file successful");
      setVisible(false);
      dispose();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  private void chooseFileButtonActionPerformed(ActionEvent evt) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Choose File");
    int rVal = fileChooser.showOpenDialog(null);
    System.out.println("rVal: " + rVal);
    if (rVal == 0) {
      String filename = fileChooser.getSelectedFile().getName();
      String dir = fileChooser.getCurrentDirectory().toString();
      String filePath = dir + "\\" + dir;
      Long sizeFile = Long.valueOf(fileChooser.getSelectedFile().length());
      this.fileNameLabel.setText(filename);
      this.sizeFileLabel.setText(sizeFile.toString());
      this.file = fileChooser.getSelectedFile();
      try {
        JOptionPane.showMessageDialog(this, "Choose successful");
      } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Fail to choose");
      } 
    } 
  }
}
