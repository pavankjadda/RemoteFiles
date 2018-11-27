package com.networking.util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.networking.config.RemoteHostProperties;

import java.util.List;
import java.util.Vector;


public class RemoteOperationsUtil
{
    private ChannelSftp channelSftp=null;
    public RemoteOperationsUtil()
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

    public void getDirectoryNumbers(Vector directories, List<Integer> reportsDirectoryNumbers)
    {
        for (Object directory : directories)
        {
            ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) directory;
            if(lsEntry.getAttrs().isDir() && !(lsEntry.getFilename().equals(".") || lsEntry.getFilename().equals("..")))
            {
                try
                {
                    reportsDirectoryNumbers.add(Integer.valueOf(lsEntry.getFilename()));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


}
