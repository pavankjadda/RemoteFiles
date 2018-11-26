package com.networking.download;

import com.jcraft.jsch.*;
import com.networking.config.RemoteHostProperties;

import java.util.Vector;

public class DownloadOperations
{
    private ChannelSftp channelSftp=null;
    public DownloadOperations()
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

    public void listFilesInDirectory(String directoryName)
    {

        Vector files= null;
        try
        {
            files = channelSftp.ls(directoryName);
            for (Object file : files)
            {
                ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) file;
                System.out.println(lsEntry.getFilename() + " is directory? " + lsEntry.getAttrs().isDir());
            }
        }

        catch (SftpException e)
        {
            e.printStackTrace();
        }
    }


    public void getFile(String sourceFilePath,String destinationFilePath)
    {
        try
        {
            channelSftp.get(sourceFilePath,destinationFilePath);
            System.out.println("File transferred successfully");
        }

        catch (SftpException e)
        {
            e.printStackTrace();
        }
    }
}
