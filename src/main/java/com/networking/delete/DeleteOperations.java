package com.networking.delete;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import com.networking.config.RemoteHost;
import com.networking.util.LocalOperationsUtil;
import com.networking.util.RemoteOperationsUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class DeleteOperations
{
    private ChannelSftp channelSftp = null;
    private RemoteOperationsUtil remoteOperationsUtil = null;
    private RemoteHost remoteHost=null;
    private LocalOperationsUtil localOperationsUtil=null;

    public DeleteOperations(RemoteHost remoteHost)
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

    /* Get Analyzed malware files and delete them from malwares list */
    public void deleteAnalyzedFiles(String remoteMalwaresDirectory, String remoteReportsDirectory)
    {
        Vector directories;
        List<Integer> reportsDirectoryNumbers = new ArrayList<>();
        List<String> malwareFileNamesFromMalwareDirectory = new ArrayList<>();

        /*  Get Malware File names from Malwares folder and store in List */
        getMalwareFileNamesFromRemoteMalwareDirectory(remoteMalwaresDirectory, malwareFileNamesFromMalwareDirectory);
        try
        {
            directories = channelSftp.ls(remoteReportsDirectory);
            /* Get directory numbers */
            remoteOperationsUtil.getDirectoryNumbers(directories, reportsDirectoryNumbers);
            System.out.println(" Number of Reports: " + reportsDirectoryNumbers.size());
            int i = 1;
            for (Integer directoryNumber : reportsDirectoryNumbers)
            {
                try
                {
                    String remoteFilePath = remoteReportsDirectory + directoryNumber + "/task.json";
                    //Get Malware filename from Cuckoo Report
                    String malwareFileNameFromReport = getFileNameFromTaskJsonFile(remoteFilePath);
                    if (isFileExistsInAnalyzedFiles(malwareFileNameFromReport, malwareFileNamesFromMalwareDirectory))
                    {
                        channelSftp.rm(remoteMalwaresDirectory + malwareFileNameFromReport);
                        System.out.println(i + " :File " + malwareFileNameFromReport + " present in " + remoteMalwaresDirectory + " directory, so deleting file");
                    }
                    else
                        System.out.println("Index :" + i);
                    i++;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("End of deleteAnalyzedFiles() method");
    }


    private boolean isFileExistsInAnalyzedFiles(String malwareFileName, List<String> malwareFileNamesFromMalwareDirectory)
    {
        return malwareFileNamesFromMalwareDirectory.contains(malwareFileName);
    }


    private void getMalwareFileNamesFromRemoteMalwareDirectory(String remoteMalwaresDirectory, List<String> malwareFileNamesFromMalwareDirectory)
    {
        Vector files;
        try
        {
            files = channelSftp.ls(remoteMalwaresDirectory);
            for (Object object : files)
            {
                ChannelSftp.LsEntry file = (ChannelSftp.LsEntry) object;
                if (!file.getAttrs().isDir())
                    malwareFileNamesFromMalwareDirectory.add(file.getFilename());
            }
        } catch (SftpException e)
        {
            e.printStackTrace();
        }
        System.out.println("Total Number of Malwares in Directory => " + malwareFileNamesFromMalwareDirectory.size());
    }



    private String getFileNameFromTaskJsonFile(String remoteFilePath) throws IOException
    {
        String localFilePath = remoteHost.getLocalTempFilePath();
        try
        {
            channelSftp.get(remoteFilePath, localFilePath);
        }

        catch (SftpException e)
        {
            e.printStackTrace();
        }
        byte[] mapByteData = Files.readAllBytes(Paths.get(localFilePath));
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(mapByteData);
        String remoteFileAbsolutePath=rootNode.path("target").textValue();
        String[] splitString=remoteFileAbsolutePath.split("/");
        return splitString[splitString.length-1];
    }



    /* Delete Malware files from Local Malware Directory*/
    public void deleteAnalyzedFilesFromLocalMalwareDirectory(String localMalwaresDirectory, String localCuckooDirectory)
    {
        List<String> malwareFileNamesFromMalwareDirectory=new ArrayList<>();
        localOperationsUtil.getMalwareFileNamesFromLocalDirectory(localMalwaresDirectory,malwareFileNamesFromMalwareDirectory);
        List<Integer> reportsDirectoryNumbers=new ArrayList<>();
        try
        {
            localOperationsUtil.getLocalCuckooDirectoryNumbers(localCuckooDirectory,reportsDirectoryNumbers);
            int i=1;
            for(Integer directoryNumber:reportsDirectoryNumbers)
            {
                String localTaskJsonFilePath=localCuckooDirectory+directoryNumber+"/task.json";
                String malwareFileNameFromReport=getMalwareNameFromLocalTaskJsonReport(localTaskJsonFilePath);
                localOperationsUtil.deleteLocalFile(malwareFileNamesFromMalwareDirectory,malwareFileNameFromReport,localMalwaresDirectory);
                i++;
            }
            System.out.println("Total Deleted Files: "+i);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("End of deleteAnalyzedFilesFromLocalMalwareDirectory() method");
    }





    private String getMalwareNameFromLocalTaskJsonReport(String fileAbsolutePath)
    {
        byte[] mapByteData;
        try
        {
            mapByteData = Files.readAllBytes(Paths.get(fileAbsolutePath));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(mapByteData);
            String remoteFileAbsolutePath = rootNode.path("target").textValue();
            String[] splitString = remoteFileAbsolutePath.split("/");
            return splitString[splitString.length - 1];
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
