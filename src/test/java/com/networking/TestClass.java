package com.networking;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestClass
{
    public static void main(String[] args)
    {
        //RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
        //remoteOperationsUtil.moveFiles("/home/cuckoo/Desktop/MalwareReports","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/");
    }

    static void test() throws IOException
    {
        Socket socket = new Socket("192.168.1.120", 22);
        InputStream in = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = sslSocketFactory.createSocket(socket, socket.getLocalSocketAddress().toString(), socket.getPort(), true);

        outputStream = socket.getOutputStream();


        System.out.println(socket.isConnected());
        outputStream.write(10);

    }

}
