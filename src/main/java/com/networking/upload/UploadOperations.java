package com.networking.upload;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.networking.config.RemoteHost;
import com.networking.util.RemoteOperationsUtil;

public class UploadOperations
{
    private ChannelSftp channelSftp=null;
    private RemoteOperationsUtil remoteOperationsUtil = null;
    private RemoteHost remoteHost=null;

    public UploadOperations(RemoteHost remoteHost)
    {
        JSch jSch=new JSch();
        try
        {
            Session session=jSch.getSession(remoteHost.getUsername(),remoteHost.getIpAddress());
            session.setPassword(remoteHost.getPassword());
            session.setConfig("StrictHostKeyChecking","no");
            session.connect();

            this.channelSftp= (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            this.remoteOperationsUtil=new RemoteOperationsUtil(remoteHost);
            this.remoteHost=remoteHost;
        }

        catch (JSchException e)
        {
            e.printStackTrace();
        }
    }



}
