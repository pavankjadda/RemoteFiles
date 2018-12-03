package com.networking.delete;

import com.networking.config.RemoteHost;
import com.networking.util.RemoteOperationsUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DeleteThread implements Runnable
{
    private String threadName;
    private RemoteHost remoteHost;
    private Thread t=null;

    public DeleteThread(String threadName, String ipAddress)
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
        DeleteOperations deleteOperation=new DeleteOperations(remoteHost);
        System.out.println("Executing Thread: "+threadName + " inside DeleteThread");

        //deleteOperation.deleteAnalyzedFilesFromLocalMalwareDirectory("/home/cuckoo/Desktop/VirusShare_00322/","/home/cuckoo/.cuckoo/storage/analyses/");
        deleteOperation.deleteAnalyzedFiles(remoteHost.getMalwareFilesDirectory(),remoteHost.getReportsDirectory());
    }


    public void start()
    {
        if(t == null)
        {
            t=new Thread(this,"DeleteThread-"+threadName);
            t.start();
        }
        t.start();
    }
}
