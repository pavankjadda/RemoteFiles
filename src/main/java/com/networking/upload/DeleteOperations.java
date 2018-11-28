package com.networking.upload;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import com.networking.config.RemoteHost;
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
            this.remoteOperationsUtil=new RemoteOperationsUtil(remoteHost);
            this.remoteHost=remoteHost;
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
        getMalwareFileNamesFromMalwareDirectory(remoteMalwaresDirectory, malwareFileNamesFromMalwareDirectory);
        try
        {
            directories = channelSftp.ls(remoteReportsDirectory);
            /* Get directory numbers */
            remoteOperationsUtil.getDirectoryNumbers(directories, reportsDirectoryNumbers);
            System.out.println(" Number of Reports: " + reportsDirectoryNumbers.size());
            int i = 1;
            for (Integer directoryNumber : reportsDirectoryNumbers)
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

    private void getMalwareFileNamesFromMalwareDirectory(String remoteMalwaresDirectory, List<String> malwareFileNamesFromMalwareDirectory)
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


    private String getMalwareFileNameFromReport(String remoteFilePath) throws IOException
    {
        try
        {
            String localFilePath = remoteHost.getLocalTempFilePath();
            channelSftp.get(remoteFilePath, localFilePath);

            byte[] fileByteData = Files.readAllBytes(Paths.get(localFilePath));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(fileByteData);
            return rootNode.path("target").path("file").path("name").textValue();
        } catch (SftpException e)
        {
            e.printStackTrace();
        }
        return null;
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




    public void deleteAnalyzedFilesFromLocalCustomDirectory(String localMalwaresDirectory, String localReportsDirectory)
    {
        List<String> malwareFileNamesFromMalwareDirectory = new ArrayList<>();
        getMalwareFileNamesFromLocalDirectory(localMalwaresDirectory, malwareFileNamesFromMalwareDirectory);
        try
        {
            File file = new File(localReportsDirectory);
            File[] fileList = file.listFiles();
            assert fileList != null;
            int i = 0;
            for (File fileEntry : fileList)
            {
                if (!fileEntry.isDirectory())
                {
                    String malwareFileNameFromReport = getMalwareNameFromLocalReport(fileEntry.getAbsolutePath());
                    if (isFileExistsInAnalyzedFiles(malwareFileNameFromReport, malwareFileNamesFromMalwareDirectory))
                    {
                        //System.out.println((i++)+" :File "+malwareFileNameFromReport +" present in "+localMalwaresDirectory+" directory, so deleting file");
                        deleteFile(localMalwaresDirectory, malwareFileNameFromReport);
                    }
                    else
                        System.out.println("Index :" + (i++));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println("End of Local deleteAnalyzedFiles() method");
    }

    private void deleteFile(String localMalwaresDirectory, String malwareFileNameFromReport)
    {
        File file = new File(localMalwaresDirectory);
        for (File fileEntry : Objects.requireNonNull(file.listFiles()))
        {
            if (!fileEntry.isDirectory() && fileEntry.getName().equals(malwareFileNameFromReport))
            {
                fileEntry.delete();
                System.out.println("File deleted: " + malwareFileNameFromReport);

            }
        }
    }

    private void getMalwareFileNamesFromLocalDirectory(String localMalwaresDirectory, List<String> malwareFileNamesFromMalwareDirectory)
    {
        File file = new File(localMalwaresDirectory);
        File[] fileList = file.listFiles();
        assert fileList != null;
        for (File fileEntry : fileList)
        {
            if (!fileEntry.isDirectory())
            {
                malwareFileNamesFromMalwareDirectory.add(fileEntry.getName());
            }
        }
    }

    private String getMalwareNameFromLocalReport(String fileAbsolutePath)
    {
        String malwareFileNameFromReport = null;
        try
        {
            byte[] fileByteData = Files.readAllBytes(Paths.get(fileAbsolutePath));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(fileByteData);
            malwareFileNameFromReport = rootNode.path("target").path("file").path("name").textValue();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return malwareFileNameFromReport;
    }


}