package com.networking.home;

import com.jcraft.jsch.*;
import com.networking.config.RemoteHost;
import com.networking.util.LocalOperationsUtil;
import com.networking.util.RemoteOperationsUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class DiskSpaceMonitor
{
    static ChannelSftp channelSftp = null;

    public static void main(String[] args)
    {
        RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
        List<RemoteHost> remoteHostList=remoteOperationsUtil.getRemoteHostsDetails();
        for(RemoteHost remoteHost:remoteHostList)
        {
            Session session=getSession(remoteHost);
            //int diskUsagePercentage = getDiskUsage(session);
            //System.out.println(remoteHost.getIpAddress()+" Disk Usage: "+diskUsagePercentage);

            //session=getSession(remoteHost);
            //executeCommandOnRemoteMachine(session);
            //System.out.println("Cuckoo Process killed on "+session.getHost());

            //Start Cuckoo
            //stopCuckooOnRemoteMachine(session);
            startCuckooOnRemoteMachine(session,remoteHost);
            System.out.println("Cuckoo Process started on "+session.getHost());
        }
    }

    private static void startCuckooOnRemoteMachine(Session session, RemoteHost remoteHost)
    {
        ChannelExec channelExec=null;
        String stopCuckooCommand="kill -9 $(ps -ef | grep /bin/cuckoo | head -n 1 |awk '{print $2}')";
        String startCuckooCommand="source venv/bin/activate && cuckoo &";
        try
        {
            executeCommandOnRemoteMachine(session,stopCuckooCommand);
            System.out.println("Stopped Cuckoo on "+session.getHost());


            session=getSession(remoteHost);
            executeCommandOnRemoteMachine(session,startCuckooCommand);
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
        ChannelExec channelExec=null;
        String stopCuckooCommand="kill -9 $(ps -ef | grep /bin/cuckoo | head -n 1 |awk '{print $2}')";
        try
        {
            session.connect();
            executeCommandOnRemoteMachine(session,stopCuckooCommand);
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
            channelExec.connect();
            readCommandOutput(channelExec);
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


    private static void readCommandOutput(ChannelExec channelExec)
    {
        InputStream in = null;
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
                    String outputStr=new String(tmp, 0, i).trim();
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
            channelExec.setCommand("df / | awk '{print $5}' | tr -dc '0-9'");
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
        InputStream in = null;
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
