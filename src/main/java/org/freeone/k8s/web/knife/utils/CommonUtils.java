package org.freeone.k8s.web.knife.utils;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
public class CommonUtils {

    @Value("${upload.path.windows}")
    private String uploadWindowsPath;
    private static String uploadWindowsPathStatic;

    @Value("${upload.path.linux}")
    private String uploadLinuxPath;
    private static String uploadLinuxPathStatic;

    @PostConstruct
    public void init(){
        uploadWindowsPathStatic = uploadWindowsPath;
        uploadLinuxPathStatic = uploadLinuxPath;
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static String getUploadPath() {
        if (isWindows()) {
            return uploadWindowsPathStatic;
        }else {
            return uploadLinuxPathStatic;
        }
    }

    public static Gson getGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static final  String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getFirstIpAddress() throws SocketException, UnknownHostException {
        List<String> list = new LinkedList<>();
        Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface network = (NetworkInterface) enumeration.nextElement();
            if (network.getName().equals("eth0")) {
                Enumeration addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address != null && (address instanceof Inet4Address  )) {
                        list.add(address.getHostAddress());
                    }
                }
            }

        }

        if(!list.isEmpty()) {
            for (String ip : list) {
                if (!"127.0.0.1".equals(ip) && !"0:0:0:0:0:0:0:1".equals(ip)) {
                    return ip;
                }
            }
        }

        InetAddress addr = InetAddress.getLocalHost();
//        System.out.println("Local HostAddress:   "+addr.getHostAddress());
//        String hostname = addr.getHostName();
//        System.out.println("Local host name: "+hostname);
        return addr.getHostAddress();
    }

    public static List<String> getIpAddress() throws SocketException {
        List<String> list = new LinkedList<>();
        Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface network = (NetworkInterface) enumeration.nextElement();
            Enumeration addresses = network.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = (InetAddress) addresses.nextElement();
                if (address != null && (address instanceof Inet4Address || address instanceof Inet6Address)) {
                    list.add(address.getHostAddress());
                }
            }
        }
        return list;
    }
    public static List<String> getIpV4Address() throws SocketException {
        List<String> list = new LinkedList<>();
        Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface network = (NetworkInterface) enumeration.nextElement();
            Enumeration addresses = network.getInetAddresses();
            System.out.println(network);
            while (addresses.hasMoreElements()) {
                InetAddress address = (InetAddress) addresses.nextElement();
                if (address != null && (address instanceof Inet4Address )) {

                    list.add(address.getHostAddress());
                }
            }
        }
        return list;
    }



    public static List<String> getIpAddress2() throws SocketException {
        List<String> list = new LinkedList<>();
        Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
        while (enumeration.hasMoreElements()) {
            NetworkInterface network = (NetworkInterface) enumeration.nextElement();
            if (network.isVirtual() || !network.isUp()) {
                continue;
            } else {
                Enumeration addresses = network.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address != null && (address instanceof Inet4Address || address instanceof Inet6Address)) {
                        list.add(address.getHostAddress());
                    }
                }
            }
        }


        return list;
    }

    public static final Date offsetDateTime2Date(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return Date.from(offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toInstant());
    }

    public static Thread copyAsync(InputStream in, OutputStream out) {
        Thread t =
                new Thread(
                        new Runnable() {
                            public void run() {
                                try {
                                    ByteStreams.copy(in, out);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
        t.start();
        return t;
    }
}
