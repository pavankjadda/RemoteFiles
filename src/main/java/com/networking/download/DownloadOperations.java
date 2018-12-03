package com.networking.download;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import com.networking.config.RemoteHost;
import com.networking.util.LocalOperationsUtil;
import com.networking.util.RemoteOperationsUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class DownloadOperations
{
    private ChannelSftp channelSftp=null;
    private RemoteOperationsUtil remoteOperationsUtil=null;
    private RemoteHost remoteHost = null;
    private LocalOperationsUtil localOperationsUtil=null;

    public DownloadOperations(RemoteHost remoteHost)
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
            this.remoteOperationsUtil=new RemoteOperationsUtil();
            this.remoteHost=remoteHost;
            this.localOperationsUtil=new LocalOperationsUtil();
        }

        catch (JSchException e)
        {
            e.printStackTrace();
        }
    }



    public void copyReportsFromRemoteToLocalDirectory(String remoteDirectory,String localDirectory)
    {
        Vector directories;
        List<Integer> reportsDirectoryNumbers=new ArrayList<>();
        try
        {
            directories = channelSftp.ls(remoteDirectory);
            remoteOperationsUtil.getDirectoryNumbers(directories,reportsDirectoryNumbers);
            for(Integer directoryNumber:reportsDirectoryNumbers)
            {
                String remoteFilePath=remoteDirectory+directoryNumber+"/reports/report.json";
                String localFilePath=localDirectory+"report.json";
                File localFile=new File(localFilePath);
                if(localFile.exists())
                    localFile.delete();
                new File(localFilePath).createNewFile();
                InputStream in=channelSftp.get(remoteFilePath);
                Files.copy(in, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println(remoteFilePath+" File Copied to Location -> "+localFilePath);
                localOperationsUtil.getTargetDataFromJsonFileAndRenameIt(localFile,localDirectory);
            }
        }

        catch (SftpException | IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Files Transfer Success");
    }

    public void copyReportsFromLocalCuckooToLocalDirectory(String localCuckooDirectory,String localDirectory)
    {
        List<Integer> reportsDirectoryNumbers=new ArrayList<>();
        try
        {
            localOperationsUtil.getLocalCuckooDirectoryNumbers(localCuckooDirectory,reportsDirectoryNumbers);
            int i=1;
            for(Integer directoryNumber:reportsDirectoryNumbers)
            {
                String localCuckooFilePath=localCuckooDirectory+directoryNumber+"/reports/report.json";
                String localFilePath=localDirectory+"report.json";
                try
                {
                    Files.copy(Paths.get(localCuckooFilePath),Paths.get(localFilePath),StandardCopyOption.REPLACE_EXISTING);
                    localOperationsUtil.getTargetDataFromJsonFileAndRenameIt(new File(localFilePath),localDirectory);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                System.out.println((i++)+" Reports transferred");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("Files Transfer Success");
    }



}
