package socket;
 
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import gui.FileSizeString;
import pojo.Address;
import pojo.FILE;
 
public class Server implements Runnable{
	private long lensize = 0;
	private double current = 0;
	private int progress = 0;
	
	private int Port;
	private String filePath;
	private JTextArea textArea;
	private JProgressBar pb;
	private JLabel lblProcess;
	private JLabel lblSpeed;
	public Server(Address address,FILE FILE,JTextArea textArea,JProgressBar pb,JLabel lblProcess,JLabel lblSpeed) {
        this.Port = address.getPort();
        this.filePath = FILE.getFilePath();
        this.textArea = textArea;
        this.pb = pb;
        this.lblProcess = lblProcess;
        this.lblSpeed = lblSpeed;
    }
	
	public void run(){
		try {
			String hostAddress = new NetworkAddress().getLocalHostLANAddress();
			textArea.append("本机有以下局域网IP:\r\n" + hostAddress);
			
			ServerSocket ss = new ServerSocket(Port);
			textArea.append("监听端口号:" + Port + "中...\r\n");
			textArea.append("等待连接...\r\n");
			
			Socket s = ss.accept();
			textArea.append("获得连接!!!\r\n开始传输!!!\r\n");
			
			InputStream is = s.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			
			byte[] namelength = new byte[1];
			bis.read(namelength);
			byte[] name = new byte[namelength[0]];
			bis.read(name);
			
			String fileName = new String(name);
			filePath = filePath + fileName;
			
			byte[] lengthlength = new byte[1];
			bis.read(lengthlength);
			byte[] length = new byte[lengthlength[0]];
			bis.read(length);
			
			long fileSize =  Long.parseLong(new String(length));
			
			OutputStream os = new FileOutputStream(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			
			byte[] b = new byte[1024];
			int len = 0;
			
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
				bos.flush();
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
			bos.close();
			is.close();
			os.close();
			s.close();
			ss.close();
		} catch (BindException ev) {
			textArea.append("该端口已被占用!!!请更换传输端口\r\n");
		}catch (IOException ev) {
			ev.printStackTrace();
		}
 
    }
}