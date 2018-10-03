package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import pojo.Address;
import pojo.FILE;
import socket.Client;
import socket.Server;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ClientGUI {
	private static JTextField textIP;
	private static JTextField textPort;
	private static JTextField textFile;

	private static File file;

	public static void main(String[] args) {
		JFrame f = new JFrame("文件传输-TCP");
		f.setSize(410, 350);
		f.setLocation(200, 200);
		f.getContentPane().setLayout(null);

		JButton btnSelect = new JButton("选择");
		btnSelect.setBounds(305, 66, 66, 23);
		f.getContentPane().add(btnSelect);

		JLabel lblIp = new JLabel("I  P：");
		lblIp.setBounds(24, 20, 36, 15);
		f.getContentPane().add(lblIp);

		JLabel lblPort = new JLabel("Port：");
		lblPort.setBounds(24, 45, 42, 15);
		f.getContentPane().add(lblPort);

		textIP = new JTextField();
		textIP.setBounds(62, 17, 194, 21);
		f.getContentPane().add(textIP);
		textIP.setColumns(10);

		textPort = new JTextField();
		textPort.setBounds(62, 42, 86, 21);
		f.getContentPane().add(textPort);
		textPort.setColumns(10);

		JLabel lblFile = new JLabel("文件：");
		lblFile.setBounds(24, 70, 54, 15);
		f.getContentPane().add(lblFile);

		textFile = new JTextField();
		textFile.setBounds(62, 67, 233, 21);
		f.getContentPane().add(textFile);
		textFile.setColumns(10);

		JButton btnUpload = new JButton("上传");
		btnUpload.setBounds(62, 98, 86, 32);
		f.getContentPane().add(btnUpload);

		JProgressBar pb = new JProgressBar();
		pb.setSize(346, 16);
		pb.setLocation(25, 140);
		pb.setMaximum(100);
		pb.setValue(0);
		pb.setStringPainted(true);
		f.getContentPane().add(pb);

		JLabel lblProcess = new JLabel("");
		lblProcess.setHorizontalAlignment(SwingConstants.RIGHT);
		lblProcess.setBounds(209, 158, 162, 15);
		f.getContentPane().add(lblProcess);

		JButton btnDownload = new JButton("接收");
		btnDownload.setBounds(209, 98, 86, 32);
		f.getContentPane().add(btnDownload);

		textIP.setText("127.0.0.1");
		textPort.setText("8888");

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(24, 177, 347, 124);
		f.getContentPane().add(scrollPane);

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setLineWrap(true);

		JLabel lblSpeed = new JLabel("");
		lblSpeed.setBounds(24, 158, 124, 15);
		f.getContentPane().add(lblSpeed);

		// 上传
		btnUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					Address address = new Address();
					FILE FILE = new FILE();
					try{
						if (file.isDirectory()){
							textArea.setText("请选择文件");
						}else{
						FILE.setFileName(file.getName());
						FILE.setFilePath(file.getAbsolutePath() + "\\");

						address.setIP(textIP.getText());
						address.setPort(Integer.parseInt(textPort.getText()));

						Client client = new Client(address, FILE, textArea, pb,lblProcess,lblSpeed);
						new Thread(client).start();
						}
					}catch (NullPointerException ev) {
						textArea.setText("请选择文件");
					}
				}
		});

		// 接收
		btnDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					Address address = new Address();
					FILE FILE = new FILE();
					try {
						if (file.isFile()){
							textArea.setText("请选择一个接收的文件夹!!!");
						}else{
							FILE.setFilePath(file.getAbsolutePath() + "\\");

							address.setPort(Integer.parseInt(textPort.getText()));

							Server server = new Server(address, FILE, textArea, pb,lblProcess,lblSpeed);
							new Thread(server).start();
						}
					} catch (NullPointerException ev) {
						textArea.setText("");
						textArea.setText("请选择一个接收的文件夹!!!");
					}
					
				}
		});

		// 选择文件
		btnSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jfc.showDialog(new JLabel(), "选择");
				file = jfc.getSelectedFile();
				if (file != null) {
					if (file.isDirectory()) {
						textArea.setText("");
						textArea.append("文件夹:" + file.getAbsolutePath() + "\r\n");
					} else if (file.isFile()) {
						textArea.setText("");
						textArea.append("文件:" + file.getAbsolutePath() + "\r\n");
						FileSizeString fss = new FileSizeString();
						Long fileSize = file.length();
						String unit = fss.unit(fileSize);
						lblProcess.setText(fss.number(fileSize, unit)+unit);
					}
					textFile.setText(file.getAbsolutePath());
				}
			}
		});

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		f.setVisible(true);

	}

	
}