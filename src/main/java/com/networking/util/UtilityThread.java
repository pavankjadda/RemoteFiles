package com.networking.util;

import com.networking.constants.CuckooConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilityThread implements Runnable
{
    private String threadName;
    private Thread t=null;
    private LocalOperationsUtil localOperationsUtil;

    static private Logger logger= LoggerFactory.getLogger(UtilityThread.class);

    public UtilityThread(String threadName)
    {
        this.threadName=threadName;
        this.localOperationsUtil=new LocalOperationsUtil();
    }


    @Override
    public void run()
    {
        logger.info("Moving {} reports from {} to external disk: {} ",threadName,CuckooConstants.localMalwareReportsDirectory,CuckooConstants.externalMediaDirectory);
        localOperationsUtil.moveFiles(CuckooConstants.localMalwareReportsDirectory,CuckooConstants.externalMediaDirectory);
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
