package com.mttk.orche.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class NetworkIpUtil {

	//
	private static final Pattern IPV4_PATTERN = 
	        Pattern.compile(
	                "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
	 private static final Pattern IPV6_STD_PATTERN = 
		        Pattern.compile(
		                "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

		    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN = 
		        Pattern.compile(
		                "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");
		    
    
	 public static boolean isIPv4Address(final String input) {
	        return IPV4_PATTERN.matcher(input).matches();
	    }
	 public static boolean isIPv6StdAddress(final String input) {
	        return IPV6_STD_PATTERN.matcher(input).matches();
	    }

	    public static boolean isIPv6HexCompressedAddress(final String input) {
	        return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
	    }

	    public static boolean isIPv6Address(final String input) {
	        return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input); 
	    }
	    
	    /**
		 * 
		 * @param preferLocalAddress
		 * @return
		 * @throws UnknownHostException
		 */
		public static InetAddress getLocalHostLANAddress(boolean preferLocalAddress) throws UnknownHostException {
			try {
				InetAddress candidateAddress = null;
				// 遍历所有的网络接口
				for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
					NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
					if (iface.isLoopback()) {
						continue;
					}
					//System.out.println(iface);
					// 在所有的接口下再遍历IP
					for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
						InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
						if (inetAddr.isLoopbackAddress()) {
							//忽略lookback地址
							continue;
						}
						if (inetAddr.isLinkLocalAddress()) {
							//忽略lookback地址
							continue;
						}
						if (!NetworkIpUtil.isIPv4Address(inetAddr.getHostAddress())) {
							//忽略非IPv4地址
							continue;
						}
						
//						System.out.println("@@@" +inetAddr.isSiteLocalAddress()+":::"+ inetAddr.isLoopbackAddress()+":::"
//								+inetAddr.isLinkLocalAddress()+":::"+ inetAddr);
						if ((preferLocalAddress && inetAddr.isSiteLocalAddress())||
							(!preferLocalAddress && !inetAddr.isSiteLocalAddress())	) {
							// 如果是site-local地址，就是它了
							return inetAddr;
						} else if (candidateAddress == null) {
							// site-local类型的地址未被发现，先记录候选地址
							candidateAddress = inetAddr;
						}
					}

				}
				if (candidateAddress != null) {
					return candidateAddress;
				}
				// 如果没有发现 non-loopback地址.只能用最次选的方案
				InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
				if (jdkSuppliedAddress == null) {
					throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
				}
				return jdkSuppliedAddress;
			} catch (Exception e) {
				UnknownHostException unknownHostException = new UnknownHostException(
						"Failed to determine LAN address: " + e);
				unknownHostException.initCause(e);
				throw unknownHostException;
			}
		}
}
