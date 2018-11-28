package com.networking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass
{
    public static void main(String[] args)
    {
        try
        {
            File file = new File("/home/cuckoo/.cuckoo/reports-backup/malwares");
            String destinationDirectory = "/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/";
            File[] fileList = file.listFiles();
            assert fileList != null;
            for (File fileEntry : fileList)
            {
                if (!fileEntry.isDirectory())
                {
                    Files.move(Paths.get(fileEntry.getAbsolutePath()), Paths.get(destinationDirectory + fileEntry.getName()));
                    System.out.println("File " + fileEntry.getAbsolutePath() + " moved to " + (destinationDirectory + fileEntry.getName()));
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void renameFilesWithThreatScore()
    {
        try
        {
            String localDirectory = "/media/cuckoo/VirusShare/Malware_JSON_Reports/malwares/";
            File file = new File(localDirectory);
            File[] fileList = file.listFiles();
            assert fileList != null;
            for (File fileEntry : fileList)
            {
                if (!fileEntry.isDirectory() && (threadScoreExistsInFileName(fileEntry.getName())))
                {
                    byte[] mapByteData = Files.readAllBytes(Paths.get(fileEntry.getAbsolutePath()));
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(mapByteData);
                    String sha256 = rootNode.path("target").path("file").path("sha256").textValue();
                    int threatScore = Math.round(rootNode.path("info").path("score").floatValue());
                    String newFileName = localDirectory + "/" + sha256 + "-" + threatScore + ".json";
                    System.out.println("new File Name: " + newFileName);
                    //fileEntry.renameTo(new File(newFileName));
                    System.out.println("threat score " + threatScore);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static boolean threadScoreExistsInFileName(String fileName)
    {
        final String regex = "[a-zA-Z0-9]+[\\-0-9]+.json";

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(fileName);

        if (matcher.find())
        {
            System.out.println("Full match: " + matcher.group(0));
            for (int i = 1; i <= matcher.groupCount(); i++)
            {
                System.out.println("Group " + i + ": " + matcher.group(i));
            }
            return true;
        }
        return false;
    }
}
