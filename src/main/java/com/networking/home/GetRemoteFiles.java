package com.networking.home;

import com.networking.delete.DeleteThread;
import com.networking.download.DownloadThread;
import com.networking.util.LocalOperationsUtil;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        // Start Delete Thread
        DownloadThread downloadThread=new DownloadThread("192.168.1.126","192.168.1.126");
        downloadThread.start();


        // Start Delete Thread
        DeleteThread deleteThread=new DeleteThread("192.168.1.126","192.168.1.126");
        deleteThread.start();

        // Move files
        LocalOperationsUtil localOperationsUtil=new LocalOperationsUtil();
        //localOperationsUtil.moveFiles("/home/cuckoo/Desktop/MalwareReports/","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares");
    }
}
