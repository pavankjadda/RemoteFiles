package com.networking.util;

import com.networking.config.RemoteHost;

public class UtilityThread implements Runnable
{
    private String threadName;
    private Thread t=null;
    private LocalOperationsUtil localOperationsUtil;


    public UtilityThread(String threadName)
    {
        this.threadName=threadName;
        this.localOperationsUtil=new LocalOperationsUtil();
    }


    @Override
    public void run()
    {
        localOperationsUtil.moveFiles("/home/cuckoo/Desktop/MalwareReports/","/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/");
    }


    public void start()
    {
        if(t == null)
        {
            t=new Thread(this,"MoveFilesThread-"+threadName);
            t.start();
        }
    }
}
