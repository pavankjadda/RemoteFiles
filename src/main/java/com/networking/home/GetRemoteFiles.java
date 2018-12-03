package com.networking.home;

import com.networking.delete.DeleteThread;
import com.networking.download.DownloadThread;
import com.networking.util.LocalOperationsUtil;
import com.networking.util.UtilityThread;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        String ipAddress="192.168.1.126";
        String host="192.168.1.126";

        // Start Delete Thread
        DownloadThread downloadThread=new DownloadThread(host,ipAddress);
        downloadThread.start();


        // Start Delete Thread
        DeleteThread deleteThread=new DeleteThread(host,ipAddress);
        //deleteThread.start();

        // Move files
        UtilityThread utilityThread=new UtilityThread("192.168.1.125");
        utilityThread.start();
    }
}
