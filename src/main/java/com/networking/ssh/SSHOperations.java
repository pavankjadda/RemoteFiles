package com.networking.ssh;

import com.jcraft.jsch.*;
import com.networking.config.RemoteHost;
import com.networking.util.RemoteOperationsUtil;

import java.io.InputStream;

public class SSHOperations
{

    private Channel channel = null;

    public SSHOperations(RemoteHost remoteHost, String channelType)
    {
        JSch jSch = new JSch();
        try
        {
            Session session = jSch.getSession(remoteHost.getUsername(), remoteHost.getIpAddress());
            session.setPassword(remoteHost.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            this.channel = session.openChannel(channelType);
        }
        catch (JSchException e)
        {
            e.printStackTrace();
        }
    }

    public void OpenShell()
    {

        try
        {
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);

            channel.connect(30000);
        }
        catch (JSchException e)
        {
            e.printStackTrace();
        }
    }

    public void executeCommand(String command)
    {
        ((ChannelExec) channel).setCommand(command);
        try
        {
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();

            getDataFromCommand(in);

            channel.disconnect();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void getDataFromCommand(InputStream in)
    {
        byte[] tmp = new byte[1024];
        try
        {
            while (true)
            {
                while (in.available() > 0)
                {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed())
                {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
