package com.networking.download;

import com.networking.config.RemoteHost;
import com.networking.util.RemoteOperationsUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DownloadThread implements Runnable
{
    private String threadName = null;
    private RemoteHost remoteHost = null;
    private Thread t=null;

    public DownloadThread(String threadName, String ipAddress)
    {
        this.threadName=threadName;
        this.remoteHost=getRemoteHost(ipAddress);
    }

    private RemoteHost getRemoteHost(String ipAddress)
    {
        RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
        List<RemoteHost> remoteHosts=remoteOperationsUtil.getRemoteHostsDetails();
        AtomicReference<RemoteHost> remoteHostAtomicReference=new AtomicReference<>();
        for (RemoteHost remoteHostObject : remoteHosts)
        {
            if (remoteHostObject.getIpAddress().equals(ipAddress))
            {
                remoteHostAtomicReference.set(remoteHostObject);
            }
        }
        return remoteHostAtomicReference.get();
    }

    @Override
    public void run()
    {
        DownloadOperations downloadOperations= new DownloadOperations(remoteHost);
        System.out.println("Executing Thread: "+threadName + " inside DownloadThread");

        //downloadOperations.copyReportsFromRemoteToLocalDirectory(remoteHost.getReportsDirectory(),"/home/cuckoo/Desktop/MalwareReports/");
        //downloadOperations.copyReportsFromLocalCuckooToLocalDirectory("/home/cuckoo/.cuckoo/storage/analyses/","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/");

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
