package com.networking.home;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.networking.config.RemoteHost;
import com.networking.constants.CuckooConstants;
import com.networking.delete.DeleteThread;
import com.networking.download.DownloadThread;
import com.networking.util.LocalOperationsUtil;
import com.networking.util.RemoteOperationsUtil;
import com.networking.util.UtilityThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DiskSpaceMonitor
{
    private static Logger logger= LoggerFactory.getLogger(DiskSpaceMonitor.class);

    public static void main(String[] args)
    {
        RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
        LocalOperationsUtil localOperationsUtil=new LocalOperationsUtil();
        List<RemoteHost> remoteHostList=remoteOperationsUtil.getRemoteHostsDetails();
        //noinspection InfiniteLoopStatement
        do
        {
            for (RemoteHost remoteHost : remoteHostList)
            {
                Session session = getSession(remoteHost);
                int diskUsagePercentage = getDiskUsage(session);
                logger.info("TimeStamp: " + LocalDateTime.now() + "  " + remoteHost.getIpAddress() + " Disk Usage: " + diskUsagePercentage);

                if (diskUsagePercentage > 60)
                {
                    logger.info(remoteHost.getIpAddress() + " Disk Usage greater than 90%,so starting clean up process ");
                    String ipAddress = remoteHost.getIpAddress();
                    String threadName = remoteHost.getIpAddress();

                    session = getSession(remoteHost);
                    stopCuckooOnRemoteMachine(session);

                    // Start Download Thread and copy files to local machine
                    DownloadThread downloadThread = new DownloadThread(threadName, ipAddress);
                    downloadThread.start();

                    // Start Delete Thread and delete analyzed files from Malware Folder
                    DeleteThread deleteThread = new DeleteThread(threadName, ipAddress);
                    deleteThread.start();

                    // Move malware reports from local machine to External Directory
                    UtilityThread utilityThread = new UtilityThread(threadName);
                    utilityThread.start();

                    localOperationsUtil.archiveExternalMalwaresFolder();

                    //Start Cuckoo again once everything is done
                    session = getSession(remoteHost);
                    startCuckooOnRemoteMachine(session, remoteHost);
                }

            }
            try
            {
                //Sleep for 5 minutes
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        } while (true);

    }

    private static void startCuckooOnRemoteMachine(Session session, RemoteHost remoteHost)
    {
        ChannelExec channelExec=null;
        try
        {
            executeCommandOnRemoteMachine(session, CuckooConstants.stopCuckooCommand);
            System.out.println("Stopped Cuckoo on "+session.getHost());
            session.disconnect();

            Thread.sleep(3000);
            session=getSession(remoteHost);
            executeCommandOnRemoteMachine(session,CuckooConstants.startCuckooCommand);
            System.out.println("Started Cuckoo on "+session.getHost());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            session.disconnect();
        }
    }


    private static void stopCuckooOnRemoteMachine(Session session)
    {
        try
        {
            executeCommandOnRemoteMachine(session,CuckooConstants.stopCuckooCommand);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            session.disconnect();
        }
    }


    private static void executeCommandOnRemoteMachine(Session session, String command)
    {
        ChannelExec channelExec=null;
        try
        {
            session.connect();
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            channelExec.setInputStream(System.in);
            channelExec.setOutputStream(System.out);
            InputStream in = channelExec.getInputStream();
            channelExec.connect();
            readCommandOutput(channelExec,in);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Objects.requireNonNull(channelExec).disconnect();
            session.disconnect();
        }

    }


    private static void readCommandOutput(ChannelExec channelExec, InputStream in)
    {
        try
        {
            byte[] tmp = new byte[1024];
            while (true)
            {
                while (in.available() > 0)
                {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    String outputStr=new String(tmp, 0, i).trim();
                    System.out.println("outputStr => "+outputStr);
                }
                if(channelExec.isClosed())
                {
                    if(in.available()>0)
                        continue;
                    return;
                }

                try
                {
                    Thread.sleep(1000);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    private static Session getSession(RemoteHost remoteHost)
    {
        JSch jSch = new JSch();
        Session session=null;
        try
        {
            session = jSch.getSession(remoteHost.getUsername(), remoteHost.getIpAddress());
            session.setPassword(remoteHost.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            return session;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return session;
    }

    private static int getDiskUsage(Session session)
    {
        ChannelExec channelExec=null;
        try
        {
            session.connect();
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(CuckooConstants.diskUsageCommand);
            channelExec.setInputStream(System.in);
            channelExec.setOutputStream(System.out);
            channelExec.connect();
            return getDiskUsagePercentage(channelExec);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Objects.requireNonNull(channelExec).disconnect();
            session.disconnect();
        }
        return 0;
    }


    private static int getDiskUsagePercentage(ChannelExec channelExec)
    {
        InputStream in;
        int disUsagePercentage = 0;
        try
        {
            in = channelExec.getInputStream();
            byte[] tmp = new byte[1024];
            while (true)
            {
                while (in.available() > 0)
                {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    disUsagePercentage = Integer.parseInt(new String(tmp, 0, i).trim());

                }
                if(channelExec.isClosed())
                {
                    if(in.available()>0)
                        continue;
                    return disUsagePercentage;
                }

                try
                {
                    Thread.sleep(1000);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
        return disUsagePercentage;
    }
}
