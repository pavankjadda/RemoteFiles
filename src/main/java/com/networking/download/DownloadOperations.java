package com.networking.download;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import com.networking.config.RemoteHost;
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
            this.remoteOperationsUtil=new RemoteOperationsUtil(remoteHost);
            this.remoteHost=remoteHost;
        }

        catch (JSchException e)
        {
            e.printStackTrace();
        }
    }

    public void listFilesInDirectory(String directoryName)
    {

        Vector files;
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
                getTargetDataFromJsonFileAndRenameIt(localFile,localDirectory);
            }
        }

        catch (SftpException | IOException e)
        {
            e.printStackTrace();
        }

        System.out.println("Files Transfer Success");
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

    private void getTargetDataFromJsonFileAndRenameIt(File localFile,String localDirectory) throws IOException
    {
        byte[] mapByteData = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode =objectMapper.readTree(mapByteData);
        String sha256=rootNode.path("target").path("file").path("sha256").textValue();
        int threatScore=Math.round(rootNode.path("info").path("score").floatValue());
        String newFileName=localDirectory+"/"+sha256+"-"+threatScore+".json";
        localFile.renameTo(new File(newFileName));

        System.out.println("sha256 "+sha256);
    }

}
