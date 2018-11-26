package com.networking.remote;

import com.jcraft.jsch.*;
import com.networking.config.RemoteHostProperties;

import java.util.Vector;

public class GetRemoteFiles
{
    public static void main(String[] args)
    {
        JSch jSch=new JSch();
        try
        {
            Session session=jSch.getSession(RemoteHostProperties.username,RemoteHostProperties.targetIpAddress);
            session.setPassword(RemoteHostProperties.password);
            session.setConfig("StrictHostKeyChecking","no");
            session.connect();

            ChannelSftp channelSftp= (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            Vector filesList=channelSftp.ls(RemoteHostProperties.targetDirectory);
            for(Object object: filesList)
            {
                System.out.println(object.toString());
            }
        }

        catch (JSchException | SftpException e)
        {
            e.printStackTrace();
        }

    }
}
