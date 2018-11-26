package com.networking.remote;

import com.jcraft.jsch.*;
import com.networking.config.RemoteHostProperties;

import java.util.Vector;
import com.jcraft.jsch.ChannelSftp.LsEntry;

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

            Vector<LsEntry> files=channelSftp.ls(RemoteHostProperties.targetDirectory);
            for(LsEntry lsEntry:files)
            {
                System.out.println(lsEntry.getFilename()+" is directory? "+lsEntry.getAttrs().isDir());
            }

        }

        catch (JSchException | SftpException e)
        {
            e.printStackTrace();
        }

    }
}
