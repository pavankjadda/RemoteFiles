package com.networking.download;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import com.networking.config.RemoteHostProperties;
import org.springframework.boot.json.JacksonJsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    public void copyFilesFromDirectory(String remoteDirectoryName,String localDirectoryName)
    {
        Vector directories;
        List<Integer> totalNumberOfReports=new ArrayList<>();
        try
        {
            directories = channelSftp.ls(remoteDirectoryName);
            getDirectoryNumbers(directories,totalNumberOfReports);

            for(Integer directoryNumber:totalNumberOfReports)
            {
                String remoteFilePath=remoteDirectoryName+directoryNumber+"/reports/report.json";
                String localFilePath=localDirectoryName+"report.json";
                File localFile=new File(localFilePath);
                if(localFile.exists())
                    localFile.delete();
                new File(localFilePath).createNewFile();
                InputStream in=channelSftp.get(remoteFilePath);
                Files.copy(in, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println(remoteFilePath+" File Copied to Location -> "+localFilePath);
                getTargetDataFromJsonFileAndRenameIt(localFile,localDirectoryName);
            }
            //channelSftp.get(remoteDirectoryName,localDirectoryName);
        }

        catch (SftpException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private void getDirectoryNumbers(Vector directories, List<Integer> totalNumberOfReports)
    {
        for (Object directory : directories)
        {
            ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) directory;
            if(lsEntry.getAttrs().isDir() && !(lsEntry.getFilename().equals(".") || lsEntry.getFilename().equals("..")))
            {
                System.out.println("Directory Name: "+lsEntry.getFilename());
                try
                {
                    totalNumberOfReports.add(Integer.valueOf(lsEntry.getFilename()));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
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

    private void getTargetDataFromJsonFileAndRenameIt(File localFile,String localDirectoryName) throws IOException
    {
        byte[] mapByteData = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode =objectMapper.readTree(mapByteData);
        String sha256=rootNode.path("target").path("file").path("sha256").textValue();
        int threatScore=Math.round(rootNode.path("info").path("score").floatValue());
        String newFileName=localDirectoryName+"/"+sha256+"-"+threatScore+".json";
        localFile.renameTo(new File(newFileName));

        System.out.println("sha256 "+sha256);
        System.out.println("threat score "+threatScore);
    }

}
