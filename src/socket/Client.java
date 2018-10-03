package socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import gui.FileSizeString;
import pojo.Address;
import pojo.FILE;

public class Client implements Runnable{
	private long lensize = 0;
	private double current = 0;
	private int progress = 0;
	
	private String IP;
	private int Port;
	private String filePath;
	private String fileName;
	private JTextArea textArea;
	private JProgressBar pb;
	private JLabel lblProcess;
	private JLabel lblSpeed;
	public Client(Address address,FILE FILE,JTextArea textArea,JProgressBar pb,JLabel lblProcess,JLabel lblSpeed) {
        this.IP = address.getIP();
        this.Port = address.getPort();
        this.filePath = FILE.getFilePath();
        this.fileName = FILE.getFileName();
        this.textArea = textArea;
        this.pb = pb;
        this.lblProcess = lblProcess;
        this.lblSpeed = lblSpeed;
    }
	
	public void run(){
		try {
			Socket s = new Socket(IP,Port);
			textArea.append("获得连接!!!\r\n开始传输!!!\r\n");
			OutputStream os = s.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);

			File file = new File(filePath);
			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);

			byte[] b = new byte[1024];
			int len = 0;
			
			byte[] fn = fileName.getBytes();
			byte[] name = new byte[fn.length+1];
			name[0] = (byte) fn.length;
			for(int i = 1;i<name.length;i++){
				name[i] = fn[i-1];
			}

			long fileSize = file.length();
			byte[] fl = (fileSize+"").getBytes();
			byte[] length = new byte[fl.length+1];
			length[0] = (byte)fl.length;
			for(int i = 1;i<length.length;i++){
				length[i] = fl[i-1];
			}
			
			bos.write(name);
			bos.write(length);
			

			lensize = 0;
			Thread getSize = new Thread() {
				public void run() {
					FileSizeString fss = new FileSizeString();
					String unit = fss.unit(fileSize);
					String fileSizeString = fss.number(fileSize, unit);
					String speed = "";
					long afterSize = 0;
					int time = 0;
					while (true) {
						if (lensize==fileSize) {
							textArea.append("传输总用时"+time+"s\r\n");
							textArea.append("传输平均速度"+fss.number(fileSize/time, fss.unit(fileSize/time)) + fss.unit(fileSize/time) + "/s\r\n");
							break;
						}
						try {
							speed = fss.number(lensize - afterSize, fss.unit(lensize - afterSize)) + fss.unit(lensize - afterSize) + "/s";
							afterSize = lensize;
							lblSpeed.setText(speed);
				            lblProcess.setText(fss.number(lensize, unit) + "/" + fileSizeString + unit);
				            time++;
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
					}
				}
			};
			getSize.start();
			
			
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
				lensize += len;
				current = (double) lensize / (double) fileSize;
	            progress = (int) (current * 100);
	            pb.setValue(progress);
			}
			
        	textArea.append("传输完成!!!\r\n");
			pb.setValue(0);
			lblSpeed.setText("");
            lblProcess.setText("");
			
			bis.close();
			is.close();
			bos.close();
			os.close();
			s.close();

		}catch (IOException ev) {
			textArea.append("\r\n***********************\r\n");
			textArea.append("连接错误!!!\r\n");
			textArea.append("请检查IP和端口是否正确!!!\r\n");
			textArea.append("请询问对方是否开启监听!!!\r\n");
			textArea.append("\r\n***********************\r\n");
		}
    }
}