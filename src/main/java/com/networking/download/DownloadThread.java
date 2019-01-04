package com.networking.download;

import com.networking.config.RemoteHost;
import com.networking.util.LocalOperationsUtil;
import com.networking.util.RemoteOperationsUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DownloadThread implements Runnable
{
    private String threadName;
    private RemoteHost remoteHost;
    private Thread t=null;
    private LocalOperationsUtil localOperationsUtil;

    public DownloadThread(String threadName, String ipAddress)
    {
        this.threadName=threadName;
        this.localOperationsUtil=new LocalOperationsUtil();
        this.remoteHost=localOperationsUtil.getRemoteHost(ipAddress);
    }



    @Override
    public void run()
    {
        try
        {
            DownloadOperations downloadOperations= new DownloadOperations(remoteHost);
            System.out.println("Executing Thread: "+threadName + " inside DownloadThread");

            downloadOperations.copyReportsFromRemoteToLocalDirectory(remoteHost.getReportsDirectory(),"/home/cuckoo/Desktop/MalwareReports/");
            //downloadOperations.copyReportsFromLocalCuckooToLocalDirectory("/home/cuckoo/.cuckoo/storage/analyses/","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void start()
    {
        if(t == null)
        {
            t=new Thread(this,"DownloadThread-"+threadName);
            t.start();
        }
    }
}
