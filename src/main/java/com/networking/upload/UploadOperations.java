package com.networking.upload;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.networking.config.RemoteHostProperties;

public class UploadOperations
{
    private ChannelSftp channelSftp=null;
    public UploadOperations()
    {
        JSch jSch=new JSch();
        try
        {
            Session session=jSch.getSession(RemoteHostProperties.username,RemoteHostProperties.targetIpAddress);
            session.setPassword(RemoteHostProperties.password);
            session.setConfig("StrictHostKeyChecking","no");
            session.connect();

            this.channelSftp= (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

        }

        catch (JSchException e)
        {
            e.printStackTrace();
        }
    }



}
