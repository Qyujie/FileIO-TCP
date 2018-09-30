package socket;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkAddress {
    // 所有网络接口
    public String getLocalHostLANAddress(){  
    	String hostAddressList = "";
        try {  
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();  
            while (allNetInterfaces.hasMoreElements()) {  
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();  

                // 去除回环接口，子接口，未运行和接口
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {  
                    continue;  
                }

                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {  
                    InetAddress ip = addresses.nextElement(); 
                    if (ip != null) {
                        // ipv4
                        if (ip instanceof Inet4Address) {
                        	// 内网 
                            if (ip.getHostAddress().startsWith("192") || ip.getHostAddress().startsWith("10")  
                                    || ip.getHostAddress().startsWith("172") || ip.getHostAddress().startsWith("169")) {  
                                hostAddressList += ip.getHostAddress() + "\r\n";
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            System.err.println("Error when getting host ip address"+ e.getMessage());
        }
        return hostAddressList;
    }
}
