package com.networking.download;

import com.networking.config.RemoteHost;
import com.networking.constants.CuckooConstants;
import com.networking.home.DiskSpaceMonitor;
import com.networking.util.LocalOperationsUtil;
import com.networking.util.RemoteOperationsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DownloadThread implements Runnable
{
    private String threadName;
    private RemoteHost remoteHost;
    private Thread t=null;

    private Logger logger= LoggerFactory.getLogger(DiskSpaceMonitor.class);

    public DownloadThread(String threadName, String ipAddress)
    {
        this.threadName=threadName;
        LocalOperationsUtil localOperationsUtil = new LocalOperationsUtil();
        this.remoteHost= localOperationsUtil.getRemoteHost(ipAddress);
    }

    @Override
    public void run()
    {
        try
        {

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
