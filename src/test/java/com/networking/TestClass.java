package com.networking;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass
{
    public static void main(String[] args) throws IOException
    {
        File[] listFiles=new File("/media/cuckoo/VirusShare/Malware_JSON_Reports/").listFiles();
        assert listFiles != null;
        for(File fileEntry:listFiles)
        {
            if (!fileEntry.isDirectory() && !fileEntry.getName().equals("malwares_json_reports_1.tar.xz"))
            {
                String fileName=fileEntry.getName();
                String newFileName=changeName(fileName);

                Files.move(Paths.get(fileEntry.getAbsolutePath()), Paths.get("/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/"+newFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }

        //RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
        //remoteOperationsUtil.moveFiles("/home/cuckoo/Desktop/MalwareReports","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/");
    }

    private static String changeName(String fileName)
    {
        String pattern = "(?<=malwares)[0-9a-zA-Z-]+.json";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(fileName);
        if (m.find())
        {
            System.out.println("Found value: " + m.group(0));
        }
        else
        {
            System.out.println("NO MATCH");
        }
        return m.group(0);
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
