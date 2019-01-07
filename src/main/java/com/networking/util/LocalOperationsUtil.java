package com.networking.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networking.config.RemoteHost;
import com.networking.constants.CuckooConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalOperationsUtil
{
    public LocalOperationsUtil()
    {

    }

    public void getLocalCuckooDirectoryNumbers(String localCuckooDirectory, List<Integer> reportsDirectoryNumbers)
    {
        File[] listFiles=new File(localCuckooDirectory).listFiles();
        assert listFiles != null;
        for(File file:listFiles)
        {
            try
            {
                if(file.isDirectory())
                    reportsDirectoryNumbers.add(Integer.valueOf(file.getName()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("Total Directories: "+reportsDirectoryNumbers.size());
    }

    public void getTargetDataFromJsonFileAndRenameIt(File localFile,String localDirectory) throws IOException
    {
        byte[] mapByteData = Files.readAllBytes(Paths.get(localFile.getAbsolutePath()));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode =objectMapper.readTree(mapByteData);
        String sha256=rootNode.path("target").path("file").path("sha256").textValue();
        int threatScore=Math.round(rootNode.path("info").path("score").floatValue());
        String newFileName=localDirectory+"/"+sha256+"-"+threatScore+".json";
        localFile.renameTo(new File(newFileName));

        //System.out.println("sha256 "+sha256);
    }

    public void getMalwareFileNamesFromLocalDirectory(String localMalwaresDirectory, List<String> malwareFileNamesFromMalwareDirectory)
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

    public void deleteLocalFile(List<String> malwareFileNamesFromMalwareDirectory, String malwareFileNameFromReport,String localMalwaresDirectory)
    {
        if (malwareFileNamesFromMalwareDirectory.contains(malwareFileNameFromReport))
            {
                new File(localMalwaresDirectory+malwareFileNameFromReport).delete();
                System.out.println("File deleted: " + malwareFileNameFromReport);
            }
    }

    public void moveFiles(String source, String destination)
    {
        try
        {
            File file = new File(source);
            File[] fileList = file.listFiles();
            assert fileList != null;
            for (File fileEntry : fileList)
            {
                if (!fileEntry.isDirectory())
                {
                    Files.move(Paths.get(fileEntry.getAbsolutePath()), Paths.get(destination + fileEntry.getName()), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("File " + fileEntry.getAbsolutePath() + " moved to " + (destination + fileEntry.getName()));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public RemoteHost getRemoteHost(String ipAddress)
    {
        RemoteOperationsUtil remoteOperationsUtil=new RemoteOperationsUtil();
        List<RemoteHost> remoteHosts=remoteOperationsUtil.getRemoteHostsDetails();
        AtomicReference<RemoteHost> remoteHostAtomicReference=new AtomicReference<>();
        for (RemoteHost remoteHostObject : remoteHosts)
        {
            if (remoteHostObject.getIpAddress().equals(ipAddress))
            {
                remoteHostAtomicReference.set(remoteHostObject);
            }
        }
        return remoteHostAtomicReference.get();
    }

    public void archiveExternalMalwaresFolder()
    {
        String externalMediaJsonReportsDirectory= CuckooConstants.externalMediaJsonReportsDirectory;

        File[] listFiles=new File(externalMediaJsonReportsDirectory).listFiles();
        assert listFiles != null;
        for(File file:listFiles)
        {
            try
            {
                if(file.isDirectory() && file.getName().equals("malwares"))
                {
                    int archiveSequence=getArchiveSequence(listFiles)+1;
                    compressDirectoryToTarFormat(file,CuckooConstants.externalMediaJsonReportsDirectory,archiveSequence);
                    if (file.delete())
                    {
                        file.mkdir();
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void compressDirectoryToTarFormat(File file, String externalMediaJsonReportsDirectory, int archiveSequence)
    {
        ProcessBuilder processBuilder = new ProcessBuilder();
        // Run a shell command
        processBuilder.directory(new File(externalMediaJsonReportsDirectory));
        processBuilder.command("bash", "-c", "tar -czvf malwares_json_reports_"+archiveSequence+".tar.xz malwares");

        try
        {
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            int exitVal = process.waitFor();
            if (exitVal == 0)
            {
                System.out.println("Success!");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private int getArchiveSequence(File[] listFiles)
    {
        int achieveSequence=0;
        for(File file:listFiles)
        {
            try
            {
                final String regex = "[\\d]";
                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(file.getName());

                if (matcher.find())
                {
                    System.out.println("Full match: " + matcher.group(0));
                    if(Integer.parseInt(matcher.group(0)) > achieveSequence)
                        achieveSequence=Integer.parseInt(matcher.group(0));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return achieveSequence;
    }
}

