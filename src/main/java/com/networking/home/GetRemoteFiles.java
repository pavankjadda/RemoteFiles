package com.networking.home;

import com.networking.delete.DeleteThread;
import com.networking.download.DownloadThread;
import com.networking.util.LocalOperationsUtil;
import com.networking.util.UtilityThread;

/**
 * Deprecated Class used to download, delete and move files between machines.
 * @see DiskSpaceMonitor class automated class
 *
 */
public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        String ipAddress="192.168.1.125";
        String threadName="192.168.1.125";

        // Start Delete Thread
        DownloadThread downloadThread=new DownloadThread(threadName,ipAddress);
        //downloadThread.start();


        // Start Delete Thread
        DeleteThread deleteThread=new DeleteThread(threadName,ipAddress);
        //deleteThread.start();

        // Move files

        UtilityThread utilityThread=new UtilityThread(threadName);
        //utilityThread.start();

        LocalOperationsUtil localOperationsUtil=new LocalOperationsUtil();
        //localOperationsUtil.archiveExternalMalwaresFolder();
    }
}
