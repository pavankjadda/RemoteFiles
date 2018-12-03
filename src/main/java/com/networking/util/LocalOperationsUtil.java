package com.networking.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
}

