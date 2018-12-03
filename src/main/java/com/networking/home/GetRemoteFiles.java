package com.networking.home;

import com.networking.delete.DeleteThread;
import com.networking.download.DownloadThread;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        //Start Delete Thread
        DownloadThread downloadThread=new DownloadThread("192.168.1.126","192.168.1.126");
        downloadThread.start();


        //Start Delete Thread
        DeleteThread deleteThread=new DeleteThread("192.168.1.126","192.168.1.126");
        deleteThread.start();
    }
}
