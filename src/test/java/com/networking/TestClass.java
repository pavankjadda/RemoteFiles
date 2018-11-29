package com.networking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networking.util.RemoteOperationsUtil;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass
{
    public static void main(String[] args)
    {
        try
        {
            Socket socket=new Socket("192.168.1.120",22);
            InputStream in=socket.getInputStream();
            OutputStream outputStream=socket.getOutputStream();

            SSLSocketFactory sslSocketFactory=(SSLSocketFactory) SSLSocketFactory.getDefault();
            socket=sslSocketFactory.createSocket(socket,socket.getLocalSocketAddress().toString(),socket.getPort(),true);

            outputStream=socket.getOutputStream();


            System.out.println(socket.isConnected());
            outputStream.write(10);

            //RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
            //remoteOperationsUtil.moveFiles("/home/cuckoo/Desktop/MalwareReports","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
